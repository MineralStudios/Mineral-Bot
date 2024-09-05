package gg.mineral.bot.api.inv.item;

import gg.mineral.bot.api.inv.potion.Potion;

public interface ItemStack {
    /**
     * Get the object corresponding to the item in this stack.
     * 
     * @return the item in this stack
     */
    Item getItem();

    /**
     * Get the attack damage of this item stack.
     * 
     * @return the attack damage of this item stack
     */
    double getAttackDamage();

    /**
     * Get the durability of this item stack.
     * 
     * @return the durability of this item stack
     */
    int getDurability();

    /**
     * Returns the Potion associated with this ItemStack.
     *
     * @return the Potion associated with this ItemStack
     * 
     * @throws IllegalStateException
     *             if the item is not a potion
     */
    Potion getPotion();
}
