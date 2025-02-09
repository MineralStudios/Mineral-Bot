package gg.mineral.bot.ai.goal

import gg.mineral.bot.ai.goal.type.InventoryGoal
import gg.mineral.bot.api.controls.Key
import gg.mineral.bot.api.controls.MouseButton
import gg.mineral.bot.api.entity.living.player.ClientPlayer
import gg.mineral.bot.api.event.Event
import gg.mineral.bot.api.event.peripherals.MouseButtonEvent
import gg.mineral.bot.api.instance.ClientInstance
import gg.mineral.bot.api.inv.item.Item
import java.util.function.Function

class DrinkPotionGoal(clientInstance: ClientInstance) : InventoryGoal(clientInstance) {
    private var drinking = false

    override fun shouldExecute(): Boolean {
        val shouldExecute = drinking || inventoryOpen || canSeeEnemy() && hasDrinkablePotion()
        info(this, "Checking shouldExecute: $shouldExecute")
        return shouldExecute
    }

    init {
        info(this, "DrinkPotionGoal initialized")
    }

    private fun hasDrinkablePotion(): Boolean {
        val fakePlayer = clientInstance.fakePlayer
        val inventory = fakePlayer.inventory ?: return warn(this, "Inventory is null").let { false }

        val hasDrinkablePotion = inventory.containsPotion(Function {
            for (effect in it.effects) if (fakePlayer.isPotionActive(effect.potionID)) return@Function false
            !it.isSplash && it.effects.isNotEmpty()
        })
        info(this, "Has drinkable potion: $hasDrinkablePotion")
        return hasDrinkablePotion
    }

    private fun canSeeEnemy(): Boolean {
        val fakePlayer = clientInstance.fakePlayer
        val world = fakePlayer.world ?: return warn(this, "World is null").let { false }

        val canSeeEnemy = world.entities
            .any {
                it is ClientPlayer
                        && !clientInstance.configuration.friendlyUUIDs.contains(it.getUuid())
            }
        info(this, "Checking canSeeEnemy: $canSeeEnemy")
        return canSeeEnemy
    }

    private fun drinkPotion() {
        drinking = true
        success(this, "Started drinking potion")
    }

    private fun switchToDrinkablePotion() {
        info(this, "Switching to a drinkable potion")
        var potionSlot = -1
        val fakePlayer = clientInstance.fakePlayer
        val inventory = fakePlayer.inventory ?: return warn(this, "Inventory is null")

        // Look for a non-splash potion in one of the 36 slots
        invLoop@ for (i in 0..35) {
            val itemStack = inventory.getItemStackAt(i) ?: continue
            val item = itemStack.item
            if (item.id == Item.POTION) {
                val potion = itemStack.potion
                for (effect in potion.effects) if (fakePlayer.isPotionActive(effect.potionID)) continue@invLoop

                // TODO: stop drinking negative potions
                if (potion.isSplash || potion.effects.isEmpty()) continue
                potionSlot = i
                break
            }
        }

        // If the potion is not in the hotbar (slots 0-8)
        if (potionSlot > 8) {
            moveItemToHotbar(potionSlot, inventory)
            return
        }

        if (inventoryOpen) {
            inventoryOpen = false
            pressKey(10, Key.Type.KEY_ESCAPE)
            info(this, "Closing inventory after switching potion")
            return
        }

        pressKey(10, Key.Type.valueOf("KEY_" + (potionSlot + 1)))
        success(this, "Switched to potion slot: " + (potionSlot + 1))
    }


    override fun onTick() {
        val fakePlayer = clientInstance.fakePlayer
        val inventory = fakePlayer.inventory ?: return warn(this, "Inventory is null on tick")

        val itemStack = inventory.heldItemStack

        if (drinking && (itemStack == null || itemStack.item.id != Item.POTION)) {
            drinking = false
            info(this, "Stopped drinking as no potion is held")
        }

        val rmbHeld = getButton(MouseButton.Type.RIGHT_CLICK).isPressed

        if (drinking && !rmbHeld) {
            pressButton(MouseButton.Type.RIGHT_CLICK)
            info(this, "Pressed RIGHT_CLICK for drinking")
        }

        if (!drinking && rmbHeld) {
            unpressButton(MouseButton.Type.RIGHT_CLICK)
            info(this, "Unpressed RIGHT_CLICK as drinking stopped")
        }

        if (drinking || !delayedTasks.isEmpty()) return

        if (itemStack != null && itemStack.item.id == Item.POTION) {
            schedule({ this.drinkPotion() }, 100)
            info(this, "Scheduled drinkPotion task")
        } else {
            schedule({ this.switchToDrinkablePotion() }, 100)
            info(this, "Scheduled switchToDrinkablePotion task")
        }
    }

    override fun onEvent(event: Event): Boolean {
        if (event is MouseButtonEvent) {
            if (drinking && event.type == MouseButton.Type.RIGHT_CLICK && !event.isPressed) {
                info(this, "Ignoring RIGHT_CLICK release event while drinking")
                return true
            }
        }
        return false
    }

    public override fun onGameLoop() {}
}
