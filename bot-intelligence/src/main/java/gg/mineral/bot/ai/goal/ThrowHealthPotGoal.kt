package gg.mineral.bot.ai.goal

import gg.mineral.bot.ai.goal.type.InventoryGoal
import gg.mineral.bot.api.controls.Key
import gg.mineral.bot.api.controls.MouseButton
import gg.mineral.bot.api.entity.ClientEntity
import gg.mineral.bot.api.entity.living.ClientLivingEntity
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
import kotlin.math.atan2

class ThrowHealthPotGoal(clientInstance: ClientInstance) : InventoryGoal(clientInstance) {
    private var lastPotTick = 0
    private var pottingTicks = 0

    // When not at a wall, throwYaw is used normally.
    // When at a wall, we also force pitch to 90 (i.e. straight down).
    private var throwYaw = 0.0f

    // Flag to remember if we are throwing because we’re at a wall.
    private var throwAtWall = false

    private var distanceFromEnemy = Double.MAX_VALUE
    private var onGround = true

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
        val world = fakePlayer.world ?: return warn(this, "World is null").let { fakePlayer.yaw }

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
        val world = fakePlayer.world ?: return warn(this, "World is null").let { Double.MAX_VALUE }

        return world.entities
            .minOfOrNull {
                if (it is ClientLivingEntity && !clientInstance.configuration.friendlyUUIDs.contains(it.uuid))
                    it.distance3DTo(fakePlayer)
                else Double.MAX_VALUE
            } ?: Double.MAX_VALUE
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

    override fun onTick() {
        val fakePlayer = clientInstance.fakePlayer

        // Move forward; clear other directional keys.
        pressKey(Key.Type.KEY_W)
        unpressKey(Key.Type.KEY_S, Key.Type.KEY_A, Key.Type.KEY_D)

        // When not potting, adjust aim based on movement and enemy positions.
        if (!inventoryOpen) {
            if (pottingTicks > 0) {
                if (throwAtWall) setMousePitch(90f)
                pottingTicks--
                if (fakePlayer.health >= 10f && (pottingTicks <= 10 || fakePlayer.health >= 16f)) pottingTicks = 0
                if (pottingTicks == 0) throwAtWall = false
                if (pottingTicks <= 10) return
            }

            val r = sqrt(fakePlayer.motionX * fakePlayer.motionX + fakePlayer.motionZ * fakePlayer.motionZ)
            val yaw = angleAwayFromEnemies()
            val pitch = abs(atan2(fakePlayer.motionZ, r) * 180 / Math.PI).toFloat()
            setMouseYaw(if (pottingTicks > 0) throwYaw else yaw)
            setMousePitch(pitch)
        }

        val inventory = fakePlayer.inventory ?: return
        val itemStack: ItemStack? = inventory.heldItemStack
        if (itemStack == null || itemStack.item.id != Item.POTION || itemStack.durability != 16421 || inventoryOpen) {
            switchToPot()
        } else {
            val distanceCondition = distanceAwayFromEnemies() >= distanceFromEnemy &&
                    distanceFromEnemy > 3.6 &&
                    fakePlayer.isOnGround &&
                    onGround
            if (isAtWall() || distanceCondition || fakePlayer.health <= 6f) {
                pottingTicks = if (fakePlayer.health <= 4f) 20 else 10
                throwYaw = fakePlayer.yaw
                throwAtWall = isAtWall()
                lastPotTick = clientInstance.currentTick
                pressButton(10, MouseButton.Type.RIGHT_CLICK)
            }
        }
        distanceFromEnemy = distanceAwayFromEnemies()
        onGround = fakePlayer.isOnGround
    }

    private fun minimizePitch(
        fakePlayer: FakePlayer,
        collisionFunction: Trajectory.CollisionFunction,
        valueFunction: (SplashPotionTrajectory) -> Double
    ): Float {
        val objective = UnivariateFunction { pitch ->
            val simulator = SplashPotionTrajectory(
                fakePlayer.world,
                fakePlayer.x,
                fakePlayer.y + fakePlayer.eyeHeight,
                fakePlayer.z,
                fakePlayer.yaw,
                pitch.toFloat(),
                collisionFunction
            )
            if (simulator.compute(1000) === Trajectory.Result.VALID)
                valueFunction.invoke(simulator)
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

    private fun hitEntities(world: ClientWorld?, x: Double, y: Double, z: Double): List<ClientEntity> {
        val list = mutableListOf<ClientEntity>()
        val entities = world?.entities ?: return list
        list.addAll(entities.filter { it: ClientEntity -> it.boundingBox?.isVecInside(x, y, z) == true })
        return list
    }

    override fun onEvent(event: Event): Boolean {
        return false
    }

    public override fun onGameLoop() {
        // No changes made in the game loop.
    }
}
