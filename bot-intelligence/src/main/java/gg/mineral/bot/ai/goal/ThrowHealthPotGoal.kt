package gg.mineral.bot.ai.goal

import gg.mineral.bot.ai.goal.type.InventoryGoal
import gg.mineral.bot.api.controls.Key
import gg.mineral.bot.api.controls.MouseButton
import gg.mineral.bot.api.entity.ClientEntity
import gg.mineral.bot.api.entity.living.ClientLivingEntity
import gg.mineral.bot.api.event.Event
import gg.mineral.bot.api.instance.ClientInstance
import gg.mineral.bot.api.inv.item.Item
import gg.mineral.bot.api.inv.item.ItemStack
import gg.mineral.bot.api.math.trajectory.Trajectory
import gg.mineral.bot.api.math.trajectory.throwable.SplashPotionTrajectory
import gg.mineral.bot.api.world.ClientWorld
import gg.mineral.bot.api.world.block.Block
import kotlin.math.atan2

// TODO: support both regen and instant health potions
class ThrowHealthPotGoal(clientInstance: ClientInstance) : InventoryGoal(clientInstance) {
    private var lastPotTick = 0
    private var potting = false
    private var throwYaw = 0.0f

    private fun switchToPot() {
        var potSlot = -1
        val fakePlayer = clientInstance.fakePlayer
        val inventory = fakePlayer.inventory ?: return

        for (i in 0..35) {
            val itemStack = inventory.getItemStackAt(i) ?: continue
            val item = itemStack.item
            if (item.id == Item.POTION && itemStack.durability == 16421) {
                potSlot = i
                break
            }
        }

        if (potSlot > 8) return moveItemToHotbar(potSlot, inventory)

        if (inventoryOpen) {
            inventoryOpen = false
            pressKey(10, Key.Type.KEY_ESCAPE)
            return
        }

        pressKey(10, Key.Type.valueOf("KEY_" + (potSlot + 1)))
    }

    override fun shouldExecute(): Boolean {
        if (clientInstance.currentTick - lastPotTick < 20) return false

        val fakePlayer = clientInstance.fakePlayer
        val inventory = fakePlayer.inventory

        if (inventory == null || !inventory.contains { it: ItemStack ->
                it.item.id == Item.POTION && it.durability == 16421
            }) return false

        return potting || fakePlayer.health < 10
    }

    private fun angleAwayFromEnemies(): Float {
        val fakePlayer = clientInstance.fakePlayer
        val world = fakePlayer.world ?: return warn(this, "World is null").let { fakePlayer.yaw }

        val enemy = world.entities
            .minByOrNull {
                if (it is ClientLivingEntity && !clientInstance.configuration.friendlyUUIDs.contains(it.uuid))
                    it.distance3DTo(fakePlayer)
                else Double.MAX_VALUE
            } ?: return fakePlayer.yaw

        val angle = toDegrees(atan2(fakePlayer.z - enemy.z, fakePlayer.x - enemy.x)).toFloat()
        return angle
    }

    private fun distanceAwayFromEnemies(): Double {
        val fakePlayer = clientInstance.fakePlayer
        val world = fakePlayer.world ?: return warn(this, "World is null").let { Double.MAX_VALUE }

        return world.entities
            .minOfOrNull {
                if (it is ClientLivingEntity && !clientInstance.configuration.friendlyUUIDs.contains(it.uuid))
                    it.distance3DTo(fakePlayer)
                else Double.MAX_VALUE
            } ?: Double.MAX_VALUE
    }

    override fun onTick() {
        val fakePlayer = clientInstance.fakePlayer
        val world = fakePlayer.world ?: return

        val inventory = fakePlayer.inventory ?: return


        if (potting) {
            setMouseYaw(throwYaw)
            if (fakePlayer.health >= 10)
                potting = false
            return
        }
        // TODO: player motion prediction
        val collisionFunction = Trajectory.CollisionFunction { x1: Double, y1: Double, z1: Double ->

            if (hasHitEntity(world, x1, y1, z1) || hasHitBlock(world, x1, y1, z1)) {
                val distance: Double = fakePlayer.distance3DToSq(x1, y1, z1)

                if (distance < 16.0) {
                    val intensity = 1.0 - kotlin.math.sqrt(distance) / 4.0

                    if (intensity > clientInstance.configuration.potAccuracy.coerceAtMost(0.9)) return@CollisionFunction true
                }
            }

            false
        }

        val univariateOptimizer =
            clientInstance.univariateOptimizer({ SplashPotionTrajectory(world) }, { it.airTimeTicks }, 1000).`val`(
                fakePlayer.x, fakePlayer.y + fakePlayer.eyeHeight,
                fakePlayer.z, fakePlayer.yaw
            ).`var`(-90, 90).`val`(collisionFunction).build()

        val pitch = univariateOptimizer.minimize().toFloat()

        setMouseYaw(angleAwayFromEnemies())
        setMousePitch(pitch)

        val itemStack = inventory.heldItemStack

        if (itemStack == null || itemStack.item.id != Item.POTION || itemStack.durability != 16421) switchToPot()
        else if (distanceAwayFromEnemies() > 3.5) {
            potting = true
            throwYaw = fakePlayer.yaw
            lastPotTick = clientInstance.currentTick
            pressButton(10, MouseButton.Type.RIGHT_CLICK)
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

            return collisionBoundingBox != null && collisionBoundingBox.isVecInside(x, y, z)
        }

        return false
    }

    private fun hasHitEntity(world: ClientWorld?, x: Double, y: Double, z: Double) =
        world?.entities?.any { it: ClientEntity -> it.boundingBox?.isVecInside(x, y, z) == true } ?: false

    override fun onEvent(event: Event): Boolean {
        return false
    }

    public override fun onGameLoop() {
    }
}
