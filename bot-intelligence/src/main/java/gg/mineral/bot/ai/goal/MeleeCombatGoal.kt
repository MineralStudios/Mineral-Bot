package gg.mineral.bot.ai.goal

import gg.mineral.bot.ai.goal.type.InventoryGoal
import gg.mineral.bot.api.controls.Key
import gg.mineral.bot.api.controls.MouseButton
import gg.mineral.bot.api.entity.ClientEntity
import gg.mineral.bot.api.entity.living.ClientLivingEntity
import gg.mineral.bot.api.entity.living.player.ClientPlayer
import gg.mineral.bot.api.event.Event
import gg.mineral.bot.api.event.entity.EntityHurtEvent
import gg.mineral.bot.api.instance.ClientInstance
import gg.mineral.bot.api.inv.item.Item
import gg.mineral.bot.api.screen.type.ContainerScreen
import gg.mineral.bot.api.world.block.Block

class MeleeCombatGoal(clientInstance: ClientInstance) : InventoryGoal(clientInstance) {
    private var target: ClientPlayer? = null

    private val meanDelay = (1000 / clientInstance.configuration.averageCps).toLong()
    private val deviation =
        kotlin.math.abs(((1000 / (clientInstance.configuration.averageCps + 1)).toLong() - meanDelay).toDouble())
            .toLong()
    private var lastBounceTime: Long = 0
    private var lastTargetSwitchTick = 0
    private var lastSprintResetTick = 0
    private var currentHorizontalAimAcceleration = 1.0f
    private var currentVerticalAimAcceleration = 1.0f
    private var lastHorizontalTurnSignum = 1
    private var lastVerticalTurnSignum = 1

    override fun shouldExecute() = true

    private fun findTarget() {
        val targetSearchRange = clientInstance.configuration.targetSearchRange

        val fakePlayer = clientInstance.fakePlayer
        val world = fakePlayer.world

        val entities = world.entities

        if (clientInstance.currentTick - lastTargetSwitchTick < 20 && target?.let {
                entities.contains(it) && isTargetValid(
                    it,
                    targetSearchRange.toFloat()
                )
            } == true
        ) return

        var closestTarget: ClientPlayer? = null
        var closestDistance = Double.MAX_VALUE

        for (entity in entities) {
            if (entity is ClientPlayer) {
                if (isTargetValid(entity, targetSearchRange.toFloat())) {
                    val distance = fakePlayer.distance3DTo(entity)
                    if (distance < closestDistance) {
                        closestDistance = distance
                        closestTarget = entity
                    }
                }
            }
        }

        if (closestTarget !== this.target) {
            lastTargetSwitchTick = clientInstance.currentTick
            this.target = closestTarget
        }
    }

    private fun isTargetValid(entity: ClientLivingEntity, range: Float): Boolean {
        val fakePlayer = clientInstance.fakePlayer
        return !clientInstance.configuration.friendlyUUIDs.contains(entity.uuid) && fakePlayer.distance3DTo(entity) <= range && entity is ClientPlayer
    }

    private fun getRotationTarget(
        current: Float,
        target: Float,
        turnSpeed: Float,
        accuracy: Float,
        erraticness: Float
    ): Float {
        val difference = angleDifference(current, target)

        if (abs(difference.toDouble()) > turnSpeed) return current + signum(difference) * turnSpeed

        if (accuracy >= 1) return target

        val deviation = 3f / max(0.01f, accuracy)
        val fakePlayer = clientInstance.fakePlayer
        val newTarget = fakePlayer.random.nextGaussian(target.toDouble(), deviation.toDouble()).toFloat()
        val newDifference = angleDifference(current, newTarget)

        val erraticnessFactor = min(180 * erraticness, turnSpeed)

        if (abs(newDifference.toDouble()) > erraticnessFactor) return current + signum(newDifference) * erraticnessFactor
        return newTarget
    }

    private fun aimAtTarget() {
        val target = this.target ?: return

        val fakePlayer = clientInstance.fakePlayer
        val optimalAngles = computeOptimalYawAndPitch(fakePlayer, target)

        if (fakePlayer.distance3DTo(target) > 6.0f) {
            setMouseYaw(optimalAngles[1])
            setMousePitch(optimalAngles[0])
            return
        }

        val yawDiff = abs(
            angleDifference(
                fakePlayer.yaw,
                optimalAngles[1]
            ).toDouble()
        )
        val pitchDiff = abs(
            angleDifference(
                fakePlayer.pitch,
                optimalAngles[0]
            ).toDouble()
        )

        val distX = abs(fakePlayer.x - target.x)
        val distZ = abs(fakePlayer.z - target.z)

        val yawSpeed =
            calculateHorizontalAimSpeed(sqrt(distX * distX + distZ * distZ), yawDiff) * currentHorizontalAimAcceleration
        val pitchSpeed = calculateVerticalAimSpeed(pitchDiff) * currentVerticalAimAcceleration

        val config = clientInstance.configuration

        val currentHorizontalTurnSignum = signum(
            angleDifference(fakePlayer.yaw, optimalAngles[1])
        ).toInt()

        if (currentHorizontalTurnSignum != lastHorizontalTurnSignum || currentHorizontalTurnSignum == 0) currentHorizontalAimAcceleration =
            1.0f
        else currentHorizontalAimAcceleration *= config.horizontalAimAcceleration

        setMouseYaw(
            getRotationTarget(
                fakePlayer.yaw, optimalAngles[1],
                abs(
                    (yawSpeed
                            * config.horizontalAimSpeed * 2.0)
                ).toFloat(),
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
                abs(
                    (pitchSpeed
                            * config.verticalAimSpeed * 2.0)
                ).toFloat(),
                verAccuracy,
                config.verticalErraticness
            )
        )

        lastVerticalTurnSignum = currentVerticalTurnSignum
    }

    private fun calculateHorizontalAimSpeed(distHorizontal: Double, yawDiff: Double): Double {
        val yawDiffFactor = 1
        val distHorizontalFactor = 1

        val distanceFactorEquation = (10 / ((5 * distHorizontal.coerceAtLeast(0.2)) - 1)) + 2
        val diffFactorEquation = yawDiff / 5
        return ((distHorizontalFactor * distanceFactorEquation)
                + (yawDiffFactor * diffFactorEquation))
    }

    private val isCollidingWithWall: Boolean
        get() {
            val fakePlayer = clientInstance.fakePlayer
            val world = fakePlayer.world

            val posX = fakePlayer.x
            val posY = fakePlayer.y + fakePlayer.eyeHeight
            val posZ = fakePlayer.z
            val yaw = fakePlayer.yaw
            val pitch = 0f

            val checkDistance = 1.0

            // Check for collision in the direction the bot is facing
            val dir = vectorForRotation(pitch, yaw)
            val checkX = posX + dir[0] * checkDistance
            val checkY = posY + dir[1] * checkDistance
            val checkZ = posZ + dir[2] * checkDistance

            val block = world.getBlockAt(checkX, checkY, checkZ)
            return block.id != Block.AIR
        }

    private val collisionNormal: DoubleArray?
        get() {
            val fakePlayer = clientInstance.fakePlayer
            val world = fakePlayer.world

            val posX = fakePlayer.x
            val posY = fakePlayer.y + fakePlayer.eyeHeight

            // Sampling multiple points around the bot
            val sampleCount = 8
            val checkDistance = 0.5
            val normal = doubleArrayOf(0.0, 0.0, 0.0)

            for (i in 0..<sampleCount) {
                val angle = fakePlayer.yaw + (360.0 / sampleCount * i).toFloat()
                val dir = vectorForRotation(0f, angle)

                val checkX = posX + dir[0] * checkDistance
                val checkZ = fakePlayer.z + dir[2] * checkDistance

                val block = world.getBlockAt(checkX, posY, checkZ)
                if (block.id != Block.AIR) {
                    // Add the opposite of the direction to the normal
                    normal[0] += -dir[0]
                    normal[1] += 0.0 // We ignore Y for horizontal normal
                    normal[2] += -dir[2]
                }
            }

            val length = sqrt(normal[0] * normal[0] + normal[2] * normal[2])
            if (length == 0.0) return null

            // Normalize the normal vector
            normal[0] /= length
            normal[2] /= length

            return normal
        }

    private fun reflectOffWall() {
        val fakePlayer = clientInstance.fakePlayer
        val normal = collisionNormal ?: return

        // Normalize the normal vector (should already be normalized)
        val normX = normal[0]
        val normZ = normal[2]

        // Get direction vector
        val yaw = fakePlayer.yaw

        // Convert yaw to radians
        val yawRad = Math.toRadians(yaw.toDouble())

        // Direction vector in x and z
        val dirX = -kotlin.math.sin(yawRad)
        val dirZ = kotlin.math.cos(yawRad)

        // Perform reflection R = V - 2(V ⋅ N)N
        val dot = dirX * normX + dirZ * normZ // Only x and z components
        val reflectedX = dirX - 2 * dot * normX
        val reflectedZ = dirZ - 2 * dot * normZ

        val newYaw = toDegrees(fastArcTan2(-reflectedX, reflectedZ)).toFloat()

        setMouseYaw(newYaw)

        this.lastBounceTime = timeMillis()
    }

    private fun calculateVerticalAimSpeed(pitchDiff: Double): Double {
        val pitchDiffFactor = 1
        val diffFactorEquation = 2 * pitchDiff / 5
        return pitchDiffFactor * diffFactorEquation
    }

    private var nextClick: Long = 0

    private fun attackTarget() {
        val fakePlayer = clientInstance.fakePlayer
        nextClick = (timeMillis() + fakePlayer.random.nextGaussian(meanDelay.toDouble(), deviation.toDouble())).toLong()
        pressButton(25, MouseButton.Type.LEFT_CLICK)
    }

    private var resetType = ResetType.OFFENSIVE
    private val lastResetType = ResetType.OFFENSIVE
    private var strafeDirection: Byte = 0

    private fun strafe() {
        val target = this.target ?: return

        val fakePlayer = clientInstance.fakePlayer
        val distance = fakePlayer.distance3DTo(target)
        if (!fakePlayer.isOnGround || distance > 2.95 /*
                                                         * || timeMillis() -
                                                         * fakePlayer.
                                                         * getLastHitSelected
                                                         * ()
                                                         * < 1000
                                                         */) {
            unpressKey(Key.Type.KEY_D, Key.Type.KEY_A)
            return
        }

        strafeDirection = strafeDirection(target)

        when (strafeDirection.toInt()) {
            1 -> {
                unpressKey(Key.Type.KEY_D)
                pressKey(Key.Type.KEY_A)
            }

            2 -> {
                unpressKey(Key.Type.KEY_A)
                pressKey(Key.Type.KEY_D)
            }
        }
    }

    private fun strafeDirection(target: ClientEntity): Byte {
        val fakePlayer = clientInstance.fakePlayer
        val toPlayer = doubleArrayOf(
            fakePlayer.x - target.x,
            fakePlayer.y - target.y,
            fakePlayer.z - target.z
        )
        val aimVector = vectorForRotation(
            target.pitch,
            target.yaw
        )

        val crossProduct = crossProduct2D(toPlayer, aimVector)

        // If cross product is positive, player is to the right of the aim direction
        // If cross product is negative, player is to the left of the aim direction
        return if (crossProduct > 0) 2.toByte() else 1
    }

    private fun crossProduct2D(vec: DoubleArray, other: DoubleArray): Float {
        return (vec[0] * other[2] - vec[2] * other[0]).toFloat()
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

        val dist = fakePlayer.distance3DTo(target)

        val inventory = fakePlayer.inventory
        val itemStack = inventory.heldItemStack

        resetType =
            if (kb < targetKB) if (dist < 2 && fakePlayer.isOnGround) ResetType.EXTRA_OFFENSIVE else ResetType.OFFENSIVE
            else if (lastResetType == ResetType.DEFENSIVE && itemStack != null && itemStack.item.id == Item.DIAMOND_SWORD) ResetType.EXTRA_DEFENSIVE
            else ResetType.DEFENSIVE

        val config = clientInstance.configuration

        val runnable = Runnable {
            when (resetType) {
                ResetType.EXTRA_OFFENSIVE -> {
                    if (config.sprintResetAccuracy >= 1
                        || fakePlayer.random.nextFloat() < config
                            .sprintResetAccuracy
                    ) pressKey(150, Key.Type.KEY_S)
                    if (config.sprintResetAccuracy >= 1
                        || fakePlayer.random.nextFloat() < config
                            .sprintResetAccuracy
                    ) unpressKey(150, Key.Type.KEY_W)
                }

                ResetType.DEFENSIVE, ResetType.OFFENSIVE -> if (config.sprintResetAccuracy >= 1
                    || fakePlayer.random.nextFloat() < config
                        .sprintResetAccuracy
                ) unpressKey(150, Key.Type.KEY_W)

                ResetType.EXTRA_DEFENSIVE -> if (config.sprintResetAccuracy >= 1
                    || fakePlayer.random.nextFloat() < config
                        .sprintResetAccuracy
                ) pressButton(75, MouseButton.Type.RIGHT_CLICK)
            }
        }

        if (resetType == ResetType.OFFENSIVE || resetType == ResetType.EXTRA_OFFENSIVE) {
            runnable.run()
            return
        }

        schedule(runnable, 350)
    }

    private fun getKB(entity: ClientLivingEntity, meanX: Double, meanY: Double, meanZ: Double): Double {
        val motX = entity.x - entity.lastX
        val motY = entity.y - entity.lastY
        val motZ = entity.z - entity.lastZ

        val newX = entity.x + motX
        val newY = entity.y + motY
        val newZ = entity.z + motZ

        val kbX = newX - meanX
        val kbY = newY - meanY
        val kbZ = newZ - meanZ

        return sqrt(kbX * kbX + kbY * kbY + kbZ * kbZ)
    }

    internal enum class ResetType {
        EXTRA_OFFENSIVE, OFFENSIVE, DEFENSIVE, EXTRA_DEFENSIVE
    }

    private fun getBestMeleeWeaponSlot(): Int {
        var bestMeleeWeaponSlot = 0
        var damage = 0.0
        val fakePlayer = clientInstance.fakePlayer
        val inventory = fakePlayer.inventory

        // Look for a non-splash potion in one of the 36 slots
        invLoop@ for (i in 0..35) {
            val itemStack = inventory.getItemStackAt(i) ?: continue
            val attackDamage = itemStack.attackDamage
            if (attackDamage > damage) {
                bestMeleeWeaponSlot = i
                damage = attackDamage
            }
        }

        return bestMeleeWeaponSlot
    }

    override fun onTick(tick: Tick) {

        pressKey(Key.Type.KEY_W, Key.Type.KEY_LCONTROL)


        val meleeWeaponSlot = getBestMeleeWeaponSlot()
        val fakePlayer = clientInstance.fakePlayer
        val inventory = fakePlayer.inventory

        tick.prerequisite("In Hotbar", meleeWeaponSlot <= 8) {
            moveItemToHotbar(meleeWeaponSlot, inventory)
        }

        tick.prerequisite("Inventory Closed", clientInstance.currentScreen !is ContainerScreen) {
            pressKey(
                10,
                Key.Type.KEY_ESCAPE
            )
        }

        tick.prerequisite("Correct Hotbar Slot Selected", inventory.heldSlot == meleeWeaponSlot) {
            pressKey(10, Key.Type.valueOf("KEY_" + (meleeWeaponSlot + 1)))
        }

        tick.execute {
            findTarget()
            aimAtTarget()
        }

        if (this.target == null && timeMillis() - lastBounceTime > 1000) if (isCollidingWithWall) reflectOffWall()
    }

    override fun onEvent(event: Event): Boolean {
        if (event is EntityHurtEvent) return onEntityHurt(event)
        return false
    }

    fun onEntityHurt(event: EntityHurtEvent): Boolean {
        if (clientInstance.currentTick - lastSprintResetTick < 9) return false

        val entity = event.attackedEntity

        val fakePlayer = clientInstance.fakePlayer
        if (entity.y - fakePlayer.y > 1.5) return false

        val target = this.target

        if (target != null && entity.uuid == target.uuid) {
            sprintReset()
            lastSprintResetTick = clientInstance.currentTick
        }

        return false
    }

    public override fun onGameLoop() {
        strafe()
        if (timeMillis() >= nextClick) attackTarget()
    }
}
