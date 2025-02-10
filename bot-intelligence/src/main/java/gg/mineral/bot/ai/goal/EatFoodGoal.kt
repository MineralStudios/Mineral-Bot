package gg.mineral.bot.ai.goal

import gg.mineral.bot.ai.goal.type.InventoryGoal
import gg.mineral.bot.api.controls.Key
import gg.mineral.bot.api.controls.MouseButton
import gg.mineral.bot.api.event.Event
import gg.mineral.bot.api.event.peripherals.MouseButtonEvent
import gg.mineral.bot.api.instance.ClientInstance
import gg.mineral.bot.api.inv.item.Item

class EatFoodGoal(clientInstance: ClientInstance) : InventoryGoal(clientInstance) {
    private var eating = false

    override fun shouldExecute(): Boolean {
        if (inventoryOpen) return true

        val fakePlayer = clientInstance.fakePlayer

        // TODO: config how conservative to be with food
        val shouldExecute = eating || hasFood() && fakePlayer.hunger < 19
        info(this, "Checking shouldExecute: $shouldExecute")
        return shouldExecute
    }

    init {
        info(this, "EatFoodGoal initialized")
    }

    private fun hasFood(): Boolean {
        val fakePlayer = clientInstance.fakePlayer
        val inventory = fakePlayer.inventory

        if (inventory == null) {
            warn(this, "Inventory is null")
            return false
        }

        val hasFood = inventory.contains(Item.Type.FOOD)
        info(this, "Has food: $hasFood")
        return hasFood
    }

    private fun eatFood() {
        this.eating = true
        success(this, "Started eating")
    }

    private fun switchToFood() {
        eating = false
        info(this, "Switching to food")
        var foodSlot = -1
        val fakePlayer = clientInstance.fakePlayer
        val inventory = fakePlayer.inventory ?: return warn(this, "Inventory is null")

        // TODO: Choose best availible food
        for (i in 0..35) {
            val itemStack = inventory.getItemStackAt(i) ?: continue
            val item = itemStack.item
            if (Item.Type.FOOD.isType(item.id)) {
                foodSlot = i
                break
            }
        }

        if (foodSlot > 8) return moveItemToHotbar(foodSlot, inventory)

        if (inventoryOpen) {
            inventoryOpen = false
            pressKey(10, Key.Type.KEY_ESCAPE)
            info(this, "Closing inventory after switching to food")
            return
        }

        pressKey(10, Key.Type.valueOf("KEY_" + (foodSlot + 1)))
        success(this, "Switched to food slot: " + (foodSlot + 1))
    }

    override fun onTick() {
        val fakePlayer = clientInstance.fakePlayer
        val inventory = fakePlayer.inventory ?: return warn(this, "Inventory is null on tick")

        val isHungerSatisfied = fakePlayer.hunger >= 19

        if (eating && isHungerSatisfied) {
            eating = false
            info(this, "Stopped eating as hunger is satisfied")
        }

        val rmbHeld = getButton(MouseButton.Type.RIGHT_CLICK).isPressed

        if (!eating && rmbHeld) {
            unpressButton(MouseButton.Type.RIGHT_CLICK)
            info(this, "Unpressed RIGHT_CLICK as eating stopped")
        }

        if (isHungerSatisfied) return

        if (eating && !rmbHeld) {
            pressButton(MouseButton.Type.RIGHT_CLICK)
            info(this, "Pressed RIGHT_CLICK for eating golden apple")
        }

        if (!delayedTasks.isEmpty()) return

        val itemStack = inventory.heldItemStack

        if (itemStack != null && itemStack.item.id == Item.GOLDEN_APPLE) {
            schedule({ this.eatFood() }, 100)
            info(this, "Scheduled eatFood task")
        } else {
            schedule({ this.switchToFood() }, 100)
            info(this, "Scheduled switchToFood task")
        }
    }

    override fun onEvent(event: Event): Boolean {
        if (event is MouseButtonEvent) {
            if (eating && event.type == MouseButton.Type.RIGHT_CLICK && !event.isPressed) {
                info(this, "Ignoring RIGHT_CLICK release event while eating")
                return true
            }
        }
        return false
    }

    public override fun onGameLoop() {
    }
}
