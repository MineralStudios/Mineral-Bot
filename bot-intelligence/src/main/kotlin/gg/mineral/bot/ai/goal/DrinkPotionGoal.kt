package gg.mineral.bot.ai.goal

import gg.mineral.bot.ai.goal.type.InventoryGoal
import gg.mineral.bot.api.controls.Key
import gg.mineral.bot.api.controls.MouseButton
import gg.mineral.bot.api.entity.living.ClientLivingEntity
import gg.mineral.bot.api.entity.living.player.ClientPlayer
import gg.mineral.bot.api.event.Event
import gg.mineral.bot.api.event.peripherals.MouseButtonEvent
import gg.mineral.bot.api.goal.Sporadic
import gg.mineral.bot.api.goal.Timebound
import gg.mineral.bot.api.instance.ClientInstance
import gg.mineral.bot.api.inv.item.Item
import gg.mineral.bot.api.inv.item.ItemStack
import gg.mineral.bot.api.inv.potion.Potion
import gg.mineral.bot.api.screen.type.ContainerScreen
import gg.mineral.bot.api.util.fastArcTan

class DrinkPotionGoal(clientInstance: ClientInstance) : InventoryGoal(clientInstance), Sporadic, Timebound {
    override var executing: Boolean = false
    override var startTime: Long = 0
    override val maxDuration: Long = 100
    private var drinking = false

    override fun shouldExecute(): Boolean {
        val shouldExecute = canSeeEnemy() && hasDrinkablePotion()
        logger.debug("Checking shouldExecute: $shouldExecute")
        return shouldExecute
    }

    override fun onStart() {
        pressKey(Key.Type.KEY_W, Key.Type.KEY_LCONTROL)
        unpressKey(Key.Type.KEY_S, Key.Type.KEY_A, Key.Type.KEY_D)
    }

    init {
        logger.debug("DrinkPotionGoal initialized")
    }

    private fun hasDrinkablePotion(): Boolean {
        val fakePlayer = clientInstance.fakePlayer
        val inventory = fakePlayer.inventory

        val hasDrinkablePotion = inventory.containsPotion {
            isValidPotion(it)
        }
        logger.debug("Has drinkable potion: $hasDrinkablePotion")
        return hasDrinkablePotion
    }

    private fun canSeeEnemy(): Boolean {
        val fakePlayer = clientInstance.fakePlayer
        val world = fakePlayer.world

        val canSeeEnemy = world.entities
            .any {
                it is ClientPlayer
                        && !clientInstance.configuration.friendlyUUIDs.contains(it.uuid)
            }
        logger.debug("Checking canSeeEnemy: $canSeeEnemy")
        return canSeeEnemy
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

    private fun isValidPotion(potion: Potion): Boolean {
        val fakePlayer = clientInstance.fakePlayer
        // TODO: exclude negative potions
        for (effect in potion.effects) if (fakePlayer.isPotionActive(effect.potionID)) return false
        return !potion.isSplash && potion.effects.isNotEmpty()
    }

    private fun isValidPotion(itemStack: ItemStack): Boolean {
        if (itemStack.item.id != Item.POTION) return false
        val potion = itemStack.potion ?: return false
        return isValidPotion(potion)
    }

    private fun getPotionSlot(): Int {
        var potionSlot = -1
        val fakePlayer = clientInstance.fakePlayer
        val inventory = fakePlayer.inventory

        // Look for a non-splash potion in one of the 36 slots
        invLoop@ for (i in 0..35) {
            val itemStack = inventory.getItemStackAt(i) ?: continue
            if (isValidPotion(itemStack)) {
                potionSlot = i
                break
            }
        }
        return potionSlot
    }

    override fun onTick(tick: Tick) {
        val potionSlot = getPotionSlot()
        val fakePlayer = clientInstance.fakePlayer
        val inventory = fakePlayer.inventory

        tick.finishIf("No Valid Potion Found", potionSlot == -1)

        tick.prerequisite("In Hotbar", potionSlot <= 8) {
            moveItemToHotbar(potionSlot, inventory)
        }

        tick.prerequisite(
            "Inventory Closed",
            clientInstance.currentScreen !is ContainerScreen
        ) { pressKey(10, Key.Type.KEY_ESCAPE) }

        tick.prerequisite("Correct Hotbar Slot Selected", inventory.heldSlot == potionSlot) {
            pressKey(10, Key.Type.valueOf("KEY_" + (potionSlot + 1)))
        }

        tick.finishIf("Not Holding Valid Potion", inventory.heldItemStack?.let { isValidPotion(it) } == false)

        tick.finishIf("Potion Not Needed", !shouldExecute())

        tick.prerequisite("Drinking", drinking && getButton(MouseButton.Type.RIGHT_CLICK).isPressed) {
            pressButton(MouseButton.Type.RIGHT_CLICK)
            drinking = true
        }

        tick.execute {
            if (distanceAwayFromEnemies() < 16) {
                setMouseYaw(angleAwayFromEnemies())
                pressKey(Key.Type.KEY_SPACE, Key.Type.KEY_W, Key.Type.KEY_LCONTROL)
            }
        }
    }

    override fun onEnd() {
        drinking = false
        unpressButton(MouseButton.Type.RIGHT_CLICK)
        unpressKey(Key.Type.KEY_SPACE)
    }

    override fun onEvent(event: Event): Boolean {
        if (event is MouseButtonEvent) {
            if (drinking && event.type == MouseButton.Type.RIGHT_CLICK && !event.pressed) {
                logger.debug("Ignoring RIGHT_CLICK release event while drinking")
                return true
            }
        }
        return false
    }

    public override fun onGameLoop() {}
}
