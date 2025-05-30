package net.minecraft.entity.ai;

import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.pathfinding.PathEntity;
import net.minecraft.pathfinding.PathPoint;
import net.minecraft.util.MathHelper;
import org.apache.commons.lang3.StringUtils;

public abstract class EntityAITarget extends EntityAIBase {
    /** The entity that this task belongs to */
    protected EntityCreature taskOwner;

    /**
     * If true, EntityAI targets must be able to be seen (cannot be blocked by
     * walls) to be suitable targets.
     */
    protected boolean shouldCheckSight;

    /**
     * When true, only entities that can be reached with minimal effort will be
     * targetted.
     */
    private boolean nearbyOnly;

    /**
     * When nearbyOnly is true: 0 -> No target, but OK to search; 1 -> Nearby target
     * found; 2 -> Target too far.
     */
    private int targetSearchStatus;

    /**
     * When nearbyOnly is true, this throttles target searching to avoid excessive
     * pathfinding.
     */
    private int targetSearchDelay;
    private int field_75298_g;

    public EntityAITarget(EntityCreature p_i1669_1_, boolean p_i1669_2_) {
        this(p_i1669_1_, p_i1669_2_, false);
    }

    public EntityAITarget(EntityCreature p_i1670_1_, boolean p_i1670_2_, boolean p_i1670_3_) {
        this.taskOwner = p_i1670_1_;
        this.shouldCheckSight = p_i1670_2_;
        this.nearbyOnly = p_i1670_3_;
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    public boolean continueExecuting() {
        EntityLivingBase var1 = this.taskOwner.getAttackTarget();

        if (var1 == null) {
            return false;
        } else if (!var1.isEntityAlive()) {
            return false;
        } else {
            double var2 = this.getTargetDistance();

            if (this.taskOwner.getDistanceSqToEntity(var1) > var2 * var2) {
                return false;
            } else {
                if (this.shouldCheckSight) {
                    if (this.taskOwner.getEntitySenses().canSee(var1)) {
                        this.field_75298_g = 0;
                    } else if (++this.field_75298_g > 60) {
                        return false;
                    }
                }

                return !(var1 instanceof EntityPlayerMP) || !((EntityPlayerMP) var1).theItemInWorldManager.isCreative();
            }
        }
    }

    protected double getTargetDistance() {
        IAttributeInstance var1 = this.taskOwner.getEntityAttribute(SharedMonsterAttributes.followRange);
        return var1 == null ? 16.0D : var1.getAttributeValue();
    }

    /**
     * Execute a one shot task or start executing a continuous task
     */
    public void startExecuting() {
        this.targetSearchStatus = 0;
        this.targetSearchDelay = 0;
        this.field_75298_g = 0;
    }

    /**
     * Resets the task
     */
    public void resetTask() {
        this.taskOwner.setAttackTarget((EntityLivingBase) null);
    }

    /**
     * A method used to see if an entity is a suitable target through a number of
     * checks.
     */
    protected boolean isSuitableTarget(EntityLivingBase p_75296_1_, boolean p_75296_2_) {
        if (p_75296_1_ == null) {
            return false;
        } else if (p_75296_1_ == this.taskOwner) {
            return false;
        } else if (!p_75296_1_.isEntityAlive()) {
            return false;
        } else if (!this.taskOwner.canAttackClass(p_75296_1_.getClass())) {
            return false;
        } else {
            if (this.taskOwner instanceof IEntityOwnable
                    && StringUtils.isNotEmpty(((IEntityOwnable) this.taskOwner).func_152113_b())) {
                if (p_75296_1_ instanceof IEntityOwnable && ((IEntityOwnable) this.taskOwner).func_152113_b()
                        .equals(((IEntityOwnable) p_75296_1_).func_152113_b())) {
                    return false;
                }

                if (p_75296_1_ == ((IEntityOwnable) this.taskOwner).getOwner()) {
                    return false;
                }
            } else if (p_75296_1_ instanceof EntityPlayer && !p_75296_2_
                    && ((EntityPlayer) p_75296_1_).capabilities.disableDamage) {
                return false;
            }

            if (!this.taskOwner.isWithinHomeDistance(MathHelper.floor_double(p_75296_1_.posX),
                    MathHelper.floor_double(p_75296_1_.posY), MathHelper.floor_double(p_75296_1_.posZ))) {
                return false;
            } else if (this.shouldCheckSight && !this.taskOwner.getEntitySenses().canSee(p_75296_1_)) {
                return false;
            } else {
                if (this.nearbyOnly) {
                    if (--this.targetSearchDelay <= 0) {
                        this.targetSearchStatus = 0;
                    }

                    if (this.targetSearchStatus == 0) {
                        this.targetSearchStatus = this.canEasilyReach(p_75296_1_) ? 1 : 2;
                    }

                    if (this.targetSearchStatus == 2) {
                        return false;
                    }
                }

                return true;
            }
        }
    }

    /**
     * Checks to see if this entity can find a short path to the given target.
     */
    private boolean canEasilyReach(EntityLivingBase p_75295_1_) {
        this.targetSearchDelay = 10 + this.taskOwner.getRNG().nextInt(5);
        PathEntity var2 = this.taskOwner.getNavigator().getPathToEntityLiving(p_75295_1_);

        if (var2 == null) {
            return false;
        } else {
            PathPoint var3 = var2.getFinalPathPoint();

            if (var3 == null) {
                return false;
            } else {
                int var4 = var3.xCoord - MathHelper.floor_double(p_75295_1_.posX);
                int var5 = var3.zCoord - MathHelper.floor_double(p_75295_1_.posZ);
                return (double) (var4 * var4 + var5 * var5) <= 2.25D;
            }
        }
    }
}
