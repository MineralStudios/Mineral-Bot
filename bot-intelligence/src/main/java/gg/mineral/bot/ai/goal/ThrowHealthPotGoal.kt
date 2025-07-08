package gg.mineral.bot.ai.goal

import gg.mineral.bot.ai.goal.type.InventoryGoal
import gg.mineral.bot.api.controls.Key
import gg.mineral.bot.api.controls.MouseButton
import gg.mineral.bot.api.entity.living.ClientLivingEntity
import gg.mineral.bot.api.entity.living.player.ClientPlayer
import gg.mineral.bot.api.entity.living.player.FakePlayer
import gg.mineral.bot.api.entity.throwable.ClientPotion
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
    private var lastPotTick = 0

    // Simplified state management
    private enum class PotState {
        PREPARING,      // Getting potion ready (inventory management)
        AIMING,         // Calculating trajectory and aiming
        THROWING,       // Actually throwing the potion
        TRACKING,       // Tracking the thrown potion
        COOLDOWN        // Waiting before next throw
    }

    private var currentState = PotState.PREPARING
    private var stateStartTick = 0
    private var thrownPotionId: Int? = null
    private var potionLandingX: Double = 0.0
    private var potionLandingZ: Double = 0.0

    override fun shouldExecute(): Boolean {
        // Cooldown between throws
        if (clientInstance.currentTick - lastPotTick < 40) return false

        val fakePlayer = clientInstance.fakePlayer
        val inventory = fakePlayer.inventory

        // Check if we have health pots
        if (!inventory.contains { it: ItemStack -> isHealthPot(it) }) return false

        val distanceFromEnemies = distanceAwayFromEnemies()

        // Update health regression
        healthRegression.addData(clientInstance.currentTick.toDouble(), fakePlayer.health.toDouble())

        val health = min(
            fakePlayer.health.toDouble(),
            healthRegression.predict((clientInstance.currentTick + clientInstance.configuration.predictionHorizon).toDouble())
        )

        // Throw if health is low
        return health < 12 && (health < 6 || distanceFromEnemies > 3.8)
    }

    override fun onStart() {
        currentState = PotState.PREPARING
        stateStartTick = clientInstance.currentTick
        thrownPotionId = null
        // Move forward by default
        pressKey(Key.Type.KEY_W, Key.Type.KEY_LCONTROL)
        unpressKey(Key.Type.KEY_S, Key.Type.KEY_A, Key.Type.KEY_D)
    }

    override fun onTick(tick: Tick) {
        val fakePlayer = clientInstance.fakePlayer
        val inventory = fakePlayer.inventory

        // Update health regression
        healthRegression.addData(clientInstance.currentTick.toDouble(), fakePlayer.health.toDouble())

        // State timeout check - prevent getting stuck in any state
        if (clientInstance.currentTick - stateStartTick > 60) {
            this.finish()
            return
        }

        when (currentState) {
            PotState.PREPARING -> handlePreparing(tick, fakePlayer, inventory)
            PotState.AIMING -> handleAiming(tick, fakePlayer, inventory)
            PotState.THROWING -> handleThrowing(tick, fakePlayer, inventory)
            PotState.TRACKING -> handleTracking(tick, fakePlayer)
            PotState.COOLDOWN -> handleCooldown(tick)
        }
    }

    private fun handlePreparing(tick: Tick, fakePlayer: FakePlayer, inventory: gg.mineral.bot.api.inv.Inventory) {
        val healthSlot = getHealthPotSlot()

        tick.finishIf("No Valid Health Pot Found", healthSlot == -1)

        // If potion is already in hotbar, just select it
        if (healthSlot <= 8) {
            // Close inventory if open
            tick.prerequisite("Inventory Closed", clientInstance.currentScreen !is ContainerScreen) {
                pressKey(10, Key.Type.KEY_ESCAPE)
            }

            // Select the potion
            tick.prerequisite("Correct Hotbar Slot Selected", inventory.heldSlot == healthSlot) {
                pressKey(10, Key.Type.valueOf("KEY_" + (healthSlot + 1)))
            }

            // Verify we're holding a health pot
            val isHoldingHealth = inventory.heldItemStack?.let { isHealthPot(it) } == true
            if (isHoldingHealth) {
                transitionTo(PotState.AIMING)
            }
        } else {
            // Potion is in main inventory, need to move it
            if (clientInstance.currentScreen !is ContainerScreen) {
                // Open inventory
                pressKey(10, Key.Type.KEY_E)
            } else {
                // Inventory is open, move the item
                moveItemToHotbar(healthSlot, inventory)
                // Don't transition yet, let the move complete
            }
        }
    }

    private fun handleAiming(tick: Tick, fakePlayer: FakePlayer, inventory: gg.mineral.bot.api.inv.Inventory) {
        // Double check we still need to pot
        tick.finishIf("Potting Not Needed", !shouldExecute())

        // Make sure we're still holding the potion
        val isHoldingHealth = inventory.heldItemStack?.let { isHealthPot(it) } == true
        tick.finishIf("Not Holding Health Pot", !isHoldingHealth)

        val closestEnemy = closestEnemy()
        val distanceFromEnemies = distanceAwayFromEnemies()

        // First, turn away from enemies
        val targetYaw = angleAwayFromEnemies()
        setMouseYaw(targetYaw)

        // Check if we're facing away properly (within 30 degrees)
        val yawDiff = kotlin.math.abs(angleDifference(fakePlayer.yaw, targetYaw))
        if (yawDiff > 5) {
            // Still turning, don't throw yet
            return
        }

        // Calculate optimal pitch after we're facing the right direction
        tick.executeAsync(0, {
            minimizePitch(fakePlayer, closestEnemy) { it.airTimeTicks.toDouble() }
        }) {
            setMousePitch(it)
        }

        // Check throwing conditions
        val atWall = isAtWall()
        val goodDistance = distanceFromEnemies > 3.6
        val criticalHealth = fakePlayer.health <= 6

        // Calculate trajectory
        val simulator = fakePlayer.motionSimulator().apply {
            keyboard.pressKey(Key.Type.KEY_W, Key.Type.KEY_LCONTROL)
            keyboard.unpressKey(Key.Type.KEY_S, Key.Type.KEY_A, Key.Type.KEY_D)
            setMouseYaw(targetYaw)
        }

        val enemySimulator = closestEnemy?.motionSimulator()

        val trajectory = object : SplashPotionTrajectory(
            fakePlayer.world,
            fakePlayer.x,
            fakePlayer.y + fakePlayer.eyeHeight,
            fakePlayer.z,
            targetYaw,
            fakePlayer.pitch, { x, y, z ->
                // Check if hits a block and player is in splash range
                val hitBlockInRange = hasHitBlock(fakePlayer.world, x, y, z) && run {
                    val distance = simulator.distance3DToSq(x, y, z)
                    if (distance < 16.0) 1.0 - sqrt(distance) / 4.0 > 0.5
                    else false
                }

                // Check if directly hits player's bounding box
                val hitsPlayer = run {
                    val playerX = simulator.x
                    val playerY = simulator.y
                    val playerZ = simulator.z
                    val width = 0.6 // Player width
                    val height = 1.8 // Player height

                    // Check if potion position is within player's bounding box
                    x >= playerX - width / 2 && x <= playerX + width / 2 &&
                            y >= playerY && y <= playerY + height &&
                            z >= playerZ - width / 2 && z <= playerZ + width / 2
                }

                // Make sure enemy wouldn't be hit
                val enemySafe = run {
                    val distance = enemySimulator?.distance3DToSq(x, y, z) ?: Double.MAX_VALUE
                    if (distance < 16.0) 1.0 - sqrt(distance) / 4.0 == 0.0
                    else true
                }

                (hitBlockInRange || hitsPlayer) && enemySafe
            }
        ) {
            override fun tick(): Trajectory.Result {
                simulator.execute(50)
                enemySimulator?.execute(50)
                return super.tick()
            }
        }

        // Decide whether to throw (increase compute limit for longer throws)
        val trajectoryResult = trajectory.compute(200)
        val validTrajectory = trajectoryResult == Trajectory.Result.VALID

        // Store landing position for tracking
        if (validTrajectory) {
            potionLandingX = trajectory.x
            potionLandingZ = trajectory.z
        }

        // Log for debugging
        logger.debug("Trajectory: {}, distance: {}, atWall: {}", trajectoryResult, distanceFromEnemies, atWall)

        // Throw if we have a valid trajectory OR if health is critical
        if ((validTrajectory && (atWall || goodDistance)) || criticalHealth) {
            transitionTo(PotState.THROWING)
        }

        // Don't stay in aiming state too long
        if (clientInstance.currentTick - stateStartTick > 60) {
            this.finish()
        }
    }

    private fun handleThrowing(tick: Tick, fakePlayer: FakePlayer, inventory: gg.mineral.bot.api.inv.Inventory) {
        // Throw the potion
        pressButton(5, MouseButton.Type.RIGHT_CLICK)

        // Look for the thrown potion entity
        val world = fakePlayer.world
        val nearbyPotion = world.entities
            .filterIsInstance<ClientPotion>()
            .filter { it.potionDurability == 16421 }
            .minByOrNull { it.distance3DTo(fakePlayer) }

        if (nearbyPotion != null) {
            thrownPotionId = nearbyPotion.entityId
            transitionTo(PotState.TRACKING)
        } else if (clientInstance.currentTick - stateStartTick > 10) {
            // Couldn't find thrown potion after 10 ticks, assume it failed
            transitionTo(PotState.COOLDOWN)
        }
    }

    private fun handleTracking(tick: Tick, fakePlayer: FakePlayer) {
        // Track the thrown potion if we can find it
        thrownPotionId?.let { potionId ->
            val world = fakePlayer.world
            val potion = world.entities
                .filterIsInstance<ClientPotion>()
                .find { it.entityId == potionId }

            if (potion != null) {
                // Create trajectory from current position with actual velocity
                val trajectory = SplashPotionTrajectory.fromVelocity(
                    fakePlayer.world,
                    potion.x,
                    potion.y,
                    potion.z,
                    potion.motionX,
                    potion.motionY,
                    potion.motionZ
                ) { x, y, z -> hasHitBlock(fakePlayer.world, x, y, z) }

                // Calculate where it will land
                if (trajectory.compute(100) == Trajectory.Result.VALID) {
                    potionLandingX = trajectory.x
                    potionLandingZ = trajectory.z
                }

                // Look at the predicted landing position
                val x = potionLandingX - fakePlayer.x
                val z = potionLandingZ - fakePlayer.z

                val yaw = Math.toDegrees(-fastArcTan(x / z)).toFloat().let {
                    when {
                        z < 0.0 && x < 0.0 -> (90.0 + Math.toDegrees(fastArcTan(z / x))).toFloat()
                        z < 0.0 && x > 0.0 -> (-90.0 + Math.toDegrees(fastArcTan(z / x))).toFloat()
                        else -> it
                    }
                }

                setMouseYaw(yaw)
            } else {
                // Potion disappeared, move to cooldown
                transitionTo(PotState.COOLDOWN)
            }
        }

        // Don't track for too long
        if (clientInstance.currentTick - stateStartTick > 30) {
            transitionTo(PotState.COOLDOWN)
        }
    }

    private fun handleCooldown(tick: Tick) {
        // Just wait a bit before finishing
        if (clientInstance.currentTick - stateStartTick > 5) {
            this.finish()
        }
    }

    private fun transitionTo(newState: PotState) {
        currentState = newState
        stateStartTick = clientInstance.currentTick


        // Update last pot tick when we start cooldown
        if (newState == PotState.COOLDOWN) {
            lastPotTick = clientInstance.currentTick
        }

        // Restore forward movement when leaving throwing state
        if (newState != PotState.THROWING) {
            unpressKey(Key.Type.KEY_S)
            pressKey(Key.Type.KEY_W, Key.Type.KEY_LCONTROL)
        }
    }

    override fun onEnd() {
        if (clientInstance.currentScreen is ContainerScreen)
            pressKey(10, Key.Type.KEY_ESCAPE)
        healthRegression.clear()
        // Restore normal movement
        unpressKey(Key.Type.KEY_S)
        pressKey(Key.Type.KEY_W, Key.Type.KEY_LCONTROL)
    }

    override fun onEvent(event: Event): Boolean {
        when (event) {
            is EntityDestroyEvent -> {
                // If we're tracking a potion and it gets destroyed, move to cooldown
                if (currentState == PotState.TRACKING &&
                    event.destroyedEntity is ClientPotion &&
                    event.destroyedEntity.entityId == thrownPotionId
                ) {
                    transitionTo(PotState.COOLDOWN)
                }
            }
        }
        return false
    }

    // Helper functions remain the same
    private fun isHealthPot(itemStack: ItemStack): Boolean {
        val item = itemStack.item
        return item.id == Item.POTION && itemStack.durability == 16421
    }

    private fun getHealthPotSlot(): Int {
        val fakePlayer = clientInstance.fakePlayer
        val inventory = fakePlayer.inventory

        for (i in 0..35) {
            val itemStack = inventory.getItemStackAt(i) ?: continue
            if (isHealthPot(itemStack)) {
                return i
            }
        }
        return -1
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

    private fun isAtWall(): Boolean {
        val fakePlayer = clientInstance.fakePlayer
        val world = fakePlayer.world

        val posX = fakePlayer.x
        val posY = fakePlayer.y + fakePlayer.eyeHeight
        val posZ = fakePlayer.z
        val yaw = fakePlayer.yaw
        val pitch = 0f

        val checkDistance = 1.0
        val dir = vectorForRotation(pitch, yaw)
        val checkX = posX + dir[0] * checkDistance
        val checkY = posY + dir[1] * checkDistance
        val checkZ = posZ + dir[2] * checkDistance

        val block = world.getBlockAt(checkX, checkY, checkZ)
        return block.id != Block.AIR
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
            // Use the angle away from enemies for trajectory calculation
            val awayYaw = angleAwayFromEnemies()
            simulator.setMouseYaw(awayYaw)
            val enemySimulator = enemy?.motionSimulator()
            val trajectory = object : SplashPotionTrajectory(
                fakePlayer.world,
                fakePlayer.x,
                fakePlayer.y + fakePlayer.eyeHeight,
                fakePlayer.z,
                awayYaw,
                pitch.toFloat(), { x, y, z ->
                    // Check if hits a block and player is in splash range
                    val hitBlockInRange = hasHitBlock(fakePlayer.world, x, y, z) && run {
                        val distance = simulator.distance3DToSq(x, y, z)
                        if (distance < 16.0) 1.0 - sqrt(distance) / 4.0 > 0.5
                        else false
                    }

                    // Check if directly hits player's bounding box
                    val hitsPlayer = run {
                        val playerX = simulator.x
                        val playerY = simulator.y
                        val playerZ = simulator.z
                        val width = 0.6 // Player width
                        val height = 1.8 // Player height

                        // Check if potion position is within player's bounding box
                        x >= playerX - width / 2 && x <= playerX + width / 2 &&
                                y >= playerY && y <= playerY + height &&
                                z >= playerZ - width / 2 && z <= playerZ + width / 2
                    }

                    // Make sure enemy wouldn't be hit
                    val enemySafe = run {
                        val distance = enemySimulator?.distance3DToSq(x, y, z) ?: Double.MAX_VALUE
                        if (distance < 16.0) 1.0 - sqrt(distance) / 4.0 == 0.0
                        else true
                    }

                    (hitBlockInRange || hitsPlayer) && enemySafe
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

    // Extension functions for adjusting the bot's aim
    private fun PlayerMotionSimulator.setMouseYaw(yaw: Float) {
        val rotYaw = this.yaw
        mouse.changeYaw(angleDifference(rotYaw, yaw))
    }

    public override fun onGameLoop() {
        // No changes made in the game loop.
    }
}