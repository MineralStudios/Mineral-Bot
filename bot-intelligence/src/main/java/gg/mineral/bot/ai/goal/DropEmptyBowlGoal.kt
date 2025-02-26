package gg.mineral.bot.ai.goal

import gg.mineral.bot.api.controls.Key
import gg.mineral.bot.api.entity.living.player.ClientPlayer
import gg.mineral.bot.api.event.Event
import gg.mineral.bot.api.goal.Goal
import gg.mineral.bot.api.goal.Sporadic
import gg.mineral.bot.api.goal.Timebound
import gg.mineral.bot.api.instance.ClientInstance
import gg.mineral.bot.api.inv.item.Item

class DropEmptyBowlGoal(clientInstance: ClientInstance) : Goal(clientInstance), Sporadic, Timebound {
    override var executing: Boolean = false
    override var startTime: Long = 0
    override val maxDuration: Long = 100

    override fun shouldExecute(): Boolean {
        val fakePlayer = clientInstance.fakePlayer
        val inventory = fakePlayer.inventory

        for (i in 0..8) {
            val itemStack = inventory.getItemStackAt(i) ?: continue
            val item = itemStack.item
            if (item.id == Item.BOWL) return true
        }
        return false
    }

    override fun onStart() {
        pressKey(Key.Type.KEY_W, Key.Type.KEY_LCONTROL)
    }

    private fun getBowlSlot(): Int {
        var bowlSlot = -1
        val fakePlayer = clientInstance.fakePlayer
        val inventory = fakePlayer.inventory

        for (i in 0..8) {
            val itemStack = inventory.getItemStackAt(i) ?: continue
            val item = itemStack.item
            if (item.id == Item.BOWL) {
                bowlSlot = i
                break
            }
        }

        return bowlSlot
    }

    // ─── NEW AIMING LOGIC ──────────────────────────────────────────────
    /**
     * Returns the optimal target (an enemy player) within the search range.
     * The criteria here is similar to the melee combat goal – the closest enemy
     * that is not marked as friendly.
     */
    private fun getOptimalTarget(): ClientPlayer? {
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

    override fun onTick(tick: Tick) {
        aimAtOptimalTarget()

        val fakePlayer = clientInstance.fakePlayer
        val inventory = fakePlayer.inventory

        val bowlSlot = getBowlSlot()

        tick.finishIf("Bowl is not in Hotbar", bowlSlot == -1)

        tick.prerequisite("Switch to Bowl Slot", inventory.heldSlot == bowlSlot) {
            pressKey(10, Key.Type.valueOf("KEY_" + (bowlSlot + 1)))
        }

        tick.finishIf("Bowl is not in Hand", inventory.heldItemStack?.item?.id != Item.BOWL)

        tick.execute { pressKey(10, Key.Type.KEY_Q) }
    }

    override fun onEnd() {
    }

    override fun onEvent(event: Event) = false

    override fun onGameLoop() {}
}
