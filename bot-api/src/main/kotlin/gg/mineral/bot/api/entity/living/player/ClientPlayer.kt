package gg.mineral.bot.api.entity.living.player

import gg.mineral.bot.api.entity.living.ClientLivingEntity
import gg.mineral.bot.api.inv.Inventory
import gg.mineral.bot.api.inv.InventoryContainer
import gg.mineral.bot.api.math.simulation.PlayerMotionSimulator
import gg.mineral.bot.api.world.ClientWorld

interface ClientPlayer : ClientLivingEntity {
    /**
     * Gets the player's inventory.
     *
     * @return the player's inventory
     */
    val inventory: Inventory

    /**
     * Gets the player's inventory container.
     *
     * @return the player's inventory container
     */
    val inventoryContainer: InventoryContainer

    /**
     * Gets the player's eye height.
     *
     * @return the player's eye height
     */
    val eyeHeight: Float

    /**
     * Gets the player's username.
     *
     * @return the player's username
     */
    val username: String

    /**
     * Gets the player's hunger.
     *
     * @return the player's hunger
     */
    val hunger: Float

    /**
     * Gets the player's motion simulator.
     *
     * @return the player's motion simulator
     */
    fun motionSimulator(world: ClientWorld = this.world): PlayerMotionSimulator
}
