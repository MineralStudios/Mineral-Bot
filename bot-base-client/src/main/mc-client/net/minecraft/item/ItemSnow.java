package net.minecraft.item;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class ItemSnow extends ItemBlockWithMetadata {

    public ItemSnow(Block p_i45354_1_, Block p_i45354_2_) {
        super(p_i45354_1_, p_i45354_2_);
    }

    /**
     * Callback for item usage. If the item does something special on right
     * clicking, he will have one of those. Return
     * True if something happen and false if it don't. This is for ITEMS, not BLOCKS
     */
    public boolean onItemUse(ItemStack p_77648_1_, EntityPlayer p_77648_2_, World p_77648_3_, int p_77648_4_,
            int p_77648_5_, int p_77648_6_, int p_77648_7_, float p_77648_8_, float p_77648_9_, float p_77648_10_) {
        if (p_77648_1_.stackSize == 0) {
            return false;
        } else if (!p_77648_2_.canPlayerEdit(p_77648_4_, p_77648_5_, p_77648_6_, p_77648_7_, p_77648_1_)) {
            return false;
        } else {
            Block var11 = p_77648_3_.getBlock(p_77648_4_, p_77648_5_, p_77648_6_);

            if (var11 == Blocks.snow_layer) {
                int var12 = p_77648_3_.getBlockMetadata(p_77648_4_, p_77648_5_, p_77648_6_);
                int var13 = var12 & 7;

                if (var13 <= 6
                        && p_77648_3_.checkNoEntityCollision(this.field_150939_a
                                .getCollisionBoundingBoxFromPool(p_77648_3_, p_77648_4_, p_77648_5_, p_77648_6_))
                        && p_77648_3_.setBlockMetadataWithNotify(p_77648_4_, p_77648_5_, p_77648_6_,
                                var13 + 1 | var12 & -8, 2)) {
                    p_77648_3_.playSoundEffect((double) ((float) p_77648_4_ + 0.5F),
                            (double) ((float) p_77648_5_ + 0.5F), (double) ((float) p_77648_6_ + 0.5F),
                            this.field_150939_a.stepSound.func_150496_b(),
                            (this.field_150939_a.stepSound.func_150497_c() + 1.0F) / 2.0F,
                            this.field_150939_a.stepSound.func_150494_d() * 0.8F);
                    --p_77648_1_.stackSize;
                    return true;
                }
            }

            return super.onItemUse(p_77648_1_, p_77648_2_, p_77648_3_, p_77648_4_, p_77648_5_, p_77648_6_, p_77648_7_,
                    p_77648_8_, p_77648_9_, p_77648_10_);
        }
    }
}
