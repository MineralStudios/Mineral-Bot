package gg.mineral.bot.ai.goal

import gg.mineral.bot.ai.goal.type.InventoryGoal
import gg.mineral.bot.api.controls.Key
import gg.mineral.bot.api.controls.MouseButton
import gg.mineral.bot.api.entity.effect.PotionEffectType
import gg.mineral.bot.api.entity.living.ClientLivingEntity
import gg.mineral.bot.api.entity.living.player.ClientPlayer
import gg.mineral.bot.api.entity.living.player.FakePlayer
import gg.mineral.bot.api.event.Event
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

class ThrowDebuffPotGoal(clientInstance: ClientInstance) : InventoryGoal(clientInstance) {
    private var lastPotTick = 0
    private var effects: Set<Int> = emptySet()
    override val isExecuting: Boolean
        get() = inventoryOpen

    private fun switchToPot() {
        var potSlot = -1
        val fakePlayer = clientInstance.fakePlayer
        val inventory = fakePlayer.inventory

        for (i in 0..35) {
            val itemStack = inventory.getItemStackAt(i) ?: continue
            val item = itemStack.item
            if (item.id == Item.POTION && itemStack.potion?.effects?.any {
                    effects.contains(it.potionID)
                } == true) {
                potSlot = i
                break
            }
        }

        if (potSlot > 8) return moveItemToHotbar(potSlot, inventory)
        if (inventoryOpen) return pressKey(10, Key.Type.KEY_ESCAPE).apply { inventoryOpen = false }

        pressKey(10, Key.Type.valueOf("KEY_" + (potSlot + 1)))
    }

    override fun shouldExecute(): Boolean {
        if (clientInstance.currentTick - lastPotTick < 20) return false

        val fakePlayer = clientInstance.fakePlayer
        val inventory = fakePlayer.inventory
        val closestEnemy = closestEnemy() ?: return false

        val debuffEffects = setOf(
            PotionEffectType.HARM.id,
            PotionEffectType.POISON.id,
            PotionEffectType.WEAKNESS.id,
            PotionEffectType.SLOW.id,
            PotionEffectType.BLINDNESS.id,
            PotionEffectType.HUNGER.id,
            PotionEffectType.SLOW_DIGGING.id
        )

        val effects = debuffEffects.subtract(closestEnemy.activePotionEffectIds.toSet())
        if (effects.isEmpty()) return false

        return (inventory.containsPotion {
            it.effects.any { effect ->
                effects.contains(effect.potionID)
            }
        } == true && closestEnemy.distance3DToSq(fakePlayer) in 25.0..64.0).apply {
            if (this) this@ThrowDebuffPotGoal.effects = effects
        }
    }

    private fun angleTowardsFromEnemies(): Float {
        val fakePlayer = clientInstance.fakePlayer
        val world = fakePlayer.world

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
        return yaw
    }

    private fun distanceAwayFromEnemies(): Double {
        val fakePlayer = clientInstance.fakePlayer
        val world = fakePlayer.world

        return world.entities
            .minOfOrNull {
                if (it is ClientLivingEntity && !clientInstance.configuration.friendlyUUIDs.contains(it.uuid))
                    it.distance2DTo(fakePlayer.x, fakePlayer.z)
                else Double.MAX_VALUE
            } ?: Double.MAX_VALUE
    }

    private fun closestEnemy(): ClientPlayer? {
        val fakePlayer = clientInstance.fakePlayer
        val world = fakePlayer.world
        val targetSearchRange = clientInstance.configuration.targetSearchRange
        var bestTarget: ClientPlayer? = null
        var closestDistance = Double.MAX_VALUE

        for (entity in world.entities) {
            if (entity is ClientPlayer && entity != fakePlayer &&
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
        mouse.changeYaw(angleDifference(rotYaw, yaw))
    }

    private fun PlayerMotionSimulator.setMousePitch(pitch: Float) {
        val rotPitch = this.pitch
        mouse.changePitch(angleDifference(rotPitch, pitch))
    }

    /**
     * Checks whether the bot is “at a wall” by sampling a block a short distance
     * in the direction the bot is facing.
     */
    private fun isAtWall(): Boolean {
        val fakePlayer = clientInstance.fakePlayer
        val world = fakePlayer.world

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
        return block.id != Block.AIR
    }

    override fun onTick() {
        val fakePlayer = clientInstance.fakePlayer

        pressKey(Key.Type.KEY_S)
        unpressKey(Key.Type.KEY_W, Key.Type.KEY_A, Key.Type.KEY_D)

        if (!inventoryOpen) {
            val pitch = minimizePitch(fakePlayer) { it.airTimeTicks.toDouble() }
            setMousePitch(pitch)
            setMouseYaw(angleTowardsFromEnemies())
        }

        val inventory = fakePlayer.inventory
        val itemStack: ItemStack? = inventory.heldItemStack
        if (itemStack == null || itemStack.item.id != Item.POTION || itemStack.potion?.effects?.any {
                effects.contains(it.potionID)
            } == false || inventoryOpen) switchToPot()
        else {
            val closestEnemy = closestEnemy() ?: return

            val simulator = fakePlayer.motionSimulator.apply {
                setMouseYaw(fakePlayer.yaw)
                keyboard.pressKey(Key.Type.KEY_S)
                keyboard.unpressKey(Key.Type.KEY_W, Key.Type.KEY_A, Key.Type.KEY_D)
            }

            val enemySimulator = closestEnemy.motionSimulator

            val trajectory = object : SplashPotionTrajectory(
                fakePlayer.world,
                fakePlayer.x,
                fakePlayer.y + fakePlayer.eyeHeight,
                fakePlayer.z,
                fakePlayer.yaw,
                fakePlayer.pitch, { x, y, z ->
                    hasHitBlock(fakePlayer.world, x, y, z) &&
                            enemySimulator.distance3DToSq(x, y, z).let {
                                if (it < 16.0) 1.0 - sqrt(it) / 4.0 > 0.5
                                else false
                            }
                            && simulator.distance3DToSq(x, y, z)
                        .let { if (it < 16.0) 1.0 - sqrt(it) / 4.0 <= 0.01 else true }
                }
            ) {
                override fun tick(): Trajectory.Result {
                    enemySimulator.execute(50)
                    simulator.execute(50)
                    return super.tick()
                }
            }

            if (trajectory.compute(1000) == Trajectory.Result.VALID && !isAtWall()) {
                lastPotTick = clientInstance.currentTick
                pressButton(10, MouseButton.Type.RIGHT_CLICK)
            }
        }
    }

    private fun minimizePitch(
        fakePlayer: FakePlayer,
        valueFunction: (SplashPotionTrajectory) -> Double
    ): Float {
        val closestEnemy = closestEnemy() ?: return fakePlayer.pitch
        val objective = UnivariateFunction { pitch ->
            val simulator = fakePlayer.motionSimulator.apply {
                setMouseYaw(fakePlayer.yaw)
                keyboard.pressKey(Key.Type.KEY_S)
                keyboard.unpressKey(Key.Type.KEY_W, Key.Type.KEY_A, Key.Type.KEY_D)
            }

            val enemySimulator = closestEnemy.motionSimulator
            val trajectory = object : SplashPotionTrajectory(
                fakePlayer.world,
                fakePlayer.x,
                fakePlayer.y + fakePlayer.eyeHeight,
                fakePlayer.z,
                fakePlayer.yaw,
                pitch.toFloat(), { x, y, z ->
                    hasHitBlock(closestEnemy.world, x, y, z) && enemySimulator.distance3DToSq(x, y, z).let {
                        if (it < 16.0) 1.0 - sqrt(it) / 4.0 > 0.5
                        else false
                    }
                            && simulator.distance3DToSq(x, y, z)
                        .let { if (it < 16.0) 1.0 - sqrt(it) / 4.0 <= 0.01 else true }
                }
            ) {
                override fun tick(): Trajectory.Result {
                    enemySimulator.execute(50)
                    simulator.execute(50)
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

        if (world != null && block != null && block.id != Block.AIR) {
            val collisionBoundingBox = block.getCollisionBoundingBox(world, xTile, yTile, zTile)
            return collisionBoundingBox != null && collisionBoundingBox.isVecInside(x, y, z)
        }

        return false
    }

    override fun onEvent(event: Event): Boolean {
        return false
    }

    public override fun onGameLoop() {
        // No changes made in the game loop.
    }
}
