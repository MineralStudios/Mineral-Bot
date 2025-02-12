package gg.mineral.bot.ai.goal

import gg.mineral.bot.api.controls.Key
import gg.mineral.bot.api.entity.living.player.ClientPlayer
import gg.mineral.bot.api.event.Event
import gg.mineral.bot.api.goal.Goal
import gg.mineral.bot.api.instance.ClientInstance
import gg.mineral.bot.api.inv.item.Item
import kotlin.math.atan2

class DropEmptyBowlGoal(clientInstance: ClientInstance) : Goal(clientInstance) {
    override fun shouldExecute(): Boolean {
        val fakePlayer = clientInstance.fakePlayer
        val inventory = fakePlayer.inventory ?: return false

        for (i in 0..8) {
            val itemStack = inventory.getItemStackAt(i) ?: continue
            val item = itemStack.item
            if (item.id == Item.BOWL) return true
        }
        return false
    }

    override fun isExecuting() = false

    private fun dropBowl() = pressKey(10, Key.Type.KEY_Q)

    private fun switchToBowl() {
        var bowlSlot = -1
        val fakePlayer = clientInstance.fakePlayer
        val inventory = fakePlayer.inventory ?: return

        for (i in 0..8) {
            val itemStack = inventory.getItemStackAt(i) ?: continue
            val item = itemStack.item
            if (item.id == Item.BOWL) {
                bowlSlot = i
                break
            }
        }

        pressKey(10, Key.Type.valueOf("KEY_" + (bowlSlot + 1)))
    }

    // ─── NEW AIMING LOGIC ──────────────────────────────────────────────
    /**
     * Returns the optimal target (an enemy player) within the search range.
     * The criteria here is similar to the melee combat goal – the closest enemy
     * that is not marked as friendly.
     */
    private fun getOptimalTarget(): ClientPlayer? {
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

    /**
     * A simple implementation that calculates the yaw and pitch the bot should have
     * in order to directly aim at the target. (Your bot may use a more sophisticated
     * method than this.)
     *
     * @return A FloatArray where index 0 is pitch and index 1 is yaw.
     */
    private fun computeOptimalYawAndPitch(player: ClientPlayer, target: ClientPlayer): FloatArray {
        val dx = target.x - player.x
        // Use eye height so that the aim is toward the head area
        val dy = (target.y + target.eyeHeight) - (player.y + player.eyeHeight)
        val dz = target.z - player.z
        val distance = sqrt(dx * dx + dz * dz)
        // Pitch is negative because in many games looking upward decreases the pitch value
        val pitch = (-Math.toDegrees(atan2(dy, distance))).toFloat()
        // Yaw calculation (adjusted by -90 to convert from mathematical to game coordinates)
        val yaw = (Math.toDegrees(atan2(dz, dx)) - 90).toFloat()
        return floatArrayOf(pitch, yaw)
    }

    /**
     * Updates the bot's aim so that it points at the optimal target.
     * This is called every tick while the goal is active.
     */
    private fun aimAtOptimalTarget() {
        val fakePlayer = clientInstance.fakePlayer
        val target = getOptimalTarget() ?: return
        val optimalAngles = computeOptimalYawAndPitch(fakePlayer, target)
        setMouseYaw(optimalAngles[1])
        setMousePitch(optimalAngles[0])
    }
    // ────────────────────────────────────────────────────────────────────

    override fun onTick() {
        val fakePlayer = clientInstance.fakePlayer
        val inventory = fakePlayer.inventory ?: return

        pressKey(Key.Type.KEY_W, Key.Type.KEY_LCONTROL)
        aimAtOptimalTarget()

        val itemStack = inventory.heldItemStack

        if (itemStack != null && itemStack.item.id == Item.BOWL) this.dropBowl()
        else if (delayedTasks.isEmpty()) schedule({ this.switchToBowl() }, 50)
    }

    override fun onEvent(event: Event) = false

    override fun onGameLoop() {}
}
