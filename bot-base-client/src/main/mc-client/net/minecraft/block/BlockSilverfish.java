package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.ImmutablePair;

public class BlockSilverfish extends Block {
    public static final String[] field_150198_a = new String[] { "stone", "cobble", "brick", "mossybrick",
            "crackedbrick", "chiseledbrick" };

    public BlockSilverfish() {
        super(Material.field_151571_B);
        this.setHardness(0.0F);
        this.setCreativeTab(CreativeTabs.tabDecorations);
    }

    /**
     * Gets the block's texture. Args: side, meta
     */
    public IIcon getIcon(int p_149691_1_, int p_149691_2_) {
        switch (p_149691_2_) {
            case 1:
                return Blocks.cobblestone.getBlockTextureFromSide(p_149691_1_);

            case 2:
                return Blocks.stonebrick.getBlockTextureFromSide(p_149691_1_);

            case 3:
                return Blocks.stonebrick.getIcon(p_149691_1_, 1);

            case 4:
                return Blocks.stonebrick.getIcon(p_149691_1_, 2);

            case 5:
                return Blocks.stonebrick.getIcon(p_149691_1_, 3);

            default:
                return Blocks.stone.getBlockTextureFromSide(p_149691_1_);
        }
    }

    public void registerBlockIcons(IIconRegister p_149651_1_) {
    }

    public void onBlockDestroyedByPlayer(World p_149664_1_, int p_149664_2_, int p_149664_3_, int p_149664_4_,
            int p_149664_5_) {
        if (!p_149664_1_.isClient) {
            EntitySilverfish var6 = new EntitySilverfish(p_149664_1_);
            var6.setLocationAndAngles((double) p_149664_2_ + 0.5D, (double) p_149664_3_, (double) p_149664_4_ + 0.5D,
                    0.0F, 0.0F);
            p_149664_1_.spawnEntityInWorld(var6);
            var6.spawnExplosionParticle();
        }

        super.onBlockDestroyedByPlayer(p_149664_1_, p_149664_2_, p_149664_3_, p_149664_4_, p_149664_5_);
    }

    /**
     * Returns the quantity of items to drop on block destruction.
     */
    public int quantityDropped(Random p_149745_1_) {
        return 0;
    }

    public static boolean func_150196_a(Block p_150196_0_) {
        return p_150196_0_ == Blocks.stone || p_150196_0_ == Blocks.cobblestone || p_150196_0_ == Blocks.stonebrick;
    }

    public static int func_150195_a(Block p_150195_0_, int p_150195_1_) {
        if (p_150195_1_ == 0) {
            if (p_150195_0_ == Blocks.cobblestone) {
                return 1;
            }

            if (p_150195_0_ == Blocks.stonebrick) {
                return 2;
            }
        } else if (p_150195_0_ == Blocks.stonebrick) {
            switch (p_150195_1_) {
                case 1:
                    return 3;

                case 2:
                    return 4;

                case 3:
                    return 5;
            }
        }

        return 0;
    }

    public static ImmutablePair<Block, Integer> func_150197_b(int p_150197_0_) {
        switch (p_150197_0_) {
            case 1:
                return new ImmutablePair<>(Blocks.cobblestone, Integer.valueOf(0));

            case 2:
                return new ImmutablePair<>(Blocks.stonebrick, Integer.valueOf(0));

            case 3:
                return new ImmutablePair<>(Blocks.stonebrick, Integer.valueOf(1));

            case 4:
                return new ImmutablePair<>(Blocks.stonebrick, Integer.valueOf(2));

            case 5:
                return new ImmutablePair<>(Blocks.stonebrick, Integer.valueOf(3));

            default:
                return new ImmutablePair<>(Blocks.stone, Integer.valueOf(0));
        }
    }

    /**
     * Returns an item stack containing a single instance of the current block type.
     * 'i' is the block's subtype/damage
     * and is ignored for blocks which do not support subtypes. Blocks which cannot
     * be harvested should return null.
     */
    protected ItemStack createStackedBlock(int p_149644_1_) {
        switch (p_149644_1_) {
            case 1:
                return new ItemStack(Blocks.cobblestone);

            case 2:
                return new ItemStack(Blocks.stonebrick);

            case 3:
                return new ItemStack(Blocks.stonebrick, 1, 1);

            case 4:
                return new ItemStack(Blocks.stonebrick, 1, 2);

            case 5:
                return new ItemStack(Blocks.stonebrick, 1, 3);

            default:
                return new ItemStack(Blocks.stone);
        }
    }

    /**
     * Drops the block items with a specified chance of dropping the specified items
     */
    public void dropBlockAsItemWithChance(World p_149690_1_, int p_149690_2_, int p_149690_3_, int p_149690_4_,
            int p_149690_5_, float p_149690_6_, int p_149690_7_) {
        if (!p_149690_1_.isClient) {
            EntitySilverfish var8 = new EntitySilverfish(p_149690_1_);
            var8.setLocationAndAngles((double) p_149690_2_ + 0.5D, (double) p_149690_3_, (double) p_149690_4_ + 0.5D,
                    0.0F, 0.0F);
            p_149690_1_.spawnEntityInWorld(var8);
            var8.spawnExplosionParticle();
        }
    }

    /**
     * Get the block's damage value (for use with pick block).
     */
    public int getDamageValue(World p_149643_1_, int p_149643_2_, int p_149643_3_, int p_149643_4_) {
        return p_149643_1_.getBlockMetadata(p_149643_2_, p_149643_3_, p_149643_4_);
    }

    @Override
    public void getSubBlocks(Item p_149666_1_, CreativeTabs p_149666_2_, List<ItemStack> p_149666_3_) {
        for (int var4 = 0; var4 < field_150198_a.length; ++var4) {
            p_149666_3_.add(new ItemStack(p_149666_1_, 1, var4));
        }
    }
}
