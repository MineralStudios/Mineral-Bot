package gg.mineral.bot.ai.goal

import gg.mineral.bot.ai.goal.type.InventoryGoal
import gg.mineral.bot.api.controls.Key
import gg.mineral.bot.api.controls.MouseButton
import gg.mineral.bot.api.event.Event
import gg.mineral.bot.api.instance.ClientInstance
import gg.mineral.bot.api.inv.item.Item

class ReplaceArmorGoal(clientInstance: ClientInstance) : InventoryGoal(clientInstance) {
    override fun shouldExecute(): Boolean {
        if (inventoryOpen) return true
        // TODO: don't replace armor if eating gapple or drinking potion

        val fakePlayer = clientInstance.fakePlayer
        val inventory = fakePlayer.inventory ?: return false

        val missingArmorPiece = missingArmorPiece()

        return canSeeEnemy() && missingArmorPiece != Item.Type.NONE && inventory.contains(missingArmorPiece)
    }

    private fun missingArmorPiece(): Item.Type {
        val fakePlayer = clientInstance.fakePlayer
        val inventory = fakePlayer.inventory ?: return Item.Type.NONE

        val helmet = inventory.helmet
        val chestplate = inventory.chestplate
        val leggings = inventory.leggings
        val boots = inventory.boots

        if (helmet == null) return Item.Type.HELMET
        if (chestplate == null) return Item.Type.CHESTPLATE
        if (leggings == null) return Item.Type.LEGGINGS

        return if (boots == null) Item.Type.BOOTS else Item.Type.NONE
    }

    private fun canSeeEnemy(): Boolean {
        val fakePlayer = clientInstance.fakePlayer
        val world = fakePlayer.world
        return world != null && world.entities.any {
            !clientInstance.configuration.friendlyUUIDs
                .contains(it.uuid)
        }
    }

    private fun applyArmor() = pressButton(10, MouseButton.Type.RIGHT_CLICK)

    private fun switchToArmor() {
        var armorSlot = -1
        val fakePlayer = clientInstance.fakePlayer
        val inventory = fakePlayer.inventory ?: return

        val missingArmorPiece = missingArmorPiece()

        // Search hotbar
        for (i in 0..35) {
            val itemStack = inventory.getItemStackAt(i) ?: continue
            val item = itemStack.item
            if (missingArmorPiece.isType(item.id)) {
                armorSlot = i
                break
            }
        }

        if (armorSlot > 8) return moveItemToHotbar(armorSlot, inventory)

        if (inventoryOpen) {
            inventoryOpen = false
            pressKey(10, Key.Type.KEY_ESCAPE)
            info(this, "Closing inventory after switching to armor")
            return
        }

        pressKey(10, Key.Type.valueOf("KEY_" + (armorSlot + 1)))
    }

    override fun onTick() {
        val fakePlayer = clientInstance.fakePlayer
        val inventory = fakePlayer.inventory ?: return

        val itemStack = inventory.heldItemStack

        val missingArmorPiece = missingArmorPiece()

        if (!delayedTasks.isEmpty()) return

        if (itemStack != null && missingArmorPiece.isType(itemStack.item.id)) schedule({ this.applyArmor() }, 100)
        else schedule({ this.switchToArmor() }, 100)
    }

    override fun onEvent(event: Event) = false

    override fun onGameLoop() {}
}
