package gg.mineral.bot.ai.goal

import gg.mineral.bot.ai.goal.type.InventoryGoal
import gg.mineral.bot.api.controls.Key
import gg.mineral.bot.api.controls.MouseButton
import gg.mineral.bot.api.entity.living.ClientLivingEntity
import gg.mineral.bot.api.entity.living.player.ClientPlayer
import gg.mineral.bot.api.entity.living.player.FakePlayer
import gg.mineral.bot.api.event.Event
import gg.mineral.bot.api.instance.ClientInstance
import gg.mineral.bot.api.inv.item.Item
import gg.mineral.bot.api.math.trajectory.Trajectory
import gg.mineral.bot.api.math.trajectory.throwable.EnderPearlTrajectory
import gg.mineral.bot.api.util.MathUtil
import gg.mineral.bot.api.world.ClientWorld
import gg.mineral.bot.api.world.block.Block
import lombok.RequiredArgsConstructor

class ThrowPearlGoal(clientInstance: ClientInstance) : InventoryGoal(clientInstance) {
    private var lastPearledTick = 0

    @RequiredArgsConstructor
    private enum class Type : MathUtil {
        RETREAT {
            // TODO: retreat when opponent is agro
            override fun test(fakePlayer: FakePlayer, entity: ClientLivingEntity) = false
        },
        SIDE {
            override fun test(fakePlayer: FakePlayer, entity: ClientLivingEntity) =
                fakePlayer.distance3DTo(entity) > 3.0 && !fakePlayer.isOnGround
        },
        FORWARD {
            override fun test(fakePlayer: FakePlayer, entity: ClientLivingEntity) =
                fakePlayer.distance3DTo(entity) > 16.0
        };

        abstract fun test(fakePlayer: FakePlayer, entity: ClientLivingEntity): Boolean
    }

    private fun switchToPearl() {
        var pearlSlot = -1
        val fakePlayer = clientInstance.fakePlayer
        val inventory = fakePlayer.inventory ?: return

        // Search hotbar
        for (i in 0..35) {
            val itemStack = inventory.getItemStackAt(i) ?: continue
            val item = itemStack.item
            if (item.id == Item.ENDER_PEARL) {
                pearlSlot = i
                break
            }
        }

        if (pearlSlot > 8) {
            moveItemToHotbar(pearlSlot, inventory)
            return
        }

        if (inventoryOpen) {
            inventoryOpen = false
            pressKey(10, Key.Type.KEY_ESCAPE)
            return
        }

        pressKey(10, Key.Type.valueOf("KEY_" + (pearlSlot + 1)))
    }

    // TODO: ender pearl cooldown
    override fun shouldExecute(): Boolean {
        if (clientInstance.currentTick - lastPearledTick < 20 || !canSeeEnemy() || clientInstance.currentTick - lastPearledTick < 20 * clientInstance.configuration.pearlCooldown) return false

        lastPearledTick = clientInstance.currentTick

        val fakePlayer = clientInstance.fakePlayer
        val inventory = fakePlayer.inventory

        if (inventory == null || !inventory.contains(Item.ENDER_PEARL)) return false

        val world = fakePlayer.world ?: return false

        for (entity in world.entities) if (entity is ClientPlayer
            && !clientInstance.configuration.friendlyUUIDs.contains(entity.getUuid())
        ) for (t in Type.entries) if (t.test(fakePlayer, entity)) return true

        return false
    }

    private fun canSeeEnemy(): Boolean {
        val fakePlayer = clientInstance.fakePlayer
        val world = fakePlayer.world
        return world != null && world.entities.any {
            !clientInstance.configuration.friendlyUUIDs
                .contains(it.uuid)
        }
    }

    override fun onTick() {
        val fakePlayer = clientInstance.fakePlayer
        val world = fakePlayer.world ?: return

        val inventory = fakePlayer.inventory ?: return

        for (entity in world.entities) {
            if (entity is ClientPlayer
                && !clientInstance.configuration.friendlyUUIDs.contains(entity.getUuid())
            ) {
                for (type in Type.entries) {
                    if (type.test(fakePlayer, entity)) {
                        val targetX = entity.getX()
                        val targetY = entity.getY()
                        val targetZ = entity.getZ()

                        val collisionFunction = when (type) {
                            Type.FORWARD -> Trajectory.CollisionFunction { x1: Double, y1: Double, z1: Double ->
                                floor(
                                    x1
                                ) == floor(targetX) && floor(y1) == floor(targetY) && floor(z1) == floor(targetZ)
                            }

                            Type.RETREAT -> Trajectory.CollisionFunction { x1: Double, y1: Double, z1: Double ->
                                hasHitBlock(
                                    world,
                                    x1,
                                    y1,
                                    z1
                                )
                            }

                            Type.SIDE -> Trajectory.CollisionFunction { x1: Double, y1: Double, z1: Double ->
                                hypot(
                                    x1 - targetX,
                                    z1 - targetZ
                                ) > 3 && hasHitBlock(world, x1, y1, z1)
                            }
                        }

                        val angles = when (type) {
                            Type.FORWARD -> {
                                val x = targetX - fakePlayer.x
                                val z = targetZ - fakePlayer.z
                                val yaw = if (z < 0.0 && x < 0.0) (90.0 + toDegrees(fastArcTan(z / x))).toFloat()
                                else if (z < 0.0 && x > 0.0) (-90.0 + toDegrees(fastArcTan(z / x))).toFloat()
                                else toDegrees(-fastArcTan(x / z)).toFloat()

                                val optimizer = clientInstance.univariateOptimizer(
                                    { EnderPearlTrajectory(world) },
                                    { it.airTimeTicks },
                                    1000
                                )
                                    .`val`(
                                        fakePlayer.x, fakePlayer.y + fakePlayer.eyeHeight,
                                        fakePlayer.z, yaw
                                    )
                                    .`var`(-90, 90).`val`(collisionFunction).build()

                                val pitch = optimizer.minimize().toFloat()

                                floatArrayOf(yaw, pitch)
                            }

                            Type.RETREAT -> {
                                val optimizer = clientInstance
                                    .bivariateOptimizer(
                                        { EnderPearlTrajectory(world) },
                                        {
                                            it.distance3DToSq(
                                                targetX,
                                                targetY,
                                                targetZ
                                            )
                                        },
                                        1000
                                    )
                                    .`val`(
                                        fakePlayer.x, fakePlayer.y + fakePlayer.eyeHeight,
                                        fakePlayer.z
                                    )
                                    .`var`(-180, 180)
                                    .`var`(-90, 90).`val`(collisionFunction).build()

                                val result = optimizer.maximize()
                                val yaw = result[0].toFloat()
                                val pitch = result[1].toFloat()

                                floatArrayOf(yaw, pitch)
                            }

                            Type.SIDE -> {
                                val optimizer = clientInstance
                                    .bivariateOptimizer(
                                        { EnderPearlTrajectory(world) },
                                        {
                                            abs(
                                                it.distance2DToSq(targetX, targetZ)
                                                        - 3.5
                                            )
                                        },
                                        1000
                                    )
                                    .`val`(
                                        fakePlayer.x, fakePlayer.y + fakePlayer.eyeHeight,
                                        fakePlayer.z
                                    )
                                    .`var`(-180, 180)
                                    .`var`(-90, 90).`val`(collisionFunction).build()

                                val result = optimizer.minimize()
                                val yaw = result[0].toFloat()
                                val pitch = result[1].toFloat()

                                floatArrayOf(yaw, pitch)
                            }
                        }

                        setMouseYaw(angles[0])
                        setMousePitch(angles[1])

                        val itemStack = inventory.heldItemStack

                        if (itemStack == null || itemStack.item.id != Item.ENDER_PEARL) switchToPearl()
                        else pressButton(10, MouseButton.Type.RIGHT_CLICK)
                        break
                    }
                }
                break
            }
        }
    }

    private fun hasHitBlock(world: ClientWorld?, x: Double, y: Double, z: Double): Boolean {
        val xTile = floor(x)
        val yTile = floor(y)
        val zTile = floor(z)
        val block = world?.getBlockAt(xTile, yTile, zTile)

        if (world != null && block != null && block.id != Block.AIR) {
            val collisionBoundingBox = block.getCollisionBoundingBox(
                world,
                xTile,
                yTile, zTile
            )

            return collisionBoundingBox?.isVecInside(x, y, z) == true
        }

        return false
    }

    override fun onEvent(event: Event) = false

    override fun onGameLoop() {
    }
}
