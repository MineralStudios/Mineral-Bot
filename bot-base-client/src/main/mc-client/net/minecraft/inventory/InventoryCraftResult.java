package net.minecraft.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.eclipse.jdt.annotation.Nullable;

public class InventoryCraftResult implements IInventory {
    /**
     * A list of one item containing the result of the crafting formula
     */
    private final ItemStack[] stackResult = new ItemStack[1];

    @Override
    public gg.mineral.bot.api.inv.item.ItemStack[] getItems() {
        return stackResult;
    }

    /**
     * Returns the number of slots in the inventory.
     */
    public int getSizeInventory() {
        return 1;
    }

    /**
     * Returns the stack in slot i
     */
    public ItemStack getStackInSlot(int p_70301_1_) {
        return this.stackResult[0];
    }

    /**
     * Returns the name of the inventory
     */
    public String getInventoryName() {
        return "Result";
    }

    /**
     * Returns if the inventory name is localized
     */
    public boolean isInventoryNameLocalized() {
        return false;
    }

    /**
     * Removes from an inventory slot (first arg) up to a specified number (second
     * arg) of items and returns them in a
     * new stack.
     */
    public ItemStack decrStackSize(int p_70298_1_, int p_70298_2_) {
        if (this.stackResult[0] != null) {
            ItemStack var3 = this.stackResult[0];
            this.stackResult[0] = null;
            return var3;
        } else {
            return null;
        }
    }

    /**
     * When some containers are closed they call this on each slot, then drop
     * whatever it returns as an EntityItem -
     * like when you close a workbench GUI.
     */
    public ItemStack getStackInSlotOnClosing(int p_70304_1_) {
        if (this.stackResult[0] != null) {
            ItemStack var2 = this.stackResult[0];
            this.stackResult[0] = null;
            return var2;
        } else {
            return null;
        }
    }

    /**
     * Sets the given item stack to the specified slot in the inventory (can be
     * crafting or armor sections).
     */
    public void setInventorySlotContents(int p_70299_1_, ItemStack p_70299_2_) {
        this.stackResult[0] = p_70299_2_;
    }

    /**
     * Returns the maximum stack size for a inventory slot.
     */
    public int getInventoryStackLimit() {
        return 64;
    }

    /**
     * Called when an the contents of an Inventory change, usually
     */
    public void onInventoryChanged() {
    }

    /**
     * Do not make give this method the name canInteractWith because it clashes with
     * Container
     */
    public boolean isUseableByPlayer(EntityPlayer p_70300_1_) {
        return true;
    }

    public void openInventory() {
    }

    public void closeInventory() {
    }

    /**
     * Returns true if automation is allowed to insert the given stack (ignoring
     * stack size) into the given slot.
     */
    public boolean isItemValidForSlot(int p_94041_1_, ItemStack p_94041_2_) {
        return true;
    }

    @Override
    public gg.mineral.bot.api.inv.item.ItemStack getHeldItemStack() {
        return null;
    }

    @Override
    public int getHeldSlot() {
        return 0;
    }

    @Override
    public gg.mineral.bot.api.inv.item.ItemStack getItemStackAt(int slot) {
        return getStackInSlot(slot);
    }

    @Override
    public int findSlot(gg.mineral.bot.api.inv.item.Item item) {
        return findSlot((Item) item);
    }

    @Override
    public int findSlot(int id) {
        Item item = Item.getItemById(id);
        return item == null ? -1 : findSlot(item);
    }

    @Override
    public gg.mineral.bot.api.inv.item.@Nullable ItemStack getHelmet() {
        return null;
    }

    @Override
    public gg.mineral.bot.api.inv.item.@Nullable ItemStack getChestplate() {
        return null;
    }

    @Override
    public gg.mineral.bot.api.inv.item.@Nullable ItemStack getLeggings() {
        return null;
    }

    @Override
    public gg.mineral.bot.api.inv.item.@Nullable ItemStack getBoots() {
        return null;
    }
}
