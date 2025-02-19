package gg.mineral.bot.api.inv

import gg.mineral.bot.api.inv.item.Item
import gg.mineral.bot.api.inv.item.ItemStack
import gg.mineral.bot.api.inv.potion.Potion
import java.util.function.Function

interface Inventory {
    /**
     * Get the item stack that the player is currently holding.
     *
     * @return the item stack that the player is currently holding
     */
    val heldItemStack: ItemStack?

    /**
     * Get the slot that the player is currently holding.
     *
     * @return the slot that the player is currently holding
     */
    val heldSlot: Int

    /**
     * Get the item stack at the specified slot.
     *
     * @param slot the slot
     * @return the item stack at the specified slot
     */
    fun getItemStackAt(slot: Int): ItemStack?

    /**
     * Get the helmet.
     *
     * @return the helmet
     */
    val helmet: ItemStack?

    /**
     * Get the chestplate.
     *
     * @return the chestplate
     */
    val chestplate: ItemStack?

    /**
     * Get the leggings.
     *
     * @return the leggings
     */
    val leggings: ItemStack?

    /**
     * Get the boots.
     *
     * @return the boots
     */
    val boots: ItemStack?

    /**
     * Find the slot of the specified item.
     *
     * @param item the item
     * @return the slot of the specified item
     */
    fun findSlot(item: Item): Int

    /**
     * Checks if the inventory contains the specified item.
     *
     * @param item the item to check for
     * @return true if the item is found in the inventory, false otherwise
     */
    fun contains(item: Item): Boolean {
        return findSlot(item) != -1
    }

    /**
     * Find the slot of the specified item.
     *
     * @param id the id of the item
     * @return the slot of the specified item
     */
    fun findSlot(id: Int): Int

    /**
     * Checks if the inventory contains an item with the specified ID.
     *
     * @param id the ID of the item to check
     * @return true if the inventory contains an item with the specified ID, false
     * otherwise
     */
    fun contains(id: Int): Boolean {
        return findSlot(id) != -1
    }

    /**
     * Checks if the inventory contains an item that matches the specified filter.
     *
     * @param filter the filter to apply
     * @return true if the inventory contains an item that matches the filter,
     * false
     * otherwise
     */
    fun contains(filter: Function<ItemStack, Boolean>): Boolean {
        for (i in 0..35) {
            val itemStack = getItemStackAt(i) ?: continue
            if (filter.apply(itemStack)) return true
        }
        return false
    }

    /**
     * Checks if the inventory contains a potion that matches the specified filter.
     *
     * @param filter the filter to apply
     * @return true if the inventory contains a potion that matches the filter,
     * false
     * otherwise
     */
    fun containsPotion(filter: Function<Potion, Boolean>): Boolean {
        for (i in 0..35) {
            val itemStack = getItemStackAt(i) ?: continue
            val item = itemStack.item
            if (item.id == Item.POTION) {
                val potion = itemStack.potion
                if (potion?.let { filter.apply(it) } == true) return true
            }
        }
        return false
    }

    /**
     * Checks if the inventory contains an item that matches the specified type.
     *
     * @param type the type to check for
     * @return true if the inventory contains an item that matches the specified
     * type,
     * false otherwise
     */
    fun contains(type: Item.Type): Boolean {
        for (i in 0..35) {
            val itemStack = getItemStackAt(i) ?: continue
            val item = itemStack.item
            if (type.isType(item.id)) return true
        }
        return false
    }

    /**
     * Get all the items in the inventory.
     *
     * @return all the items in the inventory
     */
    val items: Array<ItemStack?>
}
