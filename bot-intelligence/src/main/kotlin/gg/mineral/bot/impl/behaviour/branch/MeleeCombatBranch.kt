package gg.mineral.bot.impl.behaviour.branch

import gg.mineral.bot.api.behaviour.BTResult
import gg.mineral.bot.api.behaviour.BehaviourTree
import gg.mineral.bot.api.behaviour.branch.BTBranch
import gg.mineral.bot.api.behaviour.node.ChildNode
import gg.mineral.bot.api.behaviour.sequence
import gg.mineral.bot.api.controls.Key
import gg.mineral.bot.api.controls.MouseButton
import gg.mineral.bot.api.entity.living.ClientLivingEntity
import gg.mineral.bot.api.entity.living.player.ClientPlayer
import gg.mineral.bot.api.event.entity.EntityHurtEvent
import gg.mineral.bot.api.inv.item.Item
import gg.mineral.bot.api.inv.item.ItemStack
import gg.mineral.bot.api.screen.type.ContainerScreen
import gg.mineral.bot.api.util.dsl.computeOptimalYawAndPitch
import gg.mineral.bot.api.util.dsl.fastArcTan2
import gg.mineral.bot.api.util.dsl.timeMillis
import gg.mineral.bot.api.util.dsl.vectorForRotation
import gg.mineral.bot.api.world.block.Block
import kotlin.math.*

class MeleeCombatBranch(tree: BehaviourTree) : BTBranch(tree) {
    private var target: ClientPlayer? = null
    private val meanDelay = (1000 / tree.clientInstance.configuration.averageCps).toLong()
    private val deviation =
        abs(((1000 / (tree.clientInstance.configuration.averageCps + 1)).toLong() - meanDelay).toDouble()).toLong()
    private var lastBounceTime: Long = 0
    private var lastTargetSwitchTick = 0
    private var nextClick: Long = 0
    private var currentHorizontalAimAcceleration = 1.0f
    private var currentVerticalAimAcceleration = 1.0f
    private var lastHorizontalTurnSignum = 1
    private var lastVerticalTurnSignum = 1
    private var resetType = ResetType.OFFENSIVE
    private val lastResetType = ResetType.OFFENSIVE
    private var started = false

    // Finds and updates the current target.
    private fun findTarget() {
        val targetSearchRange = tree.clientInstance.configuration.targetSearchRange
        val fakePlayer = tree.clientInstance.fakePlayer
        val world = fakePlayer.world

        // Keep the current target if recently switched and still valid.
        if (tree.clientInstance.currentTick - lastTargetSwitchTick < 20 &&
            target?.let { world.entities.contains(it) && isTargetValid(it, targetSearchRange.toFloat()) } == true
        ) return

        var closestTarget: ClientPlayer? = null
        var closestDistance = Double.MAX_VALUE

        for (entity in world.entities) {
            if (entity is ClientPlayer && isTargetValid(entity, targetSearchRange.toFloat())) {
                val distance = fakePlayer.distance3DTo(entity)
                if (distance < closestDistance) {
                    closestDistance = distance
                    closestTarget = entity
                }
            }
        }

        if (closestTarget !== target) {
            lastTargetSwitchTick = tree.clientInstance.currentTick
            target = closestTarget
        }
    }

    // Checks if a target is valid (not friendly and within range).
    private fun isTargetValid(entity: ClientPlayer, range: Float): Boolean {
        val fakePlayer = tree.clientInstance.fakePlayer
        return !tree.clientInstance.configuration.friendlyUUIDs.contains(entity.uuid) &&
                fakePlayer.distance3DTo(entity) <= range
    }

    // Computes optimal yaw and pitch toward the target.
    private fun computeOptimalYawAndPitchBow(player: ClientPlayer, target: ClientPlayer): FloatArray {
        val xDelta = (target.x - target.lastX) * 0.4
        val zDelta = (target.z - target.lastZ) * 0.4
        var d = player.distance3DTo(target)
        d -= d % 0.8
        val multiplier = if (target.isSprinting) 1.25 else 1.0
        val xMulti = d / 0.8 * xDelta * multiplier
        val zMulti = d / 0.8 * zDelta * multiplier
        val x = target.x + xMulti - player.x
        val z = target.z + zMulti - player.z
        val y = (player.y + player.eyeHeight) - (target.y + target.eyeHeight)
        val dist = player.distance3DTo(target)
        val yaw = Math.toDegrees(atan2(z, x)).toFloat() - 90.0f
        val d1 = sqrt(x * x + z * z)
        val pitch = -(atan2(y, d1) * 180.0 / Math.PI).toFloat() + dist.toFloat() * 0.11f
        return floatArrayOf(yaw, -pitch)
    }

    private fun angleDifference(a: Float, b: Float): Float {
        var diff = b - a
        while (diff < -180f) diff += 360f
        while (diff > 180f) diff -= 360f
        return diff
    }

    private fun signum(value: Float): Float = when {
        value > 0 -> 1f
        value < 0 -> -1f
        else -> 0f
    }

    // Smoothly rotates the current angle toward the target angle.
    private fun getRotationTarget(
        current: Float,
        target: Float,
        turnSpeed: Float,
        accuracy: Float,
        erraticness: Float
    ): Float {
        val difference = angleDifference(current, target)
        if (abs(difference) > turnSpeed) return current + signum(difference) * turnSpeed
        if (accuracy >= 1) return target

        val deviation = 3f / max(0.01f, accuracy)
        val newTarget =
            tree.clientInstance.fakePlayer.random.nextGaussian(target.toDouble(), deviation.toDouble()).toFloat()
        val newDifference = angleDifference(current, newTarget)
        val erraticnessFactor = min(180 * erraticness, turnSpeed)
        return if (abs(newDifference) > erraticnessFactor) current + signum(newDifference) * erraticnessFactor else newTarget
    }

    // Aims at the current target using computed angles.
    private fun aimAtTarget() {
        val tgt = target ?: return
        val fakePlayer = tree.clientInstance.fakePlayer
        val optimalAngles = computeOptimalYawAndPitch(fakePlayer, tgt)

        if (fakePlayer.distance3DTo(tgt) > 6.0f) {
            setMouseYaw(optimalAngles[1])
            setMousePitch(optimalAngles[0])
            return
        }

        val yawDiff = abs(angleDifference(fakePlayer.yaw, optimalAngles[1]))
        val pitchDiff = abs(angleDifference(fakePlayer.pitch, optimalAngles[0]))
        val distX = abs(fakePlayer.x - tgt.x)
        val distZ = abs(fakePlayer.z - tgt.z)
        val horizontalDist = sqrt(distX * distX + distZ * distZ)
        val yawSpeed =
            calculateHorizontalAimSpeed(horizontalDist, yawDiff.toDouble()).toFloat() * currentHorizontalAimAcceleration
        val pitchSpeed = calculateVerticalAimSpeed(pitchDiff.toDouble()).toFloat() * currentVerticalAimAcceleration
        val config = tree.clientInstance.configuration

        val currentHorizontalTurnSignum = signum(
            angleDifference(fakePlayer.yaw, optimalAngles[1])
        ).toInt()

        if (currentHorizontalTurnSignum != lastHorizontalTurnSignum || currentHorizontalTurnSignum == 0) currentHorizontalAimAcceleration =
            1.0f
        else currentHorizontalAimAcceleration *= config.horizontalAimAcceleration

        setMouseYaw(
            getRotationTarget(
                fakePlayer.yaw,
                optimalAngles[1],
                abs(yawSpeed * config.horizontalAimSpeed * 2.0).toFloat(),
                config.horizontalAimAccuracy,
                config.horizontalErraticness
            )
        )

        lastHorizontalTurnSignum = currentHorizontalTurnSignum

        // Give it a higher chance of aiming down
        val verAccuracy = max(0.01f, config.verticalAimAccuracy)
        val deviation = if (verAccuracy >= 1) 0f else 3f / verAccuracy

        val currentVerticalTurnSignum = signum(
            angleDifference(fakePlayer.pitch, optimalAngles[0])
        ).toInt()

        if (currentVerticalTurnSignum != lastVerticalTurnSignum || currentVerticalTurnSignum == 0) currentVerticalAimAcceleration =
            1.0f
        else currentVerticalAimAcceleration *= config.verticalAimAcceleration

        setMousePitch(
            getRotationTarget(
                fakePlayer.pitch,
                optimalAngles[0] + deviation,
                abs(pitchSpeed * config.verticalAimSpeed * 2.0).toFloat(),
                verAccuracy,
                config.verticalErraticness
            )
        )

        lastVerticalTurnSignum = currentVerticalTurnSignum
    }

    private fun calculateHorizontalAimSpeed(distHorizontal: Double, yawDiff: Double): Double {
        val distanceFactor = (10 / ((5 * distHorizontal.coerceAtLeast(0.2)) - 1)) + 2
        val diffFactor = yawDiff / 5
        return distanceFactor + diffFactor
    }

    private fun calculateVerticalAimSpeed(pitchDiff: Double): Double {
        return 2 * pitchDiff / 5
    }

    // Checks if the bot is colliding with a wall (using a ray in the current yaw direction).
    private val isCollidingWithWall: Boolean
        get() {
            val fakePlayer = tree.clientInstance.fakePlayer
            val world = fakePlayer.world
            val posX = fakePlayer.x
            val posY = fakePlayer.y + fakePlayer.eyeHeight
            val posZ = fakePlayer.z
            val yaw = fakePlayer.yaw
            val checkDistance = 1.0
            val dir = vectorForRotation(0f, yaw)
            val checkX = posX + dir[0] * checkDistance
            val checkY = posY + dir[1] * checkDistance
            val checkZ = posZ + dir[2] * checkDistance
            val block = world.getBlockAt(checkX, checkY, checkZ)
            return block.id != Block.AIR
        }

    // Computes a collision normal by sampling around the player.
    private fun collisionNormal(): DoubleArray? {
        val fakePlayer = tree.clientInstance.fakePlayer
        val world = fakePlayer.world
        val posX = fakePlayer.x
        val posY = fakePlayer.y + fakePlayer.eyeHeight
        val sampleCount = 8
        val checkDistance = 0.5
        val normal = doubleArrayOf(0.0, 0.0, 0.0)
        for (i in 0 until sampleCount) {
            val angle = fakePlayer.yaw + (360.0 / sampleCount * i).toFloat()
            val dir = vectorForRotation(0f, angle)
            val checkX = posX + dir[0] * checkDistance
            val checkZ = fakePlayer.z + dir[2] * checkDistance
            val block = world.getBlockAt(checkX, posY, checkZ)
            if (block.id != Block.AIR) {
                normal[0] += -dir[0]
                normal[2] += -dir[2]
            }
        }
        val length = sqrt(normal[0] * normal[0] + normal[2] * normal[2])
        if (length == 0.0) return null
        normal[0] /= length
        normal[2] /= length
        return normal
    }

    // Reflects the aim off a wall.
    private fun reflectOffWall() {
        val fakePlayer = tree.clientInstance.fakePlayer
        val normal = collisionNormal() ?: return
        val normX = normal[0]
        val normZ = normal[2]
        val yaw = fakePlayer.yaw
        val yawRad = Math.toRadians(yaw.toDouble())
        val dirX = -sin(yawRad)
        val dirZ = cos(yawRad)
        val dot = dirX * normX + dirZ * normZ
        val reflectedX = dirX - 2 * dot * normX
        val reflectedZ = dirZ - 2 * dot * normZ
        val newYaw = Math.toDegrees(fastArcTan2(-reflectedX, reflectedZ)).toFloat()
        setMouseYaw(newYaw)
        this.lastBounceTime = timeMillis()
    }

    // Attacks the target by clicking and schedules the next attack.
    private fun attackTarget() {
        val fakePlayer = tree.clientInstance.fakePlayer
        nextClick = timeMillis() + fakePlayer.random.nextGaussian(meanDelay.toDouble(), deviation.toDouble()).toLong()
        pressButton(25, MouseButton.Type.LEFT_CLICK)
    }

    // Selects the best melee weapon (highest attack damage) from the 36-slot inventory.
    private fun getBestMeleeWeaponSlot(): Int {
        var bestSlot = 0
        var damage = 0.0
        val fakePlayer = tree.clientInstance.fakePlayer
        val inventory = fakePlayer.inventory
        for (i in 0..35) {
            val itemStack = inventory.getItemStackAt(i) ?: continue
            if (itemStack.attackDamage > damage) {
                bestSlot = i
                damage = itemStack.attackDamage
            }
        }
        return bestSlot
    }

    // Helper to determine if a given item stack is the best melee weapon.
    private fun isBestMeleeWeapon(itemStack: ItemStack): Boolean {
        val bestSlot = getBestMeleeWeaponSlot()
        val inventory = tree.clientInstance.fakePlayer.inventory
        val bestDamage = inventory.getItemStackAt(bestSlot)?.attackDamage ?: 0.0
        return itemStack.attackDamage == bestDamage
    }

    // Strafes left or right around the target.
    private fun strafe() {
        val tgt = target ?: return
        val fakePlayer = tree.clientInstance.fakePlayer
        val distance = fakePlayer.distance3DTo(tgt)
        if (!fakePlayer.isOnGround || distance > 2.95) {
            unpressKey(Key.Type.KEY_D, Key.Type.KEY_A)
            return
        }
        when (strafeDirection(tgt)) {
            1.toByte() -> {
                unpressKey(Key.Type.KEY_D)
                pressKey(Key.Type.KEY_A)
            }

            2.toByte() -> {
                unpressKey(Key.Type.KEY_A)
                pressKey(Key.Type.KEY_D)
            }
        }
    }

    // Determines strafe direction based on target position.
    private fun strafeDirection(target: ClientPlayer): Byte {
        val fakePlayer = tree.clientInstance.fakePlayer
        val toPlayer = doubleArrayOf(
            fakePlayer.x - target.x,
            fakePlayer.y - target.y,
            fakePlayer.z - target.z
        )
        val aimVector = vectorForRotation(target.pitch, target.yaw)
        val cross = crossProduct2D(toPlayer, aimVector)
        return if (cross > 0) 2 else 1
    }

    private fun crossProduct2D(vec: DoubleArray, other: DoubleArray): Float {
        return (vec[0] * other[2] - vec[2] * other[0]).toFloat()
    }

    override val child: ChildNode = sequence(tree) {
        // Ensure the best melee weapon is in the hotbar using moveToHotbar.
        selector {
            condition {
                val slot = getBestMeleeWeaponSlot()
                tree.clientInstance.fakePlayer.inventory.heldSlot == slot
            }
            leaf {
                moveToHotbar { isBestMeleeWeapon(this) }
            }
        }
        // Ensure inventory is closed.
        selector {
            condition { tree.clientInstance.currentScreen !is ContainerScreen }
            leaf {
                pressKey(10, Key.Type.KEY_ESCAPE)
                BTResult.SUCCESS
            }
        }
        // Acquire target, adjust aim and attack.
        leaf({
            findTarget()
            aimAtTarget()
            BTResult.SUCCESS
        }, onFrame = {
            strafe()
            if (timeMillis() >= nextClick)
                attackTarget()
            BTResult.SUCCESS
        }, onEvent = {
            if (it is EntityHurtEvent) return@leaf onEntityHurt(it)
            BTResult.SUCCESS
        })

        // Reflect off wall if no target and colliding.
        leaf {
            if (target == null && timeMillis() - lastBounceTime > 1000 && isCollidingWithWall) reflectOffWall()
            BTResult.SUCCESS
        }
    }

    private fun onEntityHurt(event: EntityHurtEvent): BTResult {
        val entity = event.attackedEntity

        val fakePlayer = clientInstance.fakePlayer
        if (entity.y - fakePlayer.y > 1.5) return BTResult.SUCCESS

        val target = this.target

        if (target != null && entity.uuid == target.uuid) sprintReset()

        return BTResult.FAILURE
    }

    internal enum class ResetType {
        EXTRA_OFFENSIVE, OFFENSIVE, DEFENSIVE, EXTRA_DEFENSIVE
    }

    private fun getKB(entity: ClientLivingEntity, meanX: Double, meanY: Double, meanZ: Double): Double {
        val motX = entity.x - entity.lastX
        val motY = entity.y - entity.lastY
        val motZ = entity.z - entity.lastZ

        val toEntityX = entity.x - meanX
        val toEntityY = entity.y - meanY
        val toEntityZ = entity.z - meanZ

        val dot = motX * toEntityX + motY * toEntityY + motZ * toEntityZ

        return if (dot > 0) sqrt(motX * motX + motY * motY + motZ * motZ) else 0.0
    }

    private fun sprintReset() {
        val target = this.target ?: return

        val fakePlayer = clientInstance.fakePlayer
        val meanX = (fakePlayer.x + target.x) / 2
        val meanY = (fakePlayer.y + target.y) / 2
        val meanZ = (fakePlayer.z + target.z) / 2

        // Offensive if dealing more kb to the target
        val kb = getKB(fakePlayer, meanX, meanY, meanZ)
        val targetKB = getKB(target, meanX, meanY, meanZ)

        val inventory = fakePlayer.inventory
        val itemStack = inventory.heldItemStack

        resetType =
            if (kb < targetKB) if (fakePlayer.isOnGround && kb <= 0) ResetType.EXTRA_OFFENSIVE else ResetType.OFFENSIVE
            else if (lastResetType == ResetType.DEFENSIVE && itemStack != null && Item.Type.SWORD.isType(itemStack.item.id)) ResetType.EXTRA_DEFENSIVE
            else ResetType.DEFENSIVE

        val config = clientInstance.configuration

        when (resetType) {
            ResetType.EXTRA_OFFENSIVE -> {
                if (config.sprintResetAccuracy >= 1
                    || fakePlayer.random.nextFloat() < config
                        .sprintResetAccuracy
                ) {
                    pressKey(150, Key.Type.KEY_S)
                    unpressKey(150, Key.Type.KEY_W)
                }
            }

            ResetType.OFFENSIVE -> if (config.sprintResetAccuracy >= 1
                || fakePlayer.random.nextFloat() < config
                    .sprintResetAccuracy
            ) unpressKey(150, Key.Type.KEY_W)

            ResetType.DEFENSIVE -> if (config.sprintResetAccuracy >= 1
                || fakePlayer.random.nextFloat() < config
                    .sprintResetAccuracy
            ) unpressKey(100, Key.Type.KEY_W)

            ResetType.EXTRA_DEFENSIVE -> if (config.sprintResetAccuracy >= 1
                || fakePlayer.random.nextFloat() < config
                    .sprintResetAccuracy
            ) pressButton(75, MouseButton.Type.RIGHT_CLICK)
        }
    }
}
