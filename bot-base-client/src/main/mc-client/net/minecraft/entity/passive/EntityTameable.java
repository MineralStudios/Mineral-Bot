package net.minecraft.entity.passive;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.ai.EntityAISit;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.management.PreYggdrasilConverter;
import net.minecraft.world.World;

import java.util.UUID;

public abstract class EntityTameable extends EntityAnimal implements IEntityOwnable {
    protected EntityAISit aiSit = new EntityAISit(this);

    public EntityTameable(World p_i1604_1_) {
        super(p_i1604_1_);
    }

    protected void entityInit() {
        super.entityInit();
        this.dataWatcher.addObject(16, Byte.valueOf((byte) 0));
        this.dataWatcher.addObject(17, "");
    }

    /**
     * (abstract) Protected helper method to write subclass entity data to NBT.
     */
    public void writeEntityToNBT(NBTTagCompound p_70014_1_) {
        super.writeEntityToNBT(p_70014_1_);

        if (this.func_152113_b() == null) {
            p_70014_1_.setString("OwnerUUID", "");
        } else {
            p_70014_1_.setString("OwnerUUID", this.func_152113_b());
        }

        p_70014_1_.setBoolean("Sitting", this.isSitting());
    }

    /**
     * (abstract) Protected helper method to read subclass entity data from NBT.
     */
    public void readEntityFromNBT(NBTTagCompound p_70037_1_) {
        super.readEntityFromNBT(p_70037_1_);
        String var2 = "";

        if (p_70037_1_.func_150297_b("OwnerUUID", 8)) {
            var2 = p_70037_1_.getString("OwnerUUID");
        } else {
            String var3 = p_70037_1_.getString("Owner");
            var2 = PreYggdrasilConverter.func_152719_a(var3);
        }

        if (var2.length() > 0) {
            this.func_152115_b(var2);
            this.setTamed(true);
        }

        this.aiSit.setSitting(p_70037_1_.getBoolean("Sitting"));
        this.setSitting(p_70037_1_.getBoolean("Sitting"));
    }

    /**
     * Play the taming effect, will either be hearts or smoke depending on status
     */
    protected void playTameEffect(boolean p_70908_1_) {
        String var2 = "heart";

        if (!p_70908_1_) {
            var2 = "smoke";
        }

        for (int var3 = 0; var3 < 7; ++var3) {
            double var4 = this.rand.nextGaussian() * 0.02D;
            double var6 = this.rand.nextGaussian() * 0.02D;
            double var8 = this.rand.nextGaussian() * 0.02D;
            this.worldObj.spawnParticle(var2,
                    this.posX + (double) (this.rand.nextFloat() * this.width * 2.0F) - (double) this.width,
                    this.posY + 0.5D + (double) (this.rand.nextFloat() * this.height),
                    this.posZ + (double) (this.rand.nextFloat() * this.width * 2.0F) - (double) this.width, var4, var6,
                    var8);
        }
    }

    public void handleHealthUpdate(byte p_70103_1_) {
        if (p_70103_1_ == 7) {
            this.playTameEffect(true);
        } else if (p_70103_1_ == 6) {
            this.playTameEffect(false);
        } else {
            super.handleHealthUpdate(p_70103_1_);
        }
    }

    public boolean isTamed() {
        return (this.dataWatcher.getWatchableObjectByte(16) & 4) != 0;
    }

    public void setTamed(boolean p_70903_1_) {
        byte var2 = this.dataWatcher.getWatchableObjectByte(16);

        if (p_70903_1_) {
            this.dataWatcher.updateObject(16, Byte.valueOf((byte) (var2 | 4)));
        } else {
            this.dataWatcher.updateObject(16, Byte.valueOf((byte) (var2 & -5)));
        }
    }

    public boolean isSitting() {
        return (this.dataWatcher.getWatchableObjectByte(16) & 1) != 0;
    }

    public void setSitting(boolean p_70904_1_) {
        byte var2 = this.dataWatcher.getWatchableObjectByte(16);

        if (p_70904_1_) {
            this.dataWatcher.updateObject(16, Byte.valueOf((byte) (var2 | 1)));
        } else {
            this.dataWatcher.updateObject(16, Byte.valueOf((byte) (var2 & -2)));
        }
    }

    public String func_152113_b() {
        return this.dataWatcher.getWatchableObjectString(17);
    }

    public void func_152115_b(String p_152115_1_) {
        this.dataWatcher.updateObject(17, p_152115_1_);
    }

    public EntityLivingBase getOwner() {
        try {
            UUID var1 = UUID.fromString(this.func_152113_b());
            return var1 == null ? null : this.worldObj.func_152378_a(var1);
        } catch (IllegalArgumentException var2) {
            return null;
        }
    }

    public boolean func_152114_e(EntityLivingBase p_152114_1_) {
        return p_152114_1_ == this.getOwner();
    }

    public EntityAISit func_70907_r() {
        return this.aiSit;
    }

    public boolean func_142018_a(EntityLivingBase p_142018_1_, EntityLivingBase p_142018_2_) {
        return true;
    }

    public Team getTeam() {
        if (this.isTamed()) {
            EntityLivingBase var1 = this.getOwner();

            if (var1 != null) {
                return var1.getTeam();
            }
        }

        return super.getTeam();
    }

    public boolean isOnSameTeam(EntityLivingBase p_142014_1_) {
        if (this.isTamed()) {
            EntityLivingBase var2 = this.getOwner();

            if (p_142014_1_ == var2) {
                return true;
            }

            if (var2 != null) {
                return var2.isOnSameTeam(p_142014_1_);
            }
        }

        return super.isOnSameTeam(p_142014_1_);
    }
}
