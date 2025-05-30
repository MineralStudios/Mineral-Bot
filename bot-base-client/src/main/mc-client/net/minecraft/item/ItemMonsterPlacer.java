package net.minecraft.item;

import java.util.Iterator;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Facing;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class ItemMonsterPlacer extends Item {
    private IIcon theIcon;

    public ItemMonsterPlacer() {
        this.setHasSubtypes(true);
        this.setCreativeTab(CreativeTabs.tabMisc);
    }

    public String getItemStackDisplayName(ItemStack p_77653_1_) {
        String var2 = ("" + StatCollector.translateToLocal(this.getUnlocalizedName() + ".name")).trim();
        String var3 = EntityList.getStringFromID(p_77653_1_.getItemDamage());

        if (var3 != null) {
            var2 = var2 + " " + StatCollector.translateToLocal("entity." + var3 + ".name");
        }

        return var2;
    }

    public int getColorFromItemStack(ItemStack p_82790_1_, int p_82790_2_) {
        EntityList.EntityEggInfo var3 = (EntityList.EntityEggInfo) EntityList.entityEggs
                .get(p_82790_1_.getItemDamage());
        return var3 != null ? (p_82790_2_ == 0 ? var3.primaryColor : var3.secondaryColor) : 16777215;
    }

    public boolean requiresMultipleRenderPasses() {
        return true;
    }

    /**
     * Gets an icon index based on an item's damage value and the given render pass
     */
    public IIcon getIconFromDamageForRenderPass(int p_77618_1_, int p_77618_2_) {
        return p_77618_2_ > 0 ? this.theIcon : super.getIconFromDamageForRenderPass(p_77618_1_, p_77618_2_);
    }

    /**
     * Callback for item usage. If the item does something special on right
     * clicking, he will have one of those. Return
     * True if something happen and false if it don't. This is for ITEMS, not BLOCKS
     */
    public boolean onItemUse(ItemStack p_77648_1_, EntityPlayer p_77648_2_, World p_77648_3_, int p_77648_4_,
            int p_77648_5_, int p_77648_6_, int p_77648_7_, float p_77648_8_, float p_77648_9_, float p_77648_10_) {
        if (p_77648_3_.isClient) {
            return true;
        } else {
            Block var11 = p_77648_3_.getBlock(p_77648_4_, p_77648_5_, p_77648_6_);
            p_77648_4_ += Facing.offsetsXForSide[p_77648_7_];
            p_77648_5_ += Facing.offsetsYForSide[p_77648_7_];
            p_77648_6_ += Facing.offsetsZForSide[p_77648_7_];
            double var12 = 0.0D;

            if (p_77648_7_ == 1 && var11.getRenderType() == 11) {
                var12 = 0.5D;
            }

            Entity var14 = spawnCreature(p_77648_3_, p_77648_1_.getItemDamage(), (double) p_77648_4_ + 0.5D,
                    (double) p_77648_5_ + var12, (double) p_77648_6_ + 0.5D);

            if (var14 != null) {
                if (var14 instanceof EntityLivingBase && p_77648_1_.hasDisplayName()) {
                    ((EntityLiving) var14).setCustomNameTag(p_77648_1_.getDisplayName());
                }

                if (!p_77648_2_.capabilities.isCreativeMode) {
                    --p_77648_1_.stackSize;
                }
            }

            return true;
        }
    }

    /**
     * Called whenever this item is equipped and the right mouse button is pressed.
     * Args: itemStack, world, entityPlayer
     */
    public ItemStack onItemRightClick(ItemStack p_77659_1_, World p_77659_2_, EntityPlayer p_77659_3_) {
        if (p_77659_2_.isClient) {
            return p_77659_1_;
        } else {
            MovingObjectPosition var4 = this.getMovingObjectPositionFromPlayer(p_77659_2_, p_77659_3_, true);

            if (var4 == null) {
                return p_77659_1_;
            } else {
                if (var4.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                    int var5 = var4.blockX;
                    int var6 = var4.blockY;
                    int var7 = var4.blockZ;

                    if (!p_77659_2_.canMineBlock(p_77659_3_, var5, var6, var7)) {
                        return p_77659_1_;
                    }

                    if (!p_77659_3_.canPlayerEdit(var5, var6, var7, var4.sideHit, p_77659_1_)) {
                        return p_77659_1_;
                    }

                    if (p_77659_2_.getBlock(var5, var6, var7) instanceof BlockLiquid) {
                        Entity var8 = spawnCreature(p_77659_2_, p_77659_1_.getItemDamage(), (double) var5,
                                (double) var6, (double) var7);

                        if (var8 != null) {
                            if (var8 instanceof EntityLivingBase && p_77659_1_.hasDisplayName()) {
                                ((EntityLiving) var8).setCustomNameTag(p_77659_1_.getDisplayName());
                            }

                            if (!p_77659_3_.capabilities.isCreativeMode) {
                                --p_77659_1_.stackSize;
                            }
                        }
                    }
                }

                return p_77659_1_;
            }
        }
    }

    /**
     * Spawns the creature specified by the egg's type in the location specified by
     * the last three parameters.
     * Parameters: world, entityID, x, y, z.
     */
    public static Entity spawnCreature(World p_77840_0_, int p_77840_1_, double p_77840_2_, double p_77840_4_,
            double p_77840_6_) {
        if (!EntityList.entityEggs.containsKey(p_77840_1_)) {
            return null;
        } else {
            Entity var8 = null;

            for (int var9 = 0; var9 < 1; ++var9) {
                var8 = EntityList.createEntityByID(p_77840_1_, p_77840_0_);

                if (var8 != null && var8 instanceof EntityLivingBase) {
                    EntityLiving var10 = (EntityLiving) var8;
                    var8.setLocationAndAngles(p_77840_2_, p_77840_4_, p_77840_6_,
                            MathHelper.wrapAngleTo180_float(p_77840_0_.rand.nextFloat() * 360.0F), 0.0F);
                    var10.rotationYawHead = var10.rotationYaw;
                    var10.renderYawOffset = var10.rotationYaw;
                    var10.onSpawnWithEgg((IEntityLivingData) null);
                    p_77840_0_.spawnEntityInWorld(var8);
                    var10.playLivingSound();
                }
            }

            return var8;
        }
    }

    /**
     * This returns the sub items
     */
    @Override
    public void getSubItems(Item p_150895_1_, CreativeTabs p_150895_2_, List<ItemStack> p_150895_3_) {
        Iterator var4 = EntityList.entityEggs.values().iterator();

        while (var4.hasNext()) {
            EntityList.EntityEggInfo var5 = (EntityList.EntityEggInfo) var4.next();
            p_150895_3_.add(new ItemStack(p_150895_1_, 1, var5.spawnedID));
        }
    }

    public void registerIcons(IIconRegister p_94581_1_) {
        super.registerIcons(p_94581_1_);
        this.theIcon = p_94581_1_.registerIcon(this.getIconString() + "_overlay");
    }
}
