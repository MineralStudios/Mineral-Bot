package net.minecraft.entity.item;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityFireworkRocket extends Entity {
    /** The age of the firework in ticks. */
    private int fireworkAge;

    /**
     * The lifetime of the firework in ticks. When the age reaches the lifetime the
     * firework explodes.
     */
    private int lifetime;

    public EntityFireworkRocket(World p_i1762_1_) {
        super(p_i1762_1_);
        this.setSize(0.25F, 0.25F);
    }

    protected void entityInit() {
        this.dataWatcher.addObjectByDataType(8, 5);
    }

    /**
     * Checks if the entity is in range to render by using the past in distance and
     * comparing it to its average edge
     * length * 64 * renderDistanceWeight Args: distance
     */
    public boolean isInRangeToRenderDist(double p_70112_1_) {
        return p_70112_1_ < 4096.0D;
    }

    public EntityFireworkRocket(World p_i1763_1_, double p_i1763_2_, double p_i1763_4_, double p_i1763_6_,
            ItemStack p_i1763_8_) {
        super(p_i1763_1_);
        this.fireworkAge = 0;
        this.setSize(0.25F, 0.25F);
        this.setPosition(p_i1763_2_, p_i1763_4_, p_i1763_6_);
        this.yOffset = 0.0F;
        int var9 = 1;

        if (p_i1763_8_ != null && p_i1763_8_.hasTagCompound()) {
            this.dataWatcher.updateObject(8, p_i1763_8_);
            NBTTagCompound var10 = p_i1763_8_.getTagCompound();
            NBTTagCompound var11 = var10.getCompoundTag("Fireworks");

            if (var11 != null) {
                var9 += var11.getByte("Flight");
            }
        }

        this.motionX = this.rand.nextGaussian() * 0.001D;
        this.motionZ = this.rand.nextGaussian() * 0.001D;
        this.motionY = 0.05D;
        this.lifetime = 10 * var9 + this.rand.nextInt(6) + this.rand.nextInt(7);
    }

    /**
     * Sets the velocity to the args. Args: x, y, z
     */
    public void setVelocity(double p_70016_1_, double p_70016_3_, double p_70016_5_) {
        this.motionX = p_70016_1_;
        this.motionY = p_70016_3_;
        this.motionZ = p_70016_5_;

        if (this.prevRotationPitch == 0.0F && this.prevRotationYaw == 0.0F) {
            float var7 = MathHelper.sqrt_double(p_70016_1_ * p_70016_1_ + p_70016_5_ * p_70016_5_);
            this.prevRotationYaw = this.rotationYaw = (float) (Math.atan2(p_70016_1_, p_70016_5_) * 180.0D / Math.PI);
            this.prevRotationPitch = this.rotationPitch = (float) (Math.atan2(p_70016_3_, (double) var7) * 180.0D
                    / Math.PI);
        }
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate() {
        this.lastTickPosX = this.posX;
        this.lastTickPosY = this.posY;
        this.lastTickPosZ = this.posZ;
        super.onUpdate();
        this.motionX *= 1.15D;
        this.motionZ *= 1.15D;
        this.motionY += 0.04D;
        this.moveEntity(this.motionX, this.motionY, this.motionZ);
        float var1 = MathHelper.sqrt_double(this.motionX * this.motionX + this.motionZ * this.motionZ);
        this.rotationYaw = (float) (Math.atan2(this.motionX, this.motionZ) * 180.0D / Math.PI);

        for (this.rotationPitch = (float) (Math.atan2(this.motionY, (double) var1) * 180.0D
                / Math.PI); this.rotationPitch - this.prevRotationPitch < -180.0F; this.prevRotationPitch -= 360.0F) {
            ;
        }

        while (this.rotationPitch - this.prevRotationPitch >= 180.0F) {
            this.prevRotationPitch += 360.0F;
        }

        while (this.rotationYaw - this.prevRotationYaw < -180.0F) {
            this.prevRotationYaw -= 360.0F;
        }

        while (this.rotationYaw - this.prevRotationYaw >= 180.0F) {
            this.prevRotationYaw += 360.0F;
        }

        this.rotationPitch = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * 0.2F;
        this.rotationYaw = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * 0.2F;

        if (this.fireworkAge == 0) {
            this.worldObj.playSoundAtEntity(this, "fireworks.launch", 3.0F, 1.0F);
        }

        ++this.fireworkAge;

        if (this.worldObj.isClient && this.fireworkAge % 2 < 2) {
            this.worldObj.spawnParticle("fireworksSpark", this.posX, this.posY - 0.3D, this.posZ,
                    this.rand.nextGaussian() * 0.05D, -this.motionY * 0.5D, this.rand.nextGaussian() * 0.05D);
        }

        if (!this.worldObj.isClient && this.fireworkAge > this.lifetime) {
            this.worldObj.setEntityState(this, (byte) 17);
            this.setDead();
        }
    }

    public void handleHealthUpdate(byte p_70103_1_) {
        if (p_70103_1_ == 17 && this.worldObj.isClient) {
            ItemStack var2 = this.dataWatcher.getWatchableObjectItemStack(8);
            NBTTagCompound var3 = null;

            if (var2 != null && var2.hasTagCompound()) {
                var3 = var2.getTagCompound().getCompoundTag("Fireworks");
            }

            this.worldObj.makeFireworks(this.posX, this.posY, this.posZ, this.motionX, this.motionY, this.motionZ,
                    var3);
        }

        super.handleHealthUpdate(p_70103_1_);
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(NBTTagCompound p_70014_1_) {
        p_70014_1_.setInteger("Life", this.fireworkAge);
        p_70014_1_.setInteger("LifeTime", this.lifetime);
        ItemStack var2 = this.dataWatcher.getWatchableObjectItemStack(8);

        if (var2 != null) {
            NBTTagCompound var3 = new NBTTagCompound();
            var2.writeToNBT(var3);
            p_70014_1_.setTag("FireworksItem", var3);
        }
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound p_70037_1_) {
        this.fireworkAge = p_70037_1_.getInteger("Life");
        this.lifetime = p_70037_1_.getInteger("LifeTime");
        NBTTagCompound var2 = p_70037_1_.getCompoundTag("FireworksItem");

        if (var2 != null) {
            ItemStack var3 = ItemStack.loadItemStackFromNBT(var2);

            if (var3 != null) {
                this.dataWatcher.updateObject(8, var3);
            }
        }
    }

    public float getShadowSize() {
        return 0.0F;
    }

    /**
     * Gets how bright this entity is.
     */
    public float getBrightness(float p_70013_1_) {
        return super.getBrightness(p_70013_1_);
    }

    public int getBrightnessForRender(float p_70070_1_) {
        return super.getBrightnessForRender(p_70070_1_);
    }

    /**
     * If returns false, the item will not inflict any damage against entities.
     */
    public boolean canAttackWithItem() {
        return false;
    }
}
