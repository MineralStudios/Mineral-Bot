package gg.mineral.bot.ai.goal

import gg.mineral.bot.ai.goal.type.InventoryGoal
import gg.mineral.bot.api.controls.Key
import gg.mineral.bot.api.controls.MouseButton
import gg.mineral.bot.api.entity.effect.PotionEffectType
import gg.mineral.bot.api.entity.living.ClientLivingEntity
import gg.mineral.bot.api.entity.living.player.ClientPlayer
import gg.mineral.bot.api.event.Event
import gg.mineral.bot.api.event.peripherals.MouseButtonEvent
import gg.mineral.bot.api.goal.Sporadic
import gg.mineral.bot.api.goal.Timebound
import gg.mineral.bot.api.instance.ClientInstance
import gg.mineral.bot.api.inv.item.Item

class EatGappleGoal(clientInstance: ClientInstance) : InventoryGoal(clientInstance), Sporadic, Timebound {
    override var executing: Boolean = false
    override var startTime: Long = 0
    override val maxDuration: Long = 100
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
        logger.debug("Checking shouldExecute: $shouldExecute")
        return shouldExecute
    }

    override fun onStart() {
        pressKey(Key.Type.KEY_W, Key.Type.KEY_LCONTROL)
        unpressKey(Key.Type.KEY_S, Key.Type.KEY_A, Key.Type.KEY_D)
    }

    init {
        logger.debug("EatGappleGoal initialized")
    }

    private fun hasGapple(): Boolean {
        val fakePlayer = clientInstance.fakePlayer
        val inventory = fakePlayer.inventory

        val hasGapple = inventory.contains(Item.GOLDEN_APPLE)
        logger.debug("Has golden apple: $hasGapple")
        return hasGapple
    }

    private fun canSeeEnemy(): Boolean {
        val fakePlayer = clientInstance.fakePlayer
        val world = fakePlayer.world

        val canSeeEnemy = world.entities.any {
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

    private fun getGappleSlot(): Int {
        var gappleSlot = -1
        val fakePlayer = clientInstance.fakePlayer
        val inventory = fakePlayer.inventory

        for (i in 0..35) {
            val itemStack = inventory.getItemStackAt(i) ?: continue
            val item = itemStack.item
            if (item.id == Item.GOLDEN_APPLE) {
                gappleSlot = i
                break
            }
        }

        return gappleSlot
    }

    override fun onTick(tick: Tick) {
        val gappleSlot = getGappleSlot()
        val fakePlayer = clientInstance.fakePlayer
        val inventory = fakePlayer.inventory

        tick.finishIf("Valid gapple slot not found", gappleSlot == -1)

        tick.prerequisite("In Hotbar", gappleSlot <= 8) {
            moveItemToHotbar(gappleSlot, inventory)
        }

        tick.prerequisite("Inventory Closed", !inventoryOpen) {
            inventoryOpen = false
        }

        tick.prerequisite("Correct Hotbar Slot Selected", inventory.heldSlot == gappleSlot) {
            pressKey(10, Key.Type.valueOf("KEY_" + (gappleSlot + 1)))
        }

        tick.finishIf("Not Holding Valid Gapple", inventory.heldItemStack?.item?.id != Item.GOLDEN_APPLE)

        tick.finishIf("Has Regen", fakePlayer.activePotionEffectIds.any { it == PotionEffectType.REGENERATION.id })

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
            logger.debug("Closing inventory after eating golden apple")
        }
    }

    override fun onEvent(event: Event): Boolean {
        if (event is MouseButtonEvent) {
            if (eating && event.type == MouseButton.Type.RIGHT_CLICK && !event.pressed) {
                logger.debug("Ignoring RIGHT_CLICK release event while eating")
                return true
            }
        }
        return false
    }

    public override fun onGameLoop() {
    }
}
