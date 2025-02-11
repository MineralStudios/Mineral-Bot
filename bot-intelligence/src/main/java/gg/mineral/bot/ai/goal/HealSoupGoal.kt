package gg.mineral.bot.ai.goal

import gg.mineral.bot.ai.goal.type.InventoryGoal
import gg.mineral.bot.api.controls.Key
import gg.mineral.bot.api.controls.MouseButton
import gg.mineral.bot.api.event.Event
import gg.mineral.bot.api.instance.ClientInstance
import gg.mineral.bot.api.inv.item.Item

class HealSoupGoal(clientInstance: ClientInstance) : InventoryGoal(clientInstance) {
    override fun shouldExecute(): Boolean {
        val fakePlayer = clientInstance.fakePlayer
        val inventory = fakePlayer.inventory ?: return false

        return fakePlayer.health <= 10 && inventory.contains(Item.MUSHROOM_STEW)
    }

    override fun isExecuting() = inventoryOpen

    private fun eatSoup() = pressButton(10, MouseButton.Type.RIGHT_CLICK)

    private fun switchToSoup() {
        var soupSlot = -1
        val fakePlayer = clientInstance.fakePlayer
        val inventory = fakePlayer.inventory ?: return

        for (i in 0..35) {
            val itemStack = inventory.getItemStackAt(i) ?: continue
            val item = itemStack.item
            if (item.id == Item.MUSHROOM_STEW) {
                soupSlot = i
                break
            }
        }

        if (soupSlot > 8) return moveItemToHotbar(soupSlot, inventory)

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

        if (itemStack != null && itemStack.item.id == Item.MUSHROOM_STEW) schedule({ this.eatSoup() }, 100)
        else schedule({ this.switchToSoup() }, 100)
    }

    override fun onEvent(event: Event) = false

    override fun onGameLoop() {}
}
