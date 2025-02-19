package gg.mineral.bot.api.inv.item

import gg.mineral.bot.api.inv.potion.Potion

interface ItemStack {
    /**
     * Get the object corresponding to the item in this stack.
     *
     * @return the item in this stack
     */
    val item: Item

    /**
     * Get the attack damage of this item stack.
     *
     * @return the attack damage of this item stack
     */
    val attackDamage: Double

    /**
     * Get the durability of this item stack.
     *
     * @return the durability of this item stack
     */
    val durability: Int

    /**
     * Returns the Potion associated with this ItemStack.
     *
     * @return the Potion associated with this ItemStack
     *
     * @return null if the item is not a potion
     */
    val potion: Potion?
}
