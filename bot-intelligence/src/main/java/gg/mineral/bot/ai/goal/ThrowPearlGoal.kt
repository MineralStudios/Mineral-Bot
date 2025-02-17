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
import gg.mineral.bot.api.math.trajectory.Trajectory.CollisionFunction
import gg.mineral.bot.api.math.trajectory.throwable.EnderPearlTrajectory
import gg.mineral.bot.api.util.MathUtil
import gg.mineral.bot.api.world.ClientWorld
import gg.mineral.bot.api.world.block.Block
import lombok.RequiredArgsConstructor
import org.apache.commons.math3.analysis.MultivariateFunction
import org.apache.commons.math3.analysis.UnivariateFunction
import org.apache.commons.math3.optim.InitialGuess
import org.apache.commons.math3.optim.MaxEval
import org.apache.commons.math3.optim.PointValuePair
import org.apache.commons.math3.optim.SimpleBounds
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType
import org.apache.commons.math3.optim.nonlinear.scalar.MultivariateOptimizer
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunction
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.BOBYQAOptimizer
import org.apache.commons.math3.optim.univariate.BrentOptimizer
import org.apache.commons.math3.optim.univariate.SearchInterval
import org.apache.commons.math3.optim.univariate.UnivariateObjectiveFunction
import kotlin.math.atan2


class ThrowPearlGoal(clientInstance: ClientInstance) : InventoryGoal(clientInstance) {
    private var lastPearledTick = 0
    private var type = Type.FORWARD

    @RequiredArgsConstructor
    private enum class Type : MathUtil {
        RETREAT {
            // TODO: retreat when opponent is agro
            override fun test(fakePlayer: FakePlayer, entity: ClientLivingEntity) = false
        },
        SIDE {
            override fun test(fakePlayer: FakePlayer, entity: ClientLivingEntity) =
                false /*fakePlayer.distance2DTo(entity.x, entity.z) in 3.6..6.0 && fakePlayer.isOnGround*/
        },
        FORWARD {
            override fun test(fakePlayer: FakePlayer, entity: ClientLivingEntity) =
                fakePlayer.distance3DTo(entity) > 6.0
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
            info(this, "Closing inventory after switching to pearl")
            return
        }

        pressKey(10, Key.Type.valueOf("KEY_" + (pearlSlot + 1)))
    }

    override fun shouldExecute(): Boolean {
        if (!canSeeEnemy() || clientInstance.currentTick - lastPearledTick < 20 * clientInstance.configuration.pearlCooldown) return false

        val fakePlayer = clientInstance.fakePlayer
        val inventory = fakePlayer.inventory

        if (inventory == null || !inventory.contains(Item.ENDER_PEARL)) return false

        val world = fakePlayer.world ?: return false

        val entity = world.entities.filterIsInstance<ClientPlayer>().minByOrNull {
            if (!clientInstance.configuration.friendlyUUIDs.contains(it.getUuid())
            ) fakePlayer.distance3DTo(it) else Double.MAX_VALUE
        } ?: return false

        for (t in Type.entries) if (t.test(fakePlayer, entity)) return fakePlayer.health > 16.0

        return false
    }

    override fun isExecuting() = inventoryOpen

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

        val entity = world.entities.filterIsInstance<ClientPlayer>().minByOrNull {
            if (!clientInstance.configuration.friendlyUUIDs.contains(it.getUuid())
            ) fakePlayer.distance3DTo(it) else Double.MAX_VALUE
        } ?: return

        for (type in Type.entries) {
            if (type.test(fakePlayer, entity)) {
                this.type = type
                break
            }
        }

        val targetX = entity.x + (entity.x - entity.lastX)
        val targetY = entity.y + (entity.y - entity.lastY)
        val targetZ = entity.z + (entity.z - entity.lastZ)

        val collisionFunction = when (type) {
            Type.FORWARD -> CollisionFunction { x1: Double, y1: Double, z1: Double ->
                abs(
                    x1
                            - targetX
                ) < 4 && abs(
                    y1
                            - targetY
                ) < 1 && abs(
                    z1
                            - targetZ
                ) < 4 && hasHitBlock(world, x1, y1, z1)
            }

            Type.RETREAT -> CollisionFunction { x1: Double, y1: Double, z1: Double ->
                hasHitBlock(
                    world,
                    x1,
                    y1,
                    z1
                )
            }

            Type.SIDE -> CollisionFunction { x1: Double, y1: Double, z1: Double ->
                val dX = abs(x1 - targetX)
                val dZ = abs(z1 - targetZ)

                sqrt(dX * dX + dZ * dZ) in 2.5..3.6 && hasHitBlock(
                    world,
                    x1,
                    y1,
                    z1
                )
            }
        }

        val angles = when (type) {
            Type.FORWARD -> {
                getAngles(fakePlayer, entity)
                /*val x = targetX - fakePlayer.x
                val z = targetZ - fakePlayer.z
                val yaw = if (z < 0.0 && x < 0.0) (90.0 + toDegrees(fastArcTan(z / x))).toFloat()
                else if (z < 0.0 && x > 0.0) (-90.0 + toDegrees(fastArcTan(z / x))).toFloat()
                else toDegrees(-fastArcTan(x / z)).toFloat()

                minimizePitch(fakePlayer, yaw, collisionFunction) {
                    it.airTimeTicks.toDouble()
                }*/
            }

            Type.RETREAT -> {
                optimizeAngles(GoalType.MAXIMIZE, fakePlayer, collisionFunction) {
                    it.distance3DToSq(
                        targetX,
                        targetY,
                        targetZ
                    )
                }
            }

            Type.SIDE -> {
                optimizeAngles(GoalType.MINIMIZE, fakePlayer, collisionFunction) {
                    it.airTimeTicks.toDouble()
                }
            }
        }

        setMouseYaw(angles[0])
        setMousePitch(angles[1])

        val itemStack = inventory.heldItemStack
        if (itemStack == null || itemStack.item.id != Item.ENDER_PEARL || inventoryOpen) switchToPearl()
        else {
            val trajectory = EnderPearlTrajectory(
                fakePlayer.world,
                fakePlayer.x,
                fakePlayer.y + fakePlayer.eyeHeight,
                fakePlayer.z,
                fakePlayer.yaw,
                fakePlayer.pitch,
                collisionFunction
            )

            if (trajectory.compute(1000) === Trajectory.Result.VALID) {
                lastPearledTick = clientInstance.currentTick

                pressButton(10, MouseButton.Type.RIGHT_CLICK)
            }
        }
    }

    private fun getAngles(player: ClientPlayer, entity: ClientPlayer): FloatArray {
        val xDelta: Double = (entity.x - entity.lastX) * 0.4
        val zDelta: Double = (entity.z - entity.lastZ) * 0.4
        var d: Double = player.distance3DTo(entity)
        d -= d % 0.8
        val xMulti: Double
        val zMulti: Double
        val sprint = entity.isSprinting()
        xMulti = d / 0.8 * xDelta * (if (sprint) 1.25 else 1.0)
        zMulti = d / 0.8 * zDelta * (if (sprint) 1.25 else 1.0)
        val x: Double = entity.x + xMulti - player.x
        val z: Double = entity.z + zMulti - player.z
        val y: Double = (player.y + player.eyeHeight
                - (entity.y + entity.eyeHeight))
        val dist: Double = player.distance3DTo(entity)
        val yaw = Math.toDegrees(atan2(z, x)).toFloat() - 90.0f
        val d1: Double = sqrt(x * x + z * z)
        val pitch = -(atan2(y, d1) * 180.0 / Math.PI).toFloat() + dist.toFloat() * 0.11f

        return floatArrayOf(yaw, -pitch)
    }

    private fun minimizePitch(
        fakePlayer: FakePlayer,
        yaw: Float,
        collisionFunction: CollisionFunction,
        valueFunction: (EnderPearlTrajectory) -> Double
    ): FloatArray {
        val objective = UnivariateFunction { pitch ->
            val simulator = EnderPearlTrajectory(
                fakePlayer.world,
                fakePlayer.x,
                fakePlayer.y + fakePlayer.eyeHeight,
                fakePlayer.z,
                yaw,
                pitch.toFloat(),
                collisionFunction
            )
            if (simulator.compute(1000) === Trajectory.Result.VALID) valueFunction.invoke(simulator) else Double.MAX_VALUE
        }

        val optimizer = BrentOptimizer(1e-10, 1e-14)

        val result = optimizer.optimize(
            MaxEval(1000),
            UnivariateObjectiveFunction(objective),
            GoalType.MINIMIZE,
            SearchInterval(-60.0, 60.0)
        )

        return floatArrayOf(yaw, result.point.toFloat())
    }

    private fun optimizeAngles(
        goalType: GoalType,
        fakePlayer: FakePlayer,
        collisionFunction: CollisionFunction,
        valueFunction: (EnderPearlTrajectory) -> Double
    ): FloatArray {
        val objective = MultivariateFunction { angles ->
            val simulator = EnderPearlTrajectory(
                fakePlayer.world,
                fakePlayer.x,
                fakePlayer.y + fakePlayer.eyeHeight,
                fakePlayer.z,
                angles[0].toFloat(),
                angles[1].toFloat(),
                collisionFunction
            )
            if (simulator.compute(1000) === Trajectory.Result.VALID) valueFunction.invoke(simulator) else if (goalType == GoalType.MINIMIZE) Double.MAX_VALUE else Double.MIN_VALUE
        }

        val optimizer: MultivariateOptimizer = BOBYQAOptimizer(4)

        val lowerBounds = doubleArrayOf(-180.0, -60.0)
        val upperBounds = doubleArrayOf(180.0, 60.0)

        fun wrapAngleTo180_float(par0: Float): Float {
            var par0 = par0
            par0 %= 360.0f

            if (par0 >= 180.0f) {
                par0 -= 360.0f
            }

            if (par0 < -180.0f) {
                par0 += 360.0f
            }

            return par0
        }

        val initialGuess = doubleArrayOf(
            wrapAngleTo180_float(fakePlayer.yaw).toDouble(),
            wrapAngleTo180_float(fakePlayer.pitch).toDouble()
        )

        val optimum: PointValuePair = optimizer.optimize(
            MaxEval(1000),
            ObjectiveFunction(objective),
            goalType,
            InitialGuess(initialGuess),
            SimpleBounds(lowerBounds, upperBounds)
        )

        return floatArrayOf(optimum.point[0].toFloat(), optimum.point[1].toFloat())
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
