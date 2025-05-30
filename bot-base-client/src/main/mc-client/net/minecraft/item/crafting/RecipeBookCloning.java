package net.minecraft.item.crafting;

import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class RecipeBookCloning implements IRecipe {

    /**
     * Used to check if a recipe matches current crafting inventory
     */
    public boolean matches(InventoryCrafting p_77569_1_, World p_77569_2_) {
        int var3 = 0;
        ItemStack var4 = null;

        for (int var5 = 0; var5 < p_77569_1_.getSizeInventory(); ++var5) {
            ItemStack var6 = p_77569_1_.getStackInSlot(var5);

            if (var6 != null) {
                if (var6.getItem() == Items.written_book) {
                    if (var4 != null) {
                        return false;
                    }

                    var4 = var6;
                } else {
                    if (var6.getItem() != Items.writable_book) {
                        return false;
                    }

                    ++var3;
                }
            }
        }

        return var4 != null && var3 > 0;
    }

    /**
     * Returns an Item that is the result of this recipe
     */
    public ItemStack getCraftingResult(InventoryCrafting p_77572_1_) {
        int var2 = 0;
        ItemStack var3 = null;

        for (int var4 = 0; var4 < p_77572_1_.getSizeInventory(); ++var4) {
            ItemStack var5 = p_77572_1_.getStackInSlot(var4);

            if (var5 != null) {
                if (var5.getItem() == Items.written_book) {
                    if (var3 != null) {
                        return null;
                    }

                    var3 = var5;
                } else {
                    if (var5.getItem() != Items.writable_book) {
                        return null;
                    }

                    ++var2;
                }
            }
        }

        if (var3 != null && var2 >= 1) {
            ItemStack var6 = new ItemStack(Items.written_book, var2 + 1);
            var6.setTagCompound((NBTTagCompound) var3.getTagCompound().copy());

            if (var3.hasDisplayName()) {
                var6.setStackDisplayName(var3.getDisplayName());
            }

            return var6;
        } else {
            return null;
        }
    }

    /**
     * Returns the size of the recipe area
     */
    public int getRecipeSize() {
        return 9;
    }

    public ItemStack getRecipeOutput() {
        return null;
    }
}
