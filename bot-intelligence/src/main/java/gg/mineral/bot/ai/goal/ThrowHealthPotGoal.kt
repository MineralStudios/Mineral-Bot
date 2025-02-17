package gg.mineral.bot.ai.goal

import gg.mineral.bot.ai.goal.type.InventoryGoal
import gg.mineral.bot.api.controls.Key
import gg.mineral.bot.api.controls.MouseButton
import gg.mineral.bot.api.entity.living.ClientLivingEntity
import gg.mineral.bot.api.entity.living.player.ClientPlayer
import gg.mineral.bot.api.entity.living.player.FakePlayer
import gg.mineral.bot.api.entity.throwable.ClientThrowableEntity
import gg.mineral.bot.api.event.Event
import gg.mineral.bot.api.event.entity.EntityDestroyEvent
import gg.mineral.bot.api.instance.ClientInstance
import gg.mineral.bot.api.inv.item.Item
import gg.mineral.bot.api.inv.item.ItemStack
import gg.mineral.bot.api.math.simulation.PlayerMotionSimulator
import gg.mineral.bot.api.math.trajectory.Trajectory
import gg.mineral.bot.api.math.trajectory.throwable.SplashPotionTrajectory
import gg.mineral.bot.api.world.ClientWorld
import gg.mineral.bot.api.world.block.Block
import org.apache.commons.math3.analysis.UnivariateFunction
import org.apache.commons.math3.optim.MaxEval
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType
import org.apache.commons.math3.optim.univariate.BrentOptimizer
import org.apache.commons.math3.optim.univariate.SearchInterval
import org.apache.commons.math3.optim.univariate.UnivariateObjectiveFunction

class ThrowHealthPotGoal(clientInstance: ClientInstance) : InventoryGoal(clientInstance) {
    private var lastPotTick = 0
    private var pottingTicks = 0
    private var thrownYaw = 0f

    private var distanceFromEnemy = Double.MAX_VALUE

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

        return fakePlayer.health < 12 && (fakePlayer.health < 5 || distanceAwayFromEnemies() > 3.8)
    }

    override fun isExecuting(): Boolean {
        return pottingTicks > 0 || inventoryOpen
    }

    private fun angleAwayFromEnemies(): Float {
        val fakePlayer = clientInstance.fakePlayer
        val world = fakePlayer.world ?: return logger.debug( "World is null").let { fakePlayer.yaw }

        val enemy = world.entities
            .minByOrNull {
                if (it is ClientLivingEntity && !clientInstance.configuration.friendlyUUIDs.contains(it.uuid))
                    it.distance3DTo(fakePlayer)
                else Double.MAX_VALUE
            } ?: return fakePlayer.yaw
        val x: Double = enemy.x - fakePlayer.x
        val z: Double = enemy.z - fakePlayer.z

        var yaw = Math.toDegrees(-fastArcTan(x / z)).toFloat()
        if (z < 0.0 && x < 0.0) yaw = (90.0 + Math.toDegrees(fastArcTan(z / x))).toFloat()
        else if (z < 0.0 && x > 0.0) yaw = (-90.0 + Math.toDegrees(fastArcTan(z / x))).toFloat()
        return yaw + 180.0f
    }

    private fun distanceAwayFromEnemies(): Double {
        val fakePlayer = clientInstance.fakePlayer
        val world = fakePlayer.world ?: return logger.debug( "World is null").let { Double.MAX_VALUE }

        return world.entities
            .minOfOrNull {
                if (it is ClientLivingEntity && !clientInstance.configuration.friendlyUUIDs.contains(it.uuid))
                    it.distance2DTo(fakePlayer.x, fakePlayer.z)
                else Double.MAX_VALUE
            } ?: Double.MAX_VALUE
    }

    private fun closestEnemy(): ClientPlayer? {
        val fakePlayer = clientInstance.fakePlayer
        val world = fakePlayer.world ?: return null
        val targetSearchRange = clientInstance.configuration.targetSearchRange
        var bestTarget: ClientPlayer? = null
        var closestDistance = Double.MAX_VALUE

        for (entity in world.entities) {
            if (entity is ClientPlayer &&
                !clientInstance.configuration.friendlyUUIDs.contains(entity.uuid)
            ) {
                val distance = fakePlayer.distance3DTo(entity)
                if (distance <= targetSearchRange && distance < closestDistance) {
                    bestTarget = entity
                    closestDistance = distance
                }
            }
        }
        return bestTarget
    }

    // Extension functions for adjusting the bot’s aim.
    private fun PlayerMotionSimulator.setMouseYaw(yaw: Float) {
        val rotYaw = this.yaw
        getMouse().changeYaw(angleDifference(rotYaw, yaw))
    }

    private fun PlayerMotionSimulator.setMousePitch(pitch: Float) {
        val rotPitch = this.pitch
        getMouse().changePitch(angleDifference(rotPitch, pitch))
    }

    /**
     * Checks whether the bot is “at a wall” by sampling a block a short distance
     * in the direction the bot is facing.
     */
    private fun isAtWall(): Boolean {
        val fakePlayer = clientInstance.fakePlayer
        val world = fakePlayer.world ?: return false

        val posX = fakePlayer.x
        val posY = fakePlayer.y + fakePlayer.eyeHeight
        val posZ = fakePlayer.z
        val yaw = fakePlayer.yaw
        val pitch = 0f

        val checkDistance = 1.0
        val dir = vectorForRotation(pitch, yaw)  // Assumes this helper exists.
        val checkX = posX + dir[0] * checkDistance
        val checkY = posY + dir[1] * checkDistance
        val checkZ = posZ + dir[2] * checkDistance

        val block = world.getBlockAt(checkX, checkY, checkZ)
        return block != null && block.id != Block.AIR
    }

    override fun onTick() {
        val fakePlayer = clientInstance.fakePlayer

        // Move forward; clear other directional keys.
        pressKey(Key.Type.KEY_W)
        unpressKey(Key.Type.KEY_S, Key.Type.KEY_A, Key.Type.KEY_D)

        // When not potting, adjust aim based on movement and enemy positions.
        if (!inventoryOpen) {
            val pitch = minimizePitch(fakePlayer) { it.airTimeTicks.toDouble() }
            setMousePitch(pitch)

            if (pottingTicks-- > 0) return setMouseYaw(thrownYaw)
            else setMouseYaw(angleAwayFromEnemies())
        }

        val inventory = fakePlayer.inventory ?: return
        val itemStack: ItemStack? = inventory.heldItemStack
        if (itemStack == null || itemStack.item.id != Item.POTION || itemStack.durability != 16421 || inventoryOpen) switchToPot()
        else {
            val closestEnemy = closestEnemy()
            val distanceCondition = (closestEnemy?.distance3DTo(fakePlayer) ?: Double.MAX_VALUE) >= distanceFromEnemy &&
                    distanceFromEnemy > 3.6
            val simulator = fakePlayer.motionSimulator
            simulator.keyboard.pressKey(Key.Type.KEY_W)
            simulator.keyboard.unpressKey(Key.Type.KEY_S, Key.Type.KEY_A, Key.Type.KEY_D)
            simulator.setMouseYaw(fakePlayer.yaw)

            val enemySimulator = closestEnemy?.motionSimulator

            val trajectory = object : SplashPotionTrajectory(
                fakePlayer.world,
                fakePlayer.x,
                fakePlayer.y + fakePlayer.eyeHeight,
                fakePlayer.z,
                fakePlayer.yaw,
                fakePlayer.pitch, { x, y, z ->
                    hasHitBlock(fakePlayer.world, x, y, z) && run {
                        val distance = simulator.distance3DToSq(x, y, z)

                        if (distance < 16.0) 1.0 - sqrt(distance) / 4.0 > 0.5
                        else false
                    } && run {
                        val distance = enemySimulator?.distance3DToSq(x, y, z) ?: Double.MAX_VALUE

                        if (distance < 16.0) 1.0 - sqrt(distance) / 4.0 == 0.0
                        else true
                    }
                }
            ) {
                override fun tick(): Trajectory.Result {
                    simulator.execute(50)
                    enemySimulator?.execute(50)
                    return super.tick()
                }
            }

            if (trajectory.compute(1000) == Trajectory.Result.VALID && (isAtWall() || distanceCondition || fakePlayer.health <= 6f)) {
                thrownYaw = fakePlayer.yaw
                lastPotTick = clientInstance.currentTick
                pottingTicks = 40
                pressButton(10, MouseButton.Type.RIGHT_CLICK)
            }
        }
        distanceFromEnemy = distanceAwayFromEnemies()
    }

    private fun minimizePitch(
        fakePlayer: FakePlayer,
        valueFunction: (SplashPotionTrajectory) -> Double
    ): Float {
        val objective = UnivariateFunction { pitch ->
            val simulator = fakePlayer.motionSimulator
            simulator.keyboard.pressKey(Key.Type.KEY_W)
            simulator.keyboard.unpressKey(Key.Type.KEY_S, Key.Type.KEY_A, Key.Type.KEY_D)
            simulator.setMouseYaw(fakePlayer.yaw)

            val enemy = closestEnemy()
            val enemySimulator = enemy?.motionSimulator
            val trajectory = object : SplashPotionTrajectory(
                fakePlayer.world,
                fakePlayer.x,
                fakePlayer.y + fakePlayer.eyeHeight,
                fakePlayer.z,
                fakePlayer.yaw,
                pitch.toFloat(), { x, y, z ->
                    hasHitBlock(fakePlayer.world, x, y, z) && run {
                        val distance = simulator.distance3DToSq(x, y, z)

                        if (distance < 16.0) 1.0 - sqrt(distance) / 4.0 > 0.5
                        else false
                    } && run {
                        val distance = enemySimulator?.distance3DToSq(x, y, z) ?: Double.MAX_VALUE

                        if (distance < 16.0) 1.0 - sqrt(distance) / 4.0 == 0.0
                        else true
                    }
                }
            ) {
                override fun tick(): Trajectory.Result {
                    simulator.execute(50)
                    enemySimulator?.execute(50)
                    return super.tick()
                }
            }
            if (trajectory.compute(1000) === Trajectory.Result.VALID)
                valueFunction.invoke(trajectory)
            else
                Double.MAX_VALUE
        }

        val optimizer = BrentOptimizer(1e-10, 1e-14)

        val result = optimizer.optimize(
            MaxEval(1000),
            UnivariateObjectiveFunction(objective),
            GoalType.MINIMIZE,
            SearchInterval(-90.0, 90.0)
        )

        return result.value.toFloat()
    }

    private fun hasHitBlock(world: ClientWorld?, x: Double, y: Double, z: Double): Boolean {
        val xTile = floor(x)
        val yTile = floor(y)
        val zTile = floor(z)
        val block = world?.getBlockAt(xTile, yTile, zTile)

        if (world != null && block != null && block.id != Block.AIR) return block.getCollisionBoundingBox(
            world,
            xTile,
            yTile,
            zTile
        )?.isVecInside(x, y, z) ?: false

        return false
    }

    override fun onEvent(event: Event): Boolean {
        if (event is EntityDestroyEvent) {
            val fakePlayer = clientInstance.fakePlayer
            if (event.destroyedEntity is ClientThrowableEntity && event.destroyedEntity.distance3DToSq(fakePlayer) < 9.0)
                pottingTicks = 0
        }
        return false
    }

    public override fun onGameLoop() {
        // No changes made in the game loop.
    }
}
