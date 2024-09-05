package gg.mineral.bot.api.entity.living.player;

import org.eclipse.jdt.annotation.Nullable;

import gg.mineral.bot.api.entity.living.ClientLivingEntity;
import gg.mineral.bot.api.inv.Inventory;
import gg.mineral.bot.api.inv.InventoryContainer;

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
}
