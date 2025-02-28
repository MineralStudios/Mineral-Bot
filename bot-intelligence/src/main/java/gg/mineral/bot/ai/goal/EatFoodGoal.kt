package gg.mineral.bot.ai.goal

import gg.mineral.bot.ai.goal.type.InventoryGoal
import gg.mineral.bot.api.controls.Key
import gg.mineral.bot.api.controls.MouseButton
import gg.mineral.bot.api.entity.living.ClientLivingEntity
import gg.mineral.bot.api.event.Event
import gg.mineral.bot.api.event.peripherals.MouseButtonEvent
import gg.mineral.bot.api.goal.Sporadic
import gg.mineral.bot.api.goal.Timebound
import gg.mineral.bot.api.instance.ClientInstance
import gg.mineral.bot.api.inv.item.Item

class EatFoodGoal(clientInstance: ClientInstance) : InventoryGoal(clientInstance), Sporadic, Timebound {
    override var executing: Boolean = false
    override var startTime: Long = 0
    override val maxDuration: Long = 100
    private var eating = false

    override fun shouldExecute(): Boolean {
        val fakePlayer = clientInstance.fakePlayer
        // TODO: config how conservative to be with food
        val shouldExecute = hasFood() && fakePlayer.hunger < 19 && fakePlayer.health > 16.0
        logger.debug("Checking shouldExecute: $shouldExecute")
        return shouldExecute
    }

    override fun onStart() {
        pressKey(Key.Type.KEY_W, Key.Type.KEY_LCONTROL)
        unpressKey(Key.Type.KEY_S, Key.Type.KEY_A, Key.Type.KEY_D)
    }

    init {
        logger.debug("EatFoodGoal initialized")
    }

    private fun hasFood(): Boolean {
        val fakePlayer = clientInstance.fakePlayer
        val inventory = fakePlayer.inventory

        val hasFood = inventory.contains(Item.Type.FOOD)
        logger.debug("Has food: $hasFood")
        return hasFood
    }

    private fun angleAwayFromEnemies(): Float {
        val fakePlayer = clientInstance.fakePlayer
        val world = fakePlayer.world

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
        val world = fakePlayer.world

        return world.entities
            .minOfOrNull {
                if (it is ClientLivingEntity && !clientInstance.configuration.friendlyUUIDs.contains(it.uuid))
                    it.distance3DTo(fakePlayer)
                else Double.MAX_VALUE
            } ?: Double.MAX_VALUE
    }

    private fun getFoodSlot(): Int {
        var foodSlot = -1
        val fakePlayer = clientInstance.fakePlayer
        val inventory = fakePlayer.inventory

        for (i in 0..35) {
            val itemStack = inventory.getItemStackAt(i) ?: continue
            val item = itemStack.item
            if (Item.Type.FOOD.isType(item.id)) {
                foodSlot = i
                break
            }
        }

        return foodSlot
    }

    override fun onTick(tick: Tick) {
        val foodSlot = getFoodSlot()
        val fakePlayer = clientInstance.fakePlayer
        val inventory = fakePlayer.inventory

        tick.finishIf("Valid Food Found", foodSlot == -1)

        tick.finishIf("Hunger Satisfied", fakePlayer.hunger >= 19)

        tick.prerequisite("In Hotbar", foodSlot <= 8) {
            moveItemToHotbar(foodSlot, inventory)
        }

        tick.prerequisite("Inventory Closed", !inventoryOpen) { inventoryOpen = false }

        tick.prerequisite("Correct Hotbar Slot Selected", inventory.heldSlot == foodSlot) {
            pressKey(10, Key.Type.valueOf("KEY_" + (foodSlot + 1)))
        }

        tick.finishIf(
            "Not Holding Valid Food",
            inventory.heldItemStack?.let { Item.Type.FOOD.isType(it.item.id) } == false)

        tick.prerequisite("Eating", eating && getButton(MouseButton.Type.RIGHT_CLICK).isPressed) {
            pressButton(MouseButton.Type.RIGHT_CLICK)
            eating = true
        }

        tick.execute {
            if (distanceAwayFromEnemies() < 16) {
                setMouseYaw(angleAwayFromEnemies())
                pressKey(Key.Type.KEY_SPACE, Key.Type.KEY_W, Key.Type.KEY_LCONTROL)
            }
        }
    }

    override fun onEnd() {
        eating = false
        unpressButton(MouseButton.Type.RIGHT_CLICK)
        unpressKey(Key.Type.KEY_SPACE)
        if (inventoryOpen) {
            inventoryOpen = false
            logger.debug("Closing inventory after eating")
        }
    }

    override fun onEvent(event: Event): Boolean {
        if (event is MouseButtonEvent) {
            if (eating && event.type == MouseButton.Type.RIGHT_CLICK && !event.pressed) {
                logger.debug("Ignoring RIGHT_CLICK release event while eating")
                return true
            } else if (event.type == MouseButton.Type.LEFT_CLICK && inventoryOpen && event.pressed) {
                logger.debug("Ignoring LEFT_CLICK press event")
                return true
            }
        }
        return false
    }

    public override fun onGameLoop() {
    }
}
