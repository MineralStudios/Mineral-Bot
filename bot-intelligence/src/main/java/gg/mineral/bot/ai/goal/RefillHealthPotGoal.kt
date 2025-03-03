package gg.mineral.bot.ai.goal

import gg.mineral.bot.ai.goal.type.InventoryGoal
import gg.mineral.bot.api.controls.Key
import gg.mineral.bot.api.entity.living.ClientLivingEntity
import gg.mineral.bot.api.event.Event
import gg.mineral.bot.api.goal.Sporadic
import gg.mineral.bot.api.goal.Timebound
import gg.mineral.bot.api.instance.ClientInstance
import gg.mineral.bot.api.inv.item.Item
import gg.mineral.bot.api.inv.item.ItemStack
import gg.mineral.bot.api.math.simulation.PlayerMotionSimulator
import gg.mineral.bot.api.screen.type.ContainerScreen

class RefillHealthPotGoal(clientInstance: ClientInstance) : InventoryGoal(clientInstance), Sporadic, Timebound {
    override val maxDuration: Long = 100
    override var startTime: Long = 0
    override var executing: Boolean = false

    override fun shouldExecute(): Boolean {
        val healthSlot = getHealthPotSlot()

        val hasHealth = healthSlot != -1

        val fakePlayer = clientInstance.fakePlayer

        val distance = distanceAwayFromEnemies()

        val health = fakePlayer.health

        val items = fakePlayer.inventory.items

        val emptyHotbarSlots = run {
            for (i in 0..8) if (items[i] == null) return true
            false
        }

        return (emptyHotbarSlots && (hasHealth || distance > 8.0)) || (health < 12.0 && healthSlot > 8)
    }

    override fun onStart() {
    }

    private fun distanceAwayFromEnemies(): Double {
        val fakePlayer = clientInstance.fakePlayer
        val world = fakePlayer.world

        return world.entities
            .minOfOrNull {
                if (it is ClientLivingEntity && !clientInstance.configuration.friendlyUUIDs.contains(it.uuid))
                    it.distance2DTo(fakePlayer.x, fakePlayer.z)
                else Double.MAX_VALUE
            } ?: Double.MAX_VALUE
    }

    // Extension functions for adjusting the bot’s aim.
    private fun PlayerMotionSimulator.setMouseYaw(yaw: Float) {
        val rotYaw = this.yaw
        mouse.changeYaw(angleDifference(rotYaw, yaw))
    }

    private fun isHealthPot(itemStack: ItemStack): Boolean {
        val item = itemStack.item
        return item.id == Item.POTION && itemStack.durability == 16421
    }

    private fun getHealthPotSlot(): Int {
        var healthPotSlot = -1
        val fakePlayer = clientInstance.fakePlayer
        val inventory = fakePlayer.inventory

        for (i in 0..35) {
            val itemStack = inventory.getItemStackAt(i) ?: continue
            if (isHealthPot(itemStack)) {
                healthPotSlot = i
                break
            }
        }

        return healthPotSlot
    }

    override fun onTick(tick: Tick) {
        val healthSlot = getHealthPotSlot()
        val fakePlayer = clientInstance.fakePlayer
        val inventory = fakePlayer.inventory

        tick.finishIf(
            "No Valid Health Pot Found",
            healthSlot == -1
        )

        val isHoldingHealth = inventory.heldItemStack?.let { isHealthPot(it) } == true

        tick.prerequisite("In Hotbar", healthSlot <= 8 || isHoldingHealth) {
            val items = fakePlayer.inventory.items
            val emptyHotbarSlot = (0..8).firstOrNull { items[it] == null } ?: 8
            moveItemToHotbar(healthSlot, inventory, emptyHotbarSlot)
        }

        tick.prerequisite("Inventory Closed", clientInstance.currentScreen !is ContainerScreen) {
            pressKey(10, Key.Type.KEY_ESCAPE)
        }

        tick.finishIf(
            "Refill Not Needed",
            !shouldExecute()
        )
    }

    override fun onEnd() {
        if (clientInstance.currentScreen is ContainerScreen)
            pressKey(10, Key.Type.KEY_ESCAPE)
    }

    override fun onEvent(event: Event): Boolean {
        return false
    }

    public override fun onGameLoop() {
        // No changes made in the game loop.
    }
}
