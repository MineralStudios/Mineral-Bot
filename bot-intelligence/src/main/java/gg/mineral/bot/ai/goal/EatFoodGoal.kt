package gg.mineral.bot.ai.goal

import gg.mineral.bot.ai.goal.type.InventoryGoal
import gg.mineral.bot.api.controls.Key
import gg.mineral.bot.api.controls.MouseButton
import gg.mineral.bot.api.entity.living.ClientLivingEntity
import gg.mineral.bot.api.event.Event
import gg.mineral.bot.api.event.peripherals.MouseButtonEvent
import gg.mineral.bot.api.instance.ClientInstance
import gg.mineral.bot.api.inv.item.Item

class EatFoodGoal(clientInstance: ClientInstance) : InventoryGoal(clientInstance) {
    private var eating = false

    override fun shouldExecute(): Boolean {
        val fakePlayer = clientInstance.fakePlayer
        // TODO: config how conservative to be with food
        val shouldExecute = hasFood() && fakePlayer.hunger < 19 && fakePlayer.health > 16.0
        logger.debug( "Checking shouldExecute: $shouldExecute")
        return shouldExecute
    }

    override fun isExecuting(): Boolean {
        return eating || inventoryOpen
    }

    init {
        logger.debug( "EatFoodGoal initialized")
    }

    private fun hasFood(): Boolean {
        val fakePlayer = clientInstance.fakePlayer
        val inventory = fakePlayer.inventory

        if (inventory == null) {
            logger.debug( "Inventory is null")
            return false
        }

        val hasFood = inventory.contains(Item.Type.FOOD)
        logger.debug( "Has food: $hasFood")
        return hasFood
    }

    private fun eatFood() {
        this.eating = true
        logger.debug( "Started eating")
    }

    private fun angleAwayFromEnemies(): Float {
        val fakePlayer = clientInstance.fakePlayer
        val world = fakePlayer.world ?: return logger.debug( "World is null").let { fakePlayer.yaw }

        val enemy = world.entities
            .minByOrNull {
                if (it is ClientLivingEntity && !clientInstance.configuration.friendlyUUIDs.contains(it.uuid))
                    it.distance3DTo(fakePlayer)
                else Double.MAX_VALUE
            } ?: return fakePlayer.yaw
        val x: Double = enemy.x - fakePlayer.x
        val z: Double = enemy.z - fakePlayer.z

        var yaw = Math.toDegrees(-fastArcTan(x / z)).toFloat()
        if (z < 0.0 && x < 0.0) yaw = (90.0 + Math.toDegrees(fastArcTan(z / x))).toFloat()
        else if (z < 0.0 && x > 0.0) yaw = (-90.0 + Math.toDegrees(fastArcTan(z / x))).toFloat()
        return yaw + 180.0f
    }

    private fun distanceAwayFromEnemies(): Double {
        val fakePlayer = clientInstance.fakePlayer
        val world = fakePlayer.world ?: return logger.debug( "World is null").let { Double.MAX_VALUE }

        return world.entities
            .minOfOrNull {
                if (it is ClientLivingEntity && !clientInstance.configuration.friendlyUUIDs.contains(it.uuid))
                    it.distance3DTo(fakePlayer)
                else Double.MAX_VALUE
            } ?: Double.MAX_VALUE
    }

    private fun switchToFood() {
        eating = false
        logger.debug( "Switching to food")
        var foodSlot = -1
        val fakePlayer = clientInstance.fakePlayer
        val inventory = fakePlayer.inventory ?: return logger.debug( "Inventory is null")

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
            logger.debug( "Closing inventory after switching to food")
            return
        }

        pressKey(10, Key.Type.valueOf("KEY_" + (foodSlot + 1)))
        logger.debug( "Switched to food slot: " + (foodSlot + 1))
    }

    override fun onTick() {
        val fakePlayer = clientInstance.fakePlayer
        val inventory = fakePlayer.inventory ?: return logger.debug( "Inventory is null on tick")

        pressKey(Key.Type.KEY_W, Key.Type.KEY_LCONTROL)
        unpressKey(Key.Type.KEY_S, Key.Type.KEY_A, Key.Type.KEY_D)

        val isHungerSatisfied = fakePlayer.hunger >= 19

        if (eating && isHungerSatisfied) {
            eating = false
            logger.debug( "Stopped eating as hunger is satisfied")
        }

        if (!eating) {
            unpressButton(MouseButton.Type.RIGHT_CLICK)
            unpressKey(Key.Type.KEY_SPACE)
            logger.debug( "Unpressed RIGHT_CLICK as eating stopped")
        }

        if (isHungerSatisfied) return

        if (eating) {
            if (distanceAwayFromEnemies() < 16) {
                setMouseYaw(angleAwayFromEnemies())
                pressKey(Key.Type.KEY_SPACE)
            }
            pressButton(MouseButton.Type.RIGHT_CLICK)
            logger.debug( "Pressed RIGHT_CLICK for eating")
        }

        if (!delayedTasks.isEmpty()) return

        val itemStack = inventory.heldItemStack

        if (itemStack != null && Item.Type.FOOD.isType(itemStack.item.id) && !inventoryOpen) {
            this.eatFood()
            logger.debug( "Scheduled eatFood task")
        } else {
            schedule({ this.switchToFood() }, 100)
            logger.debug( "Scheduled switchToFood task")
        }
    }

    override fun onEvent(event: Event): Boolean {
        if (event is MouseButtonEvent) {
            if (eating && event.type == MouseButton.Type.RIGHT_CLICK && !event.isPressed) {
                logger.debug( "Ignoring RIGHT_CLICK release event while eating")
                return true
            }
        }
        return false
    }

    public override fun onGameLoop() {
    }
}
