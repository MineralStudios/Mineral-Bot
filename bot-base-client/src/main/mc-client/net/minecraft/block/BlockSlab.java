package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.Facing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public abstract class BlockSlab extends Block {
    protected final boolean field_150004_a;

    public BlockSlab(boolean p_i45410_1_, Material p_i45410_2_) {
        super(p_i45410_2_);
        this.field_150004_a = p_i45410_1_;

        if (p_i45410_1_) {
            this.opaque = true;
        } else {
            this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
        }

        this.setLightOpacity(255);
    }

    public void setBlockBoundsBasedOnState(IBlockAccess p_149719_1_, int p_149719_2_, int p_149719_3_,
            int p_149719_4_) {
        if (this.field_150004_a) {
            this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        } else {
            boolean var5 = (p_149719_1_.getBlockMetadata(p_149719_2_, p_149719_3_, p_149719_4_) & 8) != 0;

            if (var5) {
                this.setBlockBounds(0.0F, 0.5F, 0.0F, 1.0F, 1.0F, 1.0F);
            } else {
                this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
            }
        }
    }

    /**
     * Sets the block's bounds for rendering it as an item
     */
    public void setBlockBoundsForItemRender() {
        if (this.field_150004_a) {
            this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        } else {
            this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
        }
    }

    @Override
    public void addCollisionBoxesToList(World p_149743_1_, int p_149743_2_, int p_149743_3_, int p_149743_4_,
            AxisAlignedBB p_149743_5_, List<AxisAlignedBB> p_149743_6_, Entity p_149743_7_) {
        this.setBlockBoundsBasedOnState(p_149743_1_, p_149743_2_, p_149743_3_, p_149743_4_);
        super.addCollisionBoxesToList(p_149743_1_, p_149743_2_, p_149743_3_, p_149743_4_, p_149743_5_, p_149743_6_,
                p_149743_7_);
    }

    public boolean isOpaqueCube() {
        return this.field_150004_a;
    }

    public int onBlockPlaced(World p_149660_1_, int p_149660_2_, int p_149660_3_, int p_149660_4_, int p_149660_5_,
            float p_149660_6_, float p_149660_7_, float p_149660_8_, int p_149660_9_) {
        return this.field_150004_a ? p_149660_9_
                : (p_149660_5_ != 0 && (p_149660_5_ == 1 || (double) p_149660_7_ <= 0.5D) ? p_149660_9_
                        : p_149660_9_ | 8);
    }

    /**
     * Returns the quantity of items to drop on block destruction.
     */
    public int quantityDropped(Random p_149745_1_) {
        return this.field_150004_a ? 2 : 1;
    }

    /**
     * Determines the damage on the item the block drops. Used in cloth and wood.
     */
    public int damageDropped(int p_149692_1_) {
        return p_149692_1_ & 7;
    }

    public boolean renderAsNormalBlock() {
        return this.field_150004_a;
    }

    public boolean shouldSideBeRendered(IBlockAccess p_149646_1_, int p_149646_2_, int p_149646_3_, int p_149646_4_,
            int p_149646_5_) {
        if (this.field_150004_a) {
            return super.shouldSideBeRendered(p_149646_1_, p_149646_2_, p_149646_3_, p_149646_4_, p_149646_5_);
        } else if (p_149646_5_ != 1 && p_149646_5_ != 0
                && !super.shouldSideBeRendered(p_149646_1_, p_149646_2_, p_149646_3_, p_149646_4_, p_149646_5_)) {
            return false;
        } else {
            int var6 = p_149646_2_ + Facing.offsetsXForSide[Facing.oppositeSide[p_149646_5_]];
            int var7 = p_149646_3_ + Facing.offsetsYForSide[Facing.oppositeSide[p_149646_5_]];
            int var8 = p_149646_4_ + Facing.offsetsZForSide[Facing.oppositeSide[p_149646_5_]];
            boolean var9 = (p_149646_1_.getBlockMetadata(var6, var7, var8) & 8) != 0;
            return var9
                    ? (p_149646_5_ == 0 ? true
                            : (p_149646_5_ == 1 && super.shouldSideBeRendered(p_149646_1_, p_149646_2_, p_149646_3_,
                                    p_149646_4_, p_149646_5_)
                                            ? true
                                            : !func_150003_a(
                                                    p_149646_1_.getBlock(p_149646_2_, p_149646_3_, p_149646_4_))
                                                    || (p_149646_1_.getBlockMetadata(p_149646_2_, p_149646_3_,
                                                            p_149646_4_) & 8) == 0))
                    : (p_149646_5_ == 1 ? true
                            : (p_149646_5_ == 0 && super.shouldSideBeRendered(p_149646_1_, p_149646_2_, p_149646_3_,
                                    p_149646_4_, p_149646_5_)
                                            ? true
                                            : !func_150003_a(
                                                    p_149646_1_.getBlock(p_149646_2_, p_149646_3_, p_149646_4_))
                                                    || (p_149646_1_.getBlockMetadata(p_149646_2_, p_149646_3_,
                                                            p_149646_4_) & 8) != 0));
        }
    }

    private static boolean func_150003_a(Block p_150003_0_) {
        return p_150003_0_ == Blocks.stone_slab || p_150003_0_ == Blocks.wooden_slab;
    }

    public abstract String func_150002_b(int p_150002_1_);

    /**
     * Get the block's damage value (for use with pick block).
     */
    public int getDamageValue(World p_149643_1_, int p_149643_2_, int p_149643_3_, int p_149643_4_) {
        return super.getDamageValue(p_149643_1_, p_149643_2_, p_149643_3_, p_149643_4_) & 7;
    }

    /**
     * Gets an item for the block being called on. Args: world, x, y, z
     */
    public Item getItem(World p_149694_1_, int p_149694_2_, int p_149694_3_, int p_149694_4_) {
        return func_150003_a(this) ? Item.getItemFromBlock(this)
                : (this == Blocks.double_stone_slab ? Item.getItemFromBlock(Blocks.stone_slab)
                        : (this == Blocks.double_wooden_slab ? Item.getItemFromBlock(Blocks.wooden_slab)
                                : Item.getItemFromBlock(Blocks.stone_slab)));
    }
}
