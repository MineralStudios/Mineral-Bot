package net.minecraft.entity.projectile;

import gg.mineral.bot.api.entity.throwable.ClientPotion;
import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;

public class EntityPotion extends EntityThrowable implements ClientPotion {
    /**
     * The damage value of the thrown potion that this EntityPotion represents.
     */
    private ItemStack potionDamage;

    public EntityPotion(World p_i1788_1_) {
        super(p_i1788_1_);
    }

    public EntityPotion(World p_i1789_1_, EntityLivingBase p_i1789_2_, int p_i1789_3_) {
        this(p_i1789_1_, p_i1789_2_, new ItemStack(Items.potionitem, 1, p_i1789_3_));
    }

    public EntityPotion(World p_i1790_1_, EntityLivingBase p_i1790_2_, ItemStack p_i1790_3_) {
        super(p_i1790_1_, p_i1790_2_);
        this.potionDamage = p_i1790_3_;
    }

    public EntityPotion(World p_i1791_1_, double p_i1791_2_, double p_i1791_4_, double p_i1791_6_, int p_i1791_8_) {
        this(p_i1791_1_, p_i1791_2_, p_i1791_4_, p_i1791_6_, new ItemStack(Items.potionitem, 1, p_i1791_8_));
    }

    public EntityPotion(World p_i1792_1_, double p_i1792_2_, double p_i1792_4_, double p_i1792_6_,
            ItemStack p_i1792_8_) {
        super(p_i1792_1_, p_i1792_2_, p_i1792_4_, p_i1792_6_);
        this.potionDamage = p_i1792_8_;
    }

    /**
     * Gets the amount of gravity to apply to the thrown entity with each tick.
     */
    protected float getGravityVelocity() {
        return 0.05F;
    }

    protected float func_70182_d() {
        return 0.5F;
    }

    protected float func_70183_g() {
        return -20.0F;
    }

    public void setPotionDamage(int p_82340_1_) {
        if (this.potionDamage == null) {
            this.potionDamage = new ItemStack(Items.potionitem, 1, 0);
        }

        this.potionDamage.setItemDamage(p_82340_1_);
    }

    /**
     * Returns the damage value of the thrown potion that this EntityPotion
     * represents.
     */
    public int getPotionDamage() {
        if (this.potionDamage == null) {
            this.potionDamage = new ItemStack(Items.potionitem, 1, 0);
        }

        return this.potionDamage.getItemDamage();
    }

    /**
     * Called when this EntityThrowable hits a block or entity.
     */
    protected void onImpact(MovingObjectPosition p_70184_1_) {
        if (!this.worldObj.isClient) {
            List var2 = Items.potionitem.getEffects(this.potionDamage);

            if (var2 != null && !var2.isEmpty()) {
                AxisAlignedBB var3 = this.boundingBox.expand(4.0D, 2.0D, 4.0D);
                List var4 = this.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, var3);

                if (var4 != null && !var4.isEmpty()) {
                    Iterator var5 = var4.iterator();

                    while (var5.hasNext()) {
                        EntityLivingBase var6 = (EntityLivingBase) var5.next();
                        double var7 = this.getDistanceSqToEntity(var6);

                        if (var7 < 16.0D) {
                            double var9 = 1.0D - Math.sqrt(var7) / 4.0D;

                            if (var6 == p_70184_1_.entityHit) {
                                var9 = 1.0D;
                            }

                            Iterator var11 = var2.iterator();

                            while (var11.hasNext()) {
                                PotionEffect var12 = (PotionEffect) var11.next();
                                int var13 = var12.getPotionID();

                                if (Potion.potionTypes[var13].isInstant()) {
                                    Potion.potionTypes[var13].affectEntity(this.getThrower(), var6,
                                            var12.getAmplifier(), var9);
                                } else {
                                    int var14 = (int) (var9 * (double) var12.getDuration() + 0.5D);

                                    if (var14 > 20) {
                                        var6.addPotionEffect(new PotionEffect(var13, var14, var12.getAmplifier()));
                                    }
                                }
                            }
                        }
                    }
                }
            }

            this.worldObj.playAuxSFX(2002, (int) Math.round(this.posX), (int) Math.round(this.posY),
                    (int) Math.round(this.posZ), this.getPotionDamage());
            this.setDead();
        }
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound p_70037_1_) {
        super.readEntityFromNBT(p_70037_1_);

        if (p_70037_1_.func_150297_b("Potion", 10)) {
            this.potionDamage = ItemStack.loadItemStackFromNBT(p_70037_1_.getCompoundTag("Potion"));
        } else {
            this.setPotionDamage(p_70037_1_.getInteger("potionValue"));
        }

        if (this.potionDamage == null) {
            this.setDead();
        }
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(NBTTagCompound p_70014_1_) {
        super.writeEntityToNBT(p_70014_1_);

        if (this.potionDamage != null) {
            p_70014_1_.setTag("Potion", this.potionDamage.writeToNBT(new NBTTagCompound()));
        }
    }

    @Override
    public int getPotionDurability() {
        return getPotionDamage();
    }
}
