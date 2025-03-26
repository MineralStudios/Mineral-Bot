package gg.mineral.bot.ai.goal.type

import gg.mineral.bot.api.controls.Key
import gg.mineral.bot.api.goal.Goal
import gg.mineral.bot.api.instance.ClientInstance
import gg.mineral.bot.api.inv.Inventory
import gg.mineral.bot.api.screen.type.ContainerScreen

abstract class InventoryGoal(clientInstance: ClientInstance) : Goal(clientInstance) {
    fun moveItemToHotbar(index: Int, inventory: Inventory, moveIndex: Int = 8) {
        val fakePlayer = clientInstance.fakePlayer

        val screen = clientInstance.currentScreen

        val inventoryContainer = fakePlayer.inventoryContainer

        val slot = inventoryContainer.getSlot(inventory, index) ?: run {
            pressKey(10, Key.Type.KEY_ESCAPE)
            return logger.debug("Slot is null; closing inventory")
        }

        if (screen is ContainerScreen) {
            val guiSlotX = screen.getSlotXScaled(slot, clientInstance.displayWidth)
            val guiSlotY = screen.getSlotYScaled(slot, clientInstance.displayHeight)

            val currentX = clientInstance.mouse.x
            val currentY = clientInstance.mouse.y

            if (currentX != guiSlotX || currentY != guiSlotY) {
                clientInstance.mouse.setCursorPosition(guiSlotX, guiSlotY)
                logger.debug("Moving mouse to slot at ($guiSlotX, $guiSlotY)")
            } else {
                pressKey(10, Key.Type.valueOf("KEY_" + (moveIndex + 1)))
                logger.debug("Swapped item to hotbar slot.")
            }
        } else pressKey(10, Key.Type.KEY_E)
    }
}