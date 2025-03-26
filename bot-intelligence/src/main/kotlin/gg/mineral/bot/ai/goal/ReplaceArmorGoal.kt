package gg.mineral.bot.ai.goal

import gg.mineral.bot.ai.goal.type.InventoryGoal
import gg.mineral.bot.api.controls.Key
import gg.mineral.bot.api.controls.MouseButton
import gg.mineral.bot.api.event.Event
import gg.mineral.bot.api.goal.Sporadic
import gg.mineral.bot.api.goal.Timebound
import gg.mineral.bot.api.instance.ClientInstance
import gg.mineral.bot.api.inv.item.Item
import gg.mineral.bot.api.screen.type.ContainerScreen

class ReplaceArmorGoal(clientInstance: ClientInstance) : InventoryGoal(clientInstance), Sporadic, Timebound {
    override val maxDuration: Long = 100
    override var startTime: Long = 0
    override var executing: Boolean = false

    override fun shouldExecute(): Boolean {
        val fakePlayer = clientInstance.fakePlayer
        val inventory = fakePlayer.inventory

        val missingArmorPiece = missingArmorPiece()

        return missingArmorPiece != Item.Type.NONE && inventory.contains(missingArmorPiece)
    }

    override fun onStart() {
    }

    private fun missingArmorPiece(): Item.Type {
        val fakePlayer = clientInstance.fakePlayer
        val inventory = fakePlayer.inventory

        val helmet = inventory.helmet
        val chestplate = inventory.chestplate
        val leggings = inventory.leggings
        val boots = inventory.boots

        if (helmet == null) return Item.Type.HELMET
        if (chestplate == null) return Item.Type.CHESTPLATE
        if (leggings == null) return Item.Type.LEGGINGS

        return if (boots == null) Item.Type.BOOTS else Item.Type.NONE
    }

    private fun getArmorSlot(): Int {
        var armorSlot = -1
        val fakePlayer = clientInstance.fakePlayer
        val inventory = fakePlayer.inventory

        val missingArmorPiece = missingArmorPiece()

        for (i in 0..35) {
            val itemStack = inventory.getItemStackAt(i) ?: continue
            val item = itemStack.item
            if (missingArmorPiece.isType(item.id)) {
                armorSlot = i
                break
            }
        }

        return armorSlot
    }

    override fun onTick(tick: Tick) {
        val armorSlot = getArmorSlot()
        val fakePlayer = clientInstance.fakePlayer
        val inventory = fakePlayer.inventory

        tick.finishIf("No Valid Armor Found", armorSlot == -1)

        tick.prerequisite("In Hotbar", armorSlot <= 8) {
            moveItemToHotbar(armorSlot, inventory)
        }

        tick.prerequisite("Inventory Closed", clientInstance.currentScreen !is ContainerScreen) {
            pressKey(10, Key.Type.KEY_ESCAPE)
        }

        tick.prerequisite("Correct Hotbar Slot Selected", inventory.heldSlot == armorSlot) {
            pressKey(10, Key.Type.valueOf("KEY_" + (armorSlot + 1)))
        }

        tick.finishIf(
            "Not Holding Valid Armor",
            inventory.heldItemStack?.let { missingArmorPiece().isType(it.item.id) } == false)

        tick.execute {
            pressButton(10, MouseButton.Type.RIGHT_CLICK)
        }
    }

    override fun onEnd() {
    }

    override fun onEvent(event: Event): Boolean {
        return false
    }

    override fun onGameLoop() {}
}
