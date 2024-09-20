package gg.mineral.bot.api.inv;

import java.util.function.Function;

import org.eclipse.jdt.annotation.Nullable;

import gg.mineral.bot.api.inv.item.Item;
import gg.mineral.bot.api.inv.item.ItemStack;
import gg.mineral.bot.api.inv.potion.Potion;

public interface Inventory {
    /**
     * Get the item stack that the player is currently holding.
     * 
     * @return the item stack that the player is currently holding
     */
    @Nullable
    ItemStack getHeldItemStack();

    /**
     * Get the item stack at the specified slot.
     * 
     * @param slot
     *            the slot
     * @return the item stack at the specified slot
     */
    @Nullable
    ItemStack getItemStackAt(int slot);

    /**
     * Find the slot of the specified item.
     * 
     * @param item
     *            the item
     * @return the slot of the specified item
     */
    int findSlot(Item item);

    /**
     * Checks if the inventory contains the specified item.
     *
     * @param item
     *            the item to check for
     * @return true if the item is found in the inventory, false otherwise
     */
    default boolean contains(Item item) {
        return findSlot(item) != -1;
    }

    /**
     * Find the slot of the specified item.
     * 
     * @param id
     *            the id of the item
     * @return the slot of the specified item
     */
    int findSlot(int id);

    /**
     * Checks if the inventory contains an item with the specified ID.
     *
     * @param id
     *            the ID of the item to check
     * @return true if the inventory contains an item with the specified ID, false
     *         otherwise
     */
    default boolean contains(int id) {
        return findSlot(id) != -1;
    }

    /**
     * Checks if the inventory contains a potion that matches the specified filter.
     *
     * @param filter
     *            the filter to apply
     * @return true if the inventory contains a potion that matches the filter,
     *         false
     *         otherwise
     */
    default boolean containsPotion(Function<Potion, Boolean> filter) {
        for (int i = 0; i < 36; i++) {
            ItemStack itemStack = getItemStackAt(i);
            if (itemStack == null)
                continue;
            Item item = itemStack.getItem();
            if (item.getId() == Item.POTION) {
                Potion potion = itemStack.getPotion();
                if (filter.apply(potion))
                    return true;
            }
        }
        return false;
    }
}
