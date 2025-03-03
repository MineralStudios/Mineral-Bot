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
import gg.mineral.bot.api.goal.Sporadic
import gg.mineral.bot.api.goal.Timebound
import gg.mineral.bot.api.instance.ClientInstance
import gg.mineral.bot.api.inv.item.Item
import gg.mineral.bot.api.inv.item.ItemStack
import gg.mineral.bot.api.math.simulation.PlayerMotionSimulator
import gg.mineral.bot.api.math.trajectory.Trajectory
import gg.mineral.bot.api.math.trajectory.throwable.SplashPotionTrajectory
import gg.mineral.bot.api.screen.type.ContainerScreen
import gg.mineral.bot.api.world.ClientWorld
import gg.mineral.bot.api.world.block.Block
import org.apache.commons.math3.analysis.UnivariateFunction
import org.apache.commons.math3.optim.MaxEval
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType
import org.apache.commons.math3.optim.univariate.BrentOptimizer
import org.apache.commons.math3.optim.univariate.SearchInterval
import org.apache.commons.math3.optim.univariate.UnivariateObjectiveFunction
import org.apache.commons.math3.stat.regression.SimpleRegression

class ThrowHealthPotGoal(clientInstance: ClientInstance) : InventoryGoal(clientInstance), Sporadic, Timebound {
    override val maxDuration: Long = 100
    override var startTime: Long = 0
    override var executing: Boolean = false
    private val healthRegression: SimpleRegression = SimpleRegression()
    private var distanceFromEnemy = Double.MAX_VALUE
    private var lastPotTick = 0
    private var pottingTicks = 0
        set(value) {
            if (field > 0 && value <= 0) this.finish()
            field = value
        }
    private var thrownYaw = 0f

    override fun shouldExecute(): Boolean {
        if (clientInstance.currentTick - lastPotTick < 20) return false

        val fakePlayer = clientInstance.fakePlayer
        val inventory = fakePlayer.inventory

        if (!inventory.contains { it: ItemStack ->
                isHealthPot(it)
            }) return false

        val distanceFromEnemies = distanceAwayFromEnemies()

        healthRegression.addData(clientInstance.currentTick.toDouble(), fakePlayer.health.toDouble())

        val health = min(
            fakePlayer.health.toDouble(),
            healthRegression.predict((clientInstance.currentTick + clientInstance.configuration.predictionHorizon).toDouble())
        )

        return health < 12 && (health < 6 || distanceFromEnemies > 3.8)
    }

    override fun onStart() {
        // Move forward; clear other directional keys.
        pressKey(Key.Type.KEY_W, Key.Type.KEY_LCONTROL)
        unpressKey(Key.Type.KEY_S, Key.Type.KEY_A, Key.Type.KEY_D)
    }

    private fun angleAwayFromEnemies(): Float {
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
        return yaw + 180.0f
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
        mouse.changeYaw(angleDifference(rotYaw, yaw))
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

    private fun isHealthPot(itemStack: ItemStack): Boolean {
        val item = itemStack.item
        return item.id == Item.POTION && itemStack.durability == 16421
    }

    private fun getHealthPotSlot(): Int {
        var healthPotSlot = -1
        val fakePlayer = clientInstance.fakePlayer
        val inventory = fakePlayer.inventory

        for (i in 0..35) {
            val itemStack = inventory.getItemStackAt(i) ?: continue
            if (isHealthPot(itemStack)) {
                healthPotSlot = i
                break
            }
        }

        return healthPotSlot
    }

    override fun onTick(tick: Tick) {
        val healthSlot = getHealthPotSlot()
        val fakePlayer = clientInstance.fakePlayer
        val inventory = fakePlayer.inventory

        tick.prerequisite("In Hotbar", healthSlot <= 8) {
            moveItemToHotbar(healthSlot, inventory)
        }

        tick.prerequisite("Inventory Closed", clientInstance.currentScreen !is ContainerScreen) {
            pressKey(10, Key.Type.KEY_ESCAPE)
        }

        tick.prerequisite("Isn't Potting", pottingTicks <= 0) {
            pottingTicks--
            setMouseYaw(thrownYaw)
        }

        tick.execute {
            healthRegression.addData(clientInstance.currentTick.toDouble(), fakePlayer.health.toDouble())
        }

        tick.finishIf(
            "Potting Not Needed",
            !shouldExecute()
        )

        tick.finishIf(
            "No Valid Health Pot Found",
            healthSlot == -1
        )

        val isHoldingHealth = inventory.heldItemStack?.let { isHealthPot(it) } == true

        val closestEnemy = closestEnemy()

        tick.executeAsync(0, {
            minimizePitch(fakePlayer, closestEnemy) { it.airTimeTicks.toDouble() }
        }) {
            setMousePitch(it)
            setMouseYaw(angleAwayFromEnemies())
        }

        tick.prerequisite("Correct Hotbar Slot Selected", inventory.heldSlot == healthSlot || isHoldingHealth) {
            if (healthSlot <= 8)
                pressKey(10, Key.Type.valueOf("KEY_" + (healthSlot + 1)))
        }

        tick.finishIf(
            "Not Holding Valid Health Pot",
            !isHoldingHealth
        )

        val newDistance = distanceAwayFromEnemies()
        val distanceCondition = newDistance >= distanceFromEnemy && distanceFromEnemy > 3.6 && newDistance > 3.6
        distanceFromEnemy = distanceAwayFromEnemies()
        val simulator = fakePlayer.motionSimulator().apply {
            keyboard.pressKey(Key.Type.KEY_W, Key.Type.KEY_LCONTROL)
            keyboard.unpressKey(Key.Type.KEY_S, Key.Type.KEY_A, Key.Type.KEY_D)
            setMouseYaw(fakePlayer.yaw)
        }

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
                }
            }
        ) {
            override fun tick(): Trajectory.Result {
                simulator.execute(50)
                return super.tick()
            }
        }

        tick.prerequisite(
            "Potting",
            pottingTicks > 0
        ) {
            if ((trajectory.compute(100) == Trajectory.Result.VALID && (isAtWall() || distanceCondition)) || min(
                    fakePlayer.health.toDouble(),
                    healthRegression.predict((clientInstance.currentTick + trajectory.airTimeTicks).toDouble())
                ) <= 6f
            ) {
                thrownYaw = fakePlayer.yaw
                lastPotTick = clientInstance.currentTick
                pottingTicks = 40
                pressButton(10, MouseButton.Type.RIGHT_CLICK)
            }
        }
    }

    override fun onEnd() {
        if (clientInstance.currentScreen is ContainerScreen)
            pressKey(10, Key.Type.KEY_ESCAPE)
        healthRegression.clear()
    }

    private fun minimizePitch(
        fakePlayer: FakePlayer,
        enemy: ClientPlayer?,
        valueFunction: (SplashPotionTrajectory) -> Double
    ): Float {
        val objective = UnivariateFunction { pitch ->
            val simulator = fakePlayer.motionSimulator()
            simulator.keyboard.pressKey(Key.Type.KEY_W, Key.Type.KEY_LCONTROL)
            simulator.keyboard.unpressKey(Key.Type.KEY_S, Key.Type.KEY_A, Key.Type.KEY_D)
            simulator.setMouseYaw(fakePlayer.yaw)
            val enemySimulator = enemy?.motionSimulator()
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
            if (trajectory.compute(100) === Trajectory.Result.VALID)
                valueFunction.invoke(trajectory)
            else
                Double.MAX_VALUE
        }

        val optimizer = BrentOptimizer(1e-10, 1e-14)

        val result = optimizer.optimize(
            MaxEval(180),
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
        when (event) {
            is EntityDestroyEvent -> {
                val fakePlayer = clientInstance.fakePlayer
                if (event.destroyedEntity is ClientThrowableEntity && event.destroyedEntity.distance3DToSq(fakePlayer) < 9.0)
                    pottingTicks = 0
            }
        }

        return false
    }

    public override fun onGameLoop() {
        // No changes made in the game loop.
    }
}
