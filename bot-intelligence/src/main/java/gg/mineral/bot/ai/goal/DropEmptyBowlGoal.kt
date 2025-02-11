package gg.mineral.bot.ai.goal

import gg.mineral.bot.api.controls.Key
import gg.mineral.bot.api.event.Event
import gg.mineral.bot.api.goal.Goal
import gg.mineral.bot.api.instance.ClientInstance
import gg.mineral.bot.api.inv.item.Item

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

    override fun onTick() {
        val fakePlayer = clientInstance.fakePlayer
        val inventory = fakePlayer.inventory ?: return

        val itemStack = inventory.heldItemStack

        if (itemStack != null && itemStack.item.id == Item.BOWL) this.dropBowl()
        else if (delayedTasks.isEmpty()) schedule({ this.switchToBowl() }, 100)
    }

    override fun onEvent(event: Event) = false

    override fun onGameLoop() {}
}
