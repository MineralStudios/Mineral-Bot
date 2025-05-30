package net.minecraft.entity.item;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityXPOrb extends Entity {
    /**
     * A constantly increasing value that RenderXPOrb uses to control the colour
     * shifting (Green / yellow)
     */
    public int xpColor;

    /** The age of the XP orb in ticks. */
    public int xpOrbAge;
    public int field_70532_c;

    /** The health of this XP orb. */
    private int xpOrbHealth = 5;

    /** This is how much XP this orb has. */
    private int xpValue;

    /** The closest EntityPlayer to this orb. */
    private EntityPlayer closestPlayer;

    /** Threshold color for tracking players */
    private int xpTargetColor;

    public EntityXPOrb(World p_i1585_1_, double p_i1585_2_, double p_i1585_4_, double p_i1585_6_, int p_i1585_8_) {
        super(p_i1585_1_);
        this.setSize(0.5F, 0.5F);
        this.yOffset = this.height / 2.0F;
        this.setPosition(p_i1585_2_, p_i1585_4_, p_i1585_6_);
        this.rotationYaw = (float) (Math.random() * 360.0D);
        this.motionX = (double) ((float) (Math.random() * 0.20000000298023224D - 0.10000000149011612D) * 2.0F);
        this.motionY = (double) ((float) (Math.random() * 0.2D) * 2.0F);
        this.motionZ = (double) ((float) (Math.random() * 0.20000000298023224D - 0.10000000149011612D) * 2.0F);
        this.xpValue = p_i1585_8_;
    }

    /**
     * returns if this entity triggers Block.onEntityWalking on the blocks they walk
     * on. used for spiders and wolves to
     * prevent them from trampling crops
     */
    protected boolean canTriggerWalking() {
        return false;
    }

    public EntityXPOrb(World p_i1586_1_) {
        super(p_i1586_1_);
        this.setSize(0.25F, 0.25F);
        this.yOffset = this.height / 2.0F;
    }

    protected void entityInit() {
    }

    public int getBrightnessForRender(float p_70070_1_) {
        float var2 = 0.5F;

        if (var2 < 0.0F) {
            var2 = 0.0F;
        }

        if (var2 > 1.0F) {
            var2 = 1.0F;
        }

        int var3 = super.getBrightnessForRender(p_70070_1_);
        int var4 = var3 & 255;
        int var5 = var3 >> 16 & 255;
        var4 += (int) (var2 * 15.0F * 16.0F);

        if (var4 > 240) {
            var4 = 240;
        }

        return var4 | var5 << 16;
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate() {
        super.onUpdate();

        if (this.field_70532_c > 0) {
            --this.field_70532_c;
        }

        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        this.motionY -= 0.029999999329447746D;

        if (this.worldObj.getBlock(MathHelper.floor_double(this.posX), MathHelper.floor_double(this.posY),
                MathHelper.floor_double(this.posZ)).getMaterial() == Material.lava) {
            this.motionY = 0.20000000298023224D;
            this.motionX = (double) ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F);
            this.motionZ = (double) ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F);
            this.playSound("random.fizz", 0.4F, 2.0F + this.rand.nextFloat() * 0.4F);
        }

        this.func_145771_j(this.posX, (this.boundingBox.minY + this.boundingBox.maxY) / 2.0D, this.posZ);
        double var1 = 8.0D;

        if (this.xpTargetColor < this.xpColor - 20 + this.getEntityId() % 100) {
            if (this.closestPlayer == null || this.closestPlayer.getDistanceSqToEntity(this) > var1 * var1) {
                this.closestPlayer = this.worldObj.getClosestPlayerToEntity(this, var1);
            }

            this.xpTargetColor = this.xpColor;
        }

        if (this.closestPlayer != null) {
            double var3 = (this.closestPlayer.posX - this.posX) / var1;
            double var5 = (this.closestPlayer.posY + (double) this.closestPlayer.getEyeHeight() - this.posY) / var1;
            double var7 = (this.closestPlayer.posZ - this.posZ) / var1;
            double var9 = Math.sqrt(var3 * var3 + var5 * var5 + var7 * var7);
            double var11 = 1.0D - var9;

            if (var11 > 0.0D) {
                var11 *= var11;
                this.motionX += var3 / var9 * var11 * 0.1D;
                this.motionY += var5 / var9 * var11 * 0.1D;
                this.motionZ += var7 / var9 * var11 * 0.1D;
            }
        }

        this.moveEntity(this.motionX, this.motionY, this.motionZ);
        float var13 = 0.98F;

        if (this.onGround) {
            var13 = this.worldObj.getBlock(MathHelper.floor_double(this.posX),
                    MathHelper.floor_double(this.boundingBox.minY) - 1, MathHelper.floor_double(this.posZ)).slipperiness
                    * 0.98F;
        }

        this.motionX *= (double) var13;
        this.motionY *= 0.9800000190734863D;
        this.motionZ *= (double) var13;

        if (this.onGround) {
            this.motionY *= -0.8999999761581421D;
        }

        ++this.xpColor;
        ++this.xpOrbAge;

        if (this.xpOrbAge >= 6000) {
            this.setDead();
        }
    }

    /**
     * Returns if this entity is in water and will end up adding the waters velocity
     * to the entity
     */
    public boolean handleWaterMovement() {
        return this.worldObj.handleMaterialAcceleration(this.boundingBox, Material.water, this);
    }

    /**
     * Will deal the specified amount of damage to the entity if the entity isn't
     * immune to fire damage. Args:
     * amountDamage
     */
    protected void dealFireDamage(int p_70081_1_) {
        this.attackEntityFrom(DamageSource.inFire, (float) p_70081_1_);
    }

    /**
     * Called when the entity is attacked.
     */
    public boolean attackEntityFrom(DamageSource p_70097_1_, float p_70097_2_) {
        if (this.isEntityInvulnerable()) {
            return false;
        } else {
            this.setBeenAttacked();
            this.xpOrbHealth = (int) ((float) this.xpOrbHealth - p_70097_2_);

            if (this.xpOrbHealth <= 0) {
                this.setDead();
            }

            return false;
        }
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(NBTTagCompound p_70014_1_) {
        p_70014_1_.setShort("Health", (short) ((byte) this.xpOrbHealth));
        p_70014_1_.setShort("Age", (short) this.xpOrbAge);
        p_70014_1_.setShort("Value", (short) this.xpValue);
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound p_70037_1_) {
        this.xpOrbHealth = p_70037_1_.getShort("Health") & 255;
        this.xpOrbAge = p_70037_1_.getShort("Age");
        this.xpValue = p_70037_1_.getShort("Value");
    }

    /**
     * Called by a player entity when they collide with an entity
     */
    public void onCollideWithPlayer(EntityPlayer p_70100_1_) {
        if (!this.worldObj.isClient) {
            if (this.field_70532_c == 0 && p_70100_1_.xpCooldown == 0) {
                p_70100_1_.xpCooldown = 2;
                this.worldObj.playSoundAtEntity(p_70100_1_, "random.orb", 0.1F,
                        0.5F * ((this.rand.nextFloat() - this.rand.nextFloat()) * 0.7F + 1.8F));
                p_70100_1_.onItemPickup(this, 1);
                p_70100_1_.addExperience(this.xpValue);
                this.setDead();
            }
        }
    }

    /**
     * Returns the XP value of this XP orb.
     */
    public int getXpValue() {
        return this.xpValue;
    }

    /**
     * Returns a number from 1 to 10 based on how much XP this orb is worth. This is
     * used by RenderXPOrb to determine
     * what texture to use.
     */
    public int getTextureByXP() {
        return this.xpValue >= 2477 ? 10
                : (this.xpValue >= 1237 ? 9
                        : (this.xpValue >= 617 ? 8
                                : (this.xpValue >= 307 ? 7
                                        : (this.xpValue >= 149 ? 6
                                                : (this.xpValue >= 73 ? 5
                                                        : (this.xpValue >= 37 ? 4
                                                                : (this.xpValue >= 17 ? 3
                                                                        : (this.xpValue >= 7 ? 2
                                                                                : (this.xpValue >= 3 ? 1 : 0)))))))));
    }

    /**
     * Get a fragment of the maximum experience points value for the supplied value
     * of experience points value.
     */
    public static int getXPSplit(int p_70527_0_) {
        return p_70527_0_ >= 2477 ? 2477
                : (p_70527_0_ >= 1237 ? 1237
                        : (p_70527_0_ >= 617 ? 617
                                : (p_70527_0_ >= 307 ? 307
                                        : (p_70527_0_ >= 149 ? 149
                                                : (p_70527_0_ >= 73 ? 73
                                                        : (p_70527_0_ >= 37 ? 37
                                                                : (p_70527_0_ >= 17 ? 17
                                                                        : (p_70527_0_ >= 7 ? 7
                                                                                : (p_70527_0_ >= 3 ? 3 : 1)))))))));
    }

    /**
     * If returns false, the item will not inflict any damage against entities.
     */
    public boolean canAttackWithItem() {
        return false;
    }
}
