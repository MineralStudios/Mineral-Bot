package gg.mineral.bot.api.entity.living.player;

import gg.mineral.bot.api.entity.living.ClientLivingEntity;
import gg.mineral.bot.api.inv.Inventory;
import gg.mineral.bot.api.inv.InventoryContainer;
import gg.mineral.bot.api.math.simulation.PlayerMotionSimulator;
import org.eclipse.jdt.annotation.Nullable;

public interface ClientPlayer extends ClientLivingEntity {
    /**
     * Gets the player's inventory.
     *
     * @return the player's inventory
     */
    @Nullable
    Inventory getInventory();

    /**
     * Gets the player's inventory container.
     *
     * @return the player's inventory container
     */
    @Nullable
    InventoryContainer getInventoryContainer();

    /**
     * Gets the player's eye height.
     *
     * @return the player's eye height
     */
    float getEyeHeight();

    /**
     * Gets the player's username.
     *
     * @return the player's username
     */
    String getUsername();

    /**
     * Gets the player's hunger.
     *
     * @return the player's hunger
     */
    float getHunger();

    /**
     * Gets the player's motion simulator.
     *
     * @return the player's motion simulator
     */
    PlayerMotionSimulator getMotionSimulator();
}
