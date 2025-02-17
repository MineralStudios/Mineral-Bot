package gg.mineral.bot.ai.goal

import gg.mineral.bot.ai.goal.type.InventoryGoal
import gg.mineral.bot.api.controls.Key
import gg.mineral.bot.api.controls.MouseButton
import gg.mineral.bot.api.entity.effect.PotionEffectType
import gg.mineral.bot.api.entity.living.ClientLivingEntity
import gg.mineral.bot.api.entity.living.player.ClientPlayer
import gg.mineral.bot.api.event.Event
import gg.mineral.bot.api.event.peripherals.MouseButtonEvent
import gg.mineral.bot.api.instance.ClientInstance
import gg.mineral.bot.api.inv.item.Item

class EatGappleGoal(clientInstance: ClientInstance) : InventoryGoal(clientInstance) {
    private var eating = false

    override fun shouldExecute(): Boolean {
        var hasRegen = false
        val regenId = PotionEffectType.REGENERATION.id
        val fakePlayer = clientInstance.fakePlayer
        val activeIds = fakePlayer.activePotionEffectIds

        for (activeId in activeIds) if (activeId == regenId) {
            hasRegen = true
            break
        }

        val shouldExecute =
            canSeeEnemy() && hasGapple() && !hasRegen && (fakePlayer.health < 10 || distanceAwayFromEnemies() in 8.0..16.0)
        logger.debug( "Checking shouldExecute: $shouldExecute")
        return shouldExecute
    }

    override fun isExecuting(): Boolean {
        return eating || inventoryOpen
    }

    init {
        logger.debug( "EatGappleGoal initialized")
    }

    private fun hasGapple(): Boolean {
        val fakePlayer = clientInstance.fakePlayer
        val inventory = fakePlayer.inventory

        if (inventory == null) {
            logger.debug( "Inventory is null")
            return false
        }

        val hasGapple = inventory.contains(Item.GOLDEN_APPLE)
        logger.debug( "Has golden apple: $hasGapple")
        return hasGapple
    }

    private fun canSeeEnemy(): Boolean {
        val fakePlayer = clientInstance.fakePlayer
        val world = fakePlayer.world

        if (world == null) {
            logger.debug( "World is null")
            return false
        }

        val canSeeEnemy = world.entities.any {
            it is ClientPlayer
                    && !clientInstance.configuration.friendlyUUIDs.contains(it.getUuid())
        }
        logger.debug( "Checking canSeeEnemy: $canSeeEnemy")
        return canSeeEnemy
    }

    private fun eatGapple() {
        this.eating = true
        logger.debug( "Started eating golden apple")
    }

    private fun switchToGapple() {
        eating = false
        logger.debug( "Switching to golden apple")
        var gappleSlot = -1
        val fakePlayer = clientInstance.fakePlayer
        val inventory = fakePlayer.inventory ?: return logger.debug( "Inventory is null")

        for (i in 0..35) {
            val itemStack = inventory.getItemStackAt(i) ?: continue
            val item = itemStack.item
            if (item.id == Item.GOLDEN_APPLE) {
                gappleSlot = i
                break
            }
        }

        if (gappleSlot > 8) return moveItemToHotbar(gappleSlot, inventory)

        if (inventoryOpen) {
            inventoryOpen = false
            pressKey(10, Key.Type.KEY_ESCAPE)
            logger.debug( "Closing inventory after switching to golden apple")
            return
        }

        pressKey(10, Key.Type.valueOf("KEY_" + (gappleSlot + 1)))
        logger.debug( "Switched to golden apple slot: " + (gappleSlot + 1))
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

    override fun onTick() {
        val fakePlayer = clientInstance.fakePlayer
        val inventory = fakePlayer.inventory ?: return logger.debug( "Inventory is null on tick")

        pressKey(Key.Type.KEY_W, Key.Type.KEY_LCONTROL)
        unpressKey(Key.Type.KEY_S, Key.Type.KEY_A, Key.Type.KEY_D)

        var hasRegen = false
        val regenId = PotionEffectType.REGENERATION.id
        val activeIds = fakePlayer.activePotionEffectIds

        for (i in activeIds.indices) if (activeIds[i] == regenId) {
            hasRegen = true
            break
        }

        if (eating && hasRegen) {
            eating = false
            logger.debug( "Stopped eating as regeneration is active")
        }

        if (!eating) {
            unpressButton(MouseButton.Type.RIGHT_CLICK)
            unpressKey(Key.Type.KEY_SPACE)
            logger.debug( "Unpressed RIGHT_CLICK as eating stopped")
        }

        if (hasRegen) return

        if (eating) {
            if (distanceAwayFromEnemies() < 16) {
                setMouseYaw(angleAwayFromEnemies())
                pressKey(Key.Type.KEY_SPACE)
            }
            pressButton(MouseButton.Type.RIGHT_CLICK)
            logger.debug( "Pressed RIGHT_CLICK for eating golden apple")
        }

        if (!delayedTasks.isEmpty()) return

        val itemStack = inventory.heldItemStack

        if (itemStack != null && itemStack.item.id == Item.GOLDEN_APPLE && !inventoryOpen) {
            this.eatGapple()
            logger.debug( "Scheduled eatGapple task")
        } else {
            schedule({ this.switchToGapple() }, 100)
            logger.debug( "Scheduled switchToGapple task")
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
