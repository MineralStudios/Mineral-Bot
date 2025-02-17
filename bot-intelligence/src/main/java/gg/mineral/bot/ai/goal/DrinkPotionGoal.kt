package gg.mineral.bot.ai.goal

import gg.mineral.bot.ai.goal.type.InventoryGoal
import gg.mineral.bot.api.controls.Key
import gg.mineral.bot.api.controls.MouseButton
import gg.mineral.bot.api.entity.living.ClientLivingEntity
import gg.mineral.bot.api.entity.living.player.ClientPlayer
import gg.mineral.bot.api.event.Event
import gg.mineral.bot.api.event.peripherals.MouseButtonEvent
import gg.mineral.bot.api.instance.ClientInstance
import gg.mineral.bot.api.inv.item.Item
import java.util.function.Function

class DrinkPotionGoal(clientInstance: ClientInstance) : InventoryGoal(clientInstance) {
    private var drinking = false

    override fun shouldExecute(): Boolean {
        val shouldExecute = canSeeEnemy() && hasDrinkablePotion()
        logger.debug( "Checking shouldExecute: $shouldExecute")
        return shouldExecute
    }

    override fun isExecuting(): Boolean {
        return drinking || inventoryOpen
    }

    init {
        logger.debug( "DrinkPotionGoal initialized")
    }

    private fun hasDrinkablePotion(): Boolean {
        val fakePlayer = clientInstance.fakePlayer
        val inventory = fakePlayer.inventory ?: return logger.debug( "Inventory is null").let { false }

        val hasDrinkablePotion = inventory.containsPotion(Function {
            for (effect in it.effects) if (fakePlayer.isPotionActive(effect.potionID)) return@Function false
            !it.isSplash && it.effects.isNotEmpty()
        })
        logger.debug( "Has drinkable potion: $hasDrinkablePotion")
        return hasDrinkablePotion
    }

    private fun canSeeEnemy(): Boolean {
        val fakePlayer = clientInstance.fakePlayer
        val world = fakePlayer.world ?: return logger.debug( "World is null").let { false }

        val canSeeEnemy = world.entities
            .any {
                it is ClientPlayer
                        && !clientInstance.configuration.friendlyUUIDs.contains(it.getUuid())
            }
        logger.debug( "Checking canSeeEnemy: $canSeeEnemy")
        return canSeeEnemy
    }

    private fun drinkPotion() {
        drinking = true
        logger.debug( "Started drinking potion")
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

    private fun switchToDrinkablePotion() {
        logger.debug( "Switching to a drinkable potion")
        var potionSlot = -1
        val fakePlayer = clientInstance.fakePlayer
        val inventory = fakePlayer.inventory ?: return logger.debug( "Inventory is null")

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
            logger.debug( "Closing inventory after switching potion")
            return
        }

        pressKey(10, Key.Type.valueOf("KEY_" + (potionSlot + 1)))
        logger.debug( "Switched to potion slot: " + (potionSlot + 1))
    }


    override fun onTick() {
        val fakePlayer = clientInstance.fakePlayer
        val inventory = fakePlayer.inventory ?: return logger.debug( "Inventory is null on tick")

        pressKey(Key.Type.KEY_W, Key.Type.KEY_LCONTROL)
        unpressKey(Key.Type.KEY_S, Key.Type.KEY_A, Key.Type.KEY_D)

        val itemStack = inventory.heldItemStack

        if (drinking && (itemStack == null || itemStack.item.id != Item.POTION)) {
            drinking = false
            logger.debug( "Stopped drinking as no potion is held")
        }

        if (drinking) {
            if (distanceAwayFromEnemies() < 16) {
                setMouseYaw(angleAwayFromEnemies())
                pressKey(Key.Type.KEY_SPACE)
            }
            pressButton(MouseButton.Type.RIGHT_CLICK)
            logger.debug( "Pressed RIGHT_CLICK for drinking")
        }

        if (!drinking) {
            unpressButton(MouseButton.Type.RIGHT_CLICK)
            unpressKey(Key.Type.KEY_SPACE)
            logger.debug( "Unpressed RIGHT_CLICK as drinking stopped")
        }

        if (drinking || !delayedTasks.isEmpty()) return

        if (itemStack != null && itemStack.item.id == Item.POTION && !inventoryOpen) {
            this.drinkPotion()
            logger.debug( "Scheduled drinkPotion task")
        } else {
            schedule({ this.switchToDrinkablePotion() }, 100)
            logger.debug( "Scheduled switchToDrinkablePotion task")
        }
    }

    override fun onEvent(event: Event): Boolean {
        if (event is MouseButtonEvent) {
            if (drinking && event.type == MouseButton.Type.RIGHT_CLICK && !event.isPressed) {
                logger.debug( "Ignoring RIGHT_CLICK release event while drinking")
                return true
            }
        }
        return false
    }

    public override fun onGameLoop() {}
}
