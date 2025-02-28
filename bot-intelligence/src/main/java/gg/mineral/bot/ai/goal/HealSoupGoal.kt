package gg.mineral.bot.ai.goal

import gg.mineral.bot.ai.goal.type.InventoryGoal
import gg.mineral.bot.api.controls.Key
import gg.mineral.bot.api.controls.MouseButton
import gg.mineral.bot.api.entity.living.player.ClientPlayer
import gg.mineral.bot.api.event.Event
import gg.mineral.bot.api.event.peripherals.MouseButtonEvent
import gg.mineral.bot.api.goal.Sporadic
import gg.mineral.bot.api.goal.Timebound
import gg.mineral.bot.api.instance.ClientInstance
import gg.mineral.bot.api.inv.item.Item

class HealSoupGoal(clientInstance: ClientInstance) : InventoryGoal(clientInstance), Sporadic, Timebound {
    override val maxDuration: Long = 100
    override var startTime: Long = 0
    override var executing: Boolean = false

    override fun shouldExecute(): Boolean {
        val fakePlayer = clientInstance.fakePlayer
        val inventory = fakePlayer.inventory

        return fakePlayer.health <= 10 && inventory.contains(Item.MUSHROOM_STEW)
    }

    override fun onStart() {
        pressKey(Key.Type.KEY_W, Key.Type.KEY_LCONTROL)
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

    private fun getSoupSlot(): Int {
        var soupSlot = -1
        val fakePlayer = clientInstance.fakePlayer
        val inventory = fakePlayer.inventory

        for (i in 0..35) {
            val itemStack = inventory.getItemStackAt(i) ?: continue
            val item = itemStack.item
            if (item.id == Item.MUSHROOM_STEW) {
                soupSlot = i
                break
            }
        }

        return soupSlot
    }

    override fun onTick(tick: Tick) {
        aimAtOptimalTarget()
        val soupSlot = getSoupSlot()
        val fakePlayer = clientInstance.fakePlayer
        val inventory = fakePlayer.inventory

        tick.finishIf("No Valid Soup Found", soupSlot == -1)

        tick.finishIf("Soup Not Needed", fakePlayer.health > 10)

        tick.prerequisite("In Hotbar", soupSlot <= 8) {
            moveItemToHotbar(soupSlot, inventory)
        }

        tick.prerequisite("Inventory Closed", !inventoryOpen) { inventoryOpen = false }

        tick.prerequisite("Correct Hotbar Slot Selected", inventory.heldSlot == soupSlot) {
            pressKey(10, Key.Type.valueOf("KEY_" + (soupSlot + 1)))
        }

        tick.finishIf("Not Holding Valid Soup", inventory.heldItemStack?.item?.id != Item.MUSHROOM_STEW)

        tick.execute {
            pressButton(10, MouseButton.Type.RIGHT_CLICK)
        }
    }

    override fun onEnd() {
        if (inventoryOpen) {
            inventoryOpen = false
        }
    }

    override fun onEvent(event: Event): Boolean {
        if (event is MouseButtonEvent && inventoryOpen && event.type == MouseButton.Type.LEFT_CLICK && event.pressed) {
            logger.debug("Ignoring LEFT_CLICK press event")
            return true
        }
        return false
    }

    override fun onGameLoop() {}
}
