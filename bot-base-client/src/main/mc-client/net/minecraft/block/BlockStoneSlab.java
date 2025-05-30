package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

public class BlockStoneSlab extends BlockSlab {
    public static final String[] field_150006_b = new String[] { "stone", "sand", "wood", "cobble", "brick",
            "smoothStoneBrick", "netherBrick", "quartz" };
    private IIcon field_150007_M;

    public BlockStoneSlab(boolean p_i45431_1_) {
        super(p_i45431_1_, Material.rock);
        this.setCreativeTab(CreativeTabs.tabBlock);
    }

    /**
     * Gets the block's texture. Args: side, meta
     */
    public IIcon getIcon(int p_149691_1_, int p_149691_2_) {
        int var3 = p_149691_2_ & 7;

        if (this.field_150004_a && (p_149691_2_ & 8) != 0) {
            p_149691_1_ = 1;
        }

        return var3 == 0 ? (p_149691_1_ != 1 && p_149691_1_ != 0 ? this.field_150007_M : this.blockIcon)
                : (var3 == 1 ? Blocks.sandstone.getBlockTextureFromSide(p_149691_1_)
                        : (var3 == 2 ? Blocks.planks.getBlockTextureFromSide(p_149691_1_)
                                : (var3 == 3 ? Blocks.cobblestone.getBlockTextureFromSide(p_149691_1_)
                                        : (var3 == 4 ? Blocks.brick_block.getBlockTextureFromSide(p_149691_1_)
                                                : (var3 == 5 ? Blocks.stonebrick.getIcon(p_149691_1_, 0)
                                                        : (var3 == 6 ? Blocks.nether_brick.getBlockTextureFromSide(1)
                                                                : (var3 == 7
                                                                        ? Blocks.quartz_block
                                                                                .getBlockTextureFromSide(p_149691_1_)
                                                                        : this.blockIcon)))))));
    }

    public void registerBlockIcons(IIconRegister p_149651_1_) {
        this.blockIcon = p_149651_1_.registerIcon("stone_slab_top");
        this.field_150007_M = p_149651_1_.registerIcon("stone_slab_side");
    }

    public Item getItemDropped(int p_149650_1_, Random p_149650_2_, int p_149650_3_) {
        return Item.getItemFromBlock(Blocks.stone_slab);
    }

    /**
     * Returns an item stack containing a single instance of the current block type.
     * 'i' is the block's subtype/damage
     * and is ignored for blocks which do not support subtypes. Blocks which cannot
     * be harvested should return null.
     */
    protected ItemStack createStackedBlock(int p_149644_1_) {
        return new ItemStack(Item.getItemFromBlock(Blocks.stone_slab), 2, p_149644_1_ & 7);
    }

    public String func_150002_b(int p_150002_1_) {
        if (p_150002_1_ < 0 || p_150002_1_ >= field_150006_b.length) {
            p_150002_1_ = 0;
        }

        return super.getUnlocalizedName() + "." + field_150006_b[p_150002_1_];
    }

    @Override
    public void getSubBlocks(Item p_149666_1_, CreativeTabs p_149666_2_, List<ItemStack> p_149666_3_) {
        if (p_149666_1_ != Item.getItemFromBlock(Blocks.double_stone_slab)) {
            for (int var4 = 0; var4 <= 7; ++var4) {
                if (var4 != 2) {
                    p_149666_3_.add(new ItemStack(p_149666_1_, 1, var4));
                }
            }
        }
    }
}
