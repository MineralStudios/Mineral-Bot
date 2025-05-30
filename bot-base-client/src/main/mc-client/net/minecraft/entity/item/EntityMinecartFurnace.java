package net.minecraft.entity.item;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class EntityMinecartFurnace extends EntityMinecart {
    private int fuel;
    public double pushX;
    public double pushZ;

    public EntityMinecartFurnace(World p_i1718_1_) {
        super(p_i1718_1_);
    }

    public EntityMinecartFurnace(World p_i1719_1_, double p_i1719_2_, double p_i1719_4_, double p_i1719_6_) {
        super(p_i1719_1_, p_i1719_2_, p_i1719_4_, p_i1719_6_);
    }

    public int getMinecartType() {
        return 2;
    }

    protected void entityInit() {
        super.entityInit();
        this.dataWatcher.addObject(16, (byte) 0);
    }

    /**
     * Called to update the entity's position/logic.
     */
    public void onUpdate() {
        super.onUpdate();

        if (this.fuel > 0) {
            --this.fuel;
        }

        if (this.fuel <= 0) {
            this.pushX = this.pushZ = 0.0D;
        }

        this.setMinecartPowered(this.fuel > 0);

        if (this.isMinecartPowered() && this.rand.nextInt(4) == 0) {
            this.worldObj.spawnParticle("largesmoke", this.posX, this.posY + 0.8D, this.posZ, 0.0D, 0.0D, 0.0D);
        }
    }

    public void killMinecart(DamageSource p_94095_1_) {
        super.killMinecart(p_94095_1_);

        if (!p_94095_1_.isExplosion()) {
            this.entityDropItem(new ItemStack(Blocks.furnace, 1), 0.0F);
        }
    }

    protected void func_145821_a(int p_145821_1_, int p_145821_2_, int p_145821_3_, double p_145821_4_,
            double p_145821_6_, Block p_145821_8_, int p_145821_9_) {
        super.func_145821_a(p_145821_1_, p_145821_2_, p_145821_3_, p_145821_4_, p_145821_6_, p_145821_8_, p_145821_9_);
        double var10 = this.pushX * this.pushX + this.pushZ * this.pushZ;

        if (var10 > 1.0E-4D && this.motionX * this.motionX + this.motionZ * this.motionZ > 0.001D) {
            var10 = (double) MathHelper.sqrt_double(var10);
            this.pushX /= var10;
            this.pushZ /= var10;

            if (this.pushX * this.motionX + this.pushZ * this.motionZ < 0.0D) {
                this.pushX = 0.0D;
                this.pushZ = 0.0D;
            } else {
                this.pushX = this.motionX;
                this.pushZ = this.motionZ;
            }
        }
    }

    protected void applyDrag() {
        double var1 = this.pushX * this.pushX + this.pushZ * this.pushZ;

        if (var1 > 1.0E-4D) {
            var1 = (double) MathHelper.sqrt_double(var1);
            this.pushX /= var1;
            this.pushZ /= var1;
            double var3 = 0.05D;
            this.motionX *= 0.800000011920929D;
            this.motionY *= 0.0D;
            this.motionZ *= 0.800000011920929D;
            this.motionX += this.pushX * var3;
            this.motionZ += this.pushZ * var3;
        } else {
            this.motionX *= 0.9800000190734863D;
            this.motionY *= 0.0D;
            this.motionZ *= 0.9800000190734863D;
        }

        super.applyDrag();
    }

    /**
     * First layer of player interaction
     */
    public boolean interactFirst(EntityPlayer p_130002_1_) {
        ItemStack var2 = p_130002_1_.inventory.getCurrentItem();

        if (var2 != null && var2.getItem() == Items.coal) {
            if (!p_130002_1_.capabilities.isCreativeMode && --var2.stackSize == 0) {
                p_130002_1_.inventory.setInventorySlotContents(p_130002_1_.inventory.currentItem, (ItemStack) null);
            }

            this.fuel += 3600;
        }

        this.pushX = this.posX - p_130002_1_.posX;
        this.pushZ = this.posZ - p_130002_1_.posZ;
        return true;
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    protected void writeEntityToNBT(NBTTagCompound p_70014_1_) {
        super.writeEntityToNBT(p_70014_1_);
        p_70014_1_.setDouble("PushX", this.pushX);
        p_70014_1_.setDouble("PushZ", this.pushZ);
        p_70014_1_.setShort("Fuel", (short) this.fuel);
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    protected void readEntityFromNBT(NBTTagCompound p_70037_1_) {
        super.readEntityFromNBT(p_70037_1_);
        this.pushX = p_70037_1_.getDouble("PushX");
        this.pushZ = p_70037_1_.getDouble("PushZ");
        this.fuel = p_70037_1_.getShort("Fuel");
    }

    protected boolean isMinecartPowered() {
        return (this.dataWatcher.getWatchableObjectByte(16) & 1) != 0;
    }

    protected void setMinecartPowered(boolean p_94107_1_) {
        if (p_94107_1_) {
            this.dataWatcher.updateObject(16, Byte.valueOf((byte) (this.dataWatcher.getWatchableObjectByte(16) | 1)));
        } else {
            this.dataWatcher.updateObject(16, Byte.valueOf((byte) (this.dataWatcher.getWatchableObjectByte(16) & -2)));
        }
    }

    public Block func_145817_o() {
        return Blocks.lit_furnace;
    }

    public int getDefaultDisplayTileData() {
        return 2;
    }
}
