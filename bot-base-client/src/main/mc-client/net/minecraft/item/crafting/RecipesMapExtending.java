package net.minecraft.item.crafting;

import org.eclipse.jdt.annotation.Nullable;

import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapData;

public class RecipesMapExtending extends ShapedRecipes {

    public RecipesMapExtending() {
        super(3, 3,
                new ItemStack[]{new ItemStack(Items.paper), new ItemStack(Items.paper), new ItemStack(Items.paper),
                        new ItemStack(Items.paper), new ItemStack(Items.filled_map, 0, 32767),
                        new ItemStack(Items.paper), new ItemStack(Items.paper), new ItemStack(Items.paper),
                        new ItemStack(Items.paper)},
                new ItemStack(Items.map, 0, 0));
    }

    /**
     * Used to check if a recipe matches current crafting inventory
     */
    public boolean matches(InventoryCrafting p_77569_1_, World p_77569_2_) {
        if (!super.matches(p_77569_1_, p_77569_2_)) {
            return false;
        } else {
            ItemStack var3 = null;

            for (int var4 = 0; var4 < p_77569_1_.getSizeInventory() && var3 == null; ++var4) {
                ItemStack var5 = p_77569_1_.getStackInSlot(var4);

                if (var5 != null && var5.getItem() == Items.filled_map) {
                    var3 = var5;
                }
            }

            if (var3 == null) {
                return false;
            } else {
                MapData var6 = Items.filled_map.getMapData(var3, p_77569_2_);
                return var6 == null ? false : var6.scale < 4;
            }
        }
    }

    /**
     * Returns an Item that is the result of this recipe
     */
    @Nullable
    public ItemStack getCraftingResult(InventoryCrafting p_77572_1_) {
        ItemStack var2 = null;

        for (int var3 = 0; var3 < p_77572_1_.getSizeInventory() && var2 == null; ++var3) {
            ItemStack var4 = p_77572_1_.getStackInSlot(var3);

            if (var4 != null && var4.getItem() == Items.filled_map) {
                var2 = var4;
            }
        }

        var2 = var2 != null ? var2.copy() : null;

        if (var2 != null) {
            var2.stackSize = 1;

            if (var2.getTagCompound() == null)
                var2.setTagCompound(new NBTTagCompound());

            var2.getTagCompound().setBoolean("map_is_scaling", true);
        }
        return var2;
    }
}
