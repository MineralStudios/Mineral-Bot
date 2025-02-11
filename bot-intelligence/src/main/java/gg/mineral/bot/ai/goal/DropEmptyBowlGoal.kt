package gg.mineral.bot.ai.goal

import gg.mineral.bot.ai.goal.type.InventoryGoal
import gg.mineral.bot.api.controls.Key
import gg.mineral.bot.api.event.Event
import gg.mineral.bot.api.instance.ClientInstance
import gg.mineral.bot.api.inv.item.Item

class DropEmptyBowlGoal(clientInstance: ClientInstance) : InventoryGoal(clientInstance) {
    override fun shouldExecute(): Boolean {
        val fakePlayer = clientInstance.fakePlayer
        val inventory = fakePlayer.inventory ?: return false

        return inventory.contains(Item.BOWL)
    }

    override fun isExecuting() = inventoryOpen

    private fun dropBowl() = pressKey(10, Key.Type.KEY_Q)

    private fun switchToBowl() {
        var soupSlot = -1
        val fakePlayer = clientInstance.fakePlayer
        val inventory = fakePlayer.inventory ?: return

        for (i in 0..8) {
            val itemStack = inventory.getItemStackAt(i) ?: continue
            val item = itemStack.item
            if (item.id == Item.MUSHROOM_STEW) {
                soupSlot = i
                break
            }
        }

        if (inventoryOpen) {
            inventoryOpen = false
            pressKey(10, Key.Type.KEY_ESCAPE)
            info(this, "Closing inventory after switching to soup")
            return
        }

        pressKey(10, Key.Type.valueOf("KEY_" + (soupSlot + 1)))
    }

    override fun onTick() {
        val fakePlayer = clientInstance.fakePlayer
        val inventory = fakePlayer.inventory ?: return

        val itemStack = inventory.heldItemStack

        if (!delayedTasks.isEmpty()) return

        if (itemStack != null && itemStack.item.id == Item.BOWL) schedule({ this.dropBowl() }, 100)
        else schedule({ this.switchToBowl() }, 100)
    }

    override fun onEvent(event: Event) = false

    override fun onGameLoop() {}
}
