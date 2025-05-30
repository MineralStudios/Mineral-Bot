package net.minecraft.entity.monster;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;

public class EntityMagmaCube extends EntitySlime {

    public EntityMagmaCube(World p_i1737_1_) {
        super(p_i1737_1_);
        this.isImmuneToFire = true;
    }

    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.20000000298023224D);
    }

    /**
     * Checks if the entity's current position is a valid location to spawn this
     * entity.
     */
    public boolean getCanSpawnHere() {
        return this.worldObj.difficultySetting != EnumDifficulty.PEACEFUL
                && this.worldObj.checkNoEntityCollision(this.boundingBox)
                && this.worldObj.getCollidingBoundingBoxes(this, this.boundingBox).isEmpty()
                && !this.worldObj.isAnyLiquid(this.boundingBox);
    }

    /**
     * Returns the current armor value as determined by a call to
     * InventoryPlayer.getTotalArmorValue
     */
    public int getTotalArmorValue() {
        return this.getSlimeSize() * 3;
    }

    public int getBrightnessForRender(float p_70070_1_) {
        return 15728880;
    }

    /**
     * Gets how bright this entity is.
     */
    public float getBrightness(float p_70013_1_) {
        return 1.0F;
    }

    /**
     * Returns the name of a particle effect that may be randomly created by
     * EntitySlime.onUpdate()
     */
    protected String getSlimeParticle() {
        return "flame";
    }

    protected EntitySlime createInstance() {
        return new EntityMagmaCube(this.worldObj);
    }

    protected Item func_146068_u() {
        return Items.magma_cream;
    }

    /**
     * Drop 0-2 items of this living's type
     */
    protected void dropFewItems(boolean p_70628_1_, int p_70628_2_) {
        Item var3 = this.func_146068_u();

        if (var3 != null && this.getSlimeSize() > 1) {
            int var4 = this.rand.nextInt(4) - 2;

            if (p_70628_2_ > 0) {
                var4 += this.rand.nextInt(p_70628_2_ + 1);
            }

            for (int var5 = 0; var5 < var4; ++var5) {
                this.func_145779_a(var3, 1);
            }
        }
    }

    /**
     * Returns true if the entity is on fire. Used by render to add the fire effect
     * on rendering.
     */
    public boolean isBurning() {
        return false;
    }

    /**
     * Gets the amount of time the slime needs to wait between jumps.
     */
    protected int getJumpDelay() {
        return super.getJumpDelay() * 4;
    }

    protected void alterSquishAmount() {
        this.squishAmount *= 0.9F;
    }

    /**
     * Causes this entity to do an upwards motion (jumping).
     */
    protected void jump() {
        this.motionY = (double) (0.42F + (float) this.getSlimeSize() * 0.1F);
        this.isAirBorne = true;
    }

    /**
     * Called when the mob is falling. Calculates and applies fall damage.
     */
    protected void fall(float p_70069_1_) {
    }

    /**
     * Indicates weather the slime is able to damage the player (based upon the
     * slime's size)
     */
    protected boolean canDamagePlayer() {
        return true;
    }

    /**
     * Gets the amount of damage dealt to the player when "attacked" by the slime.
     */
    protected int getAttackStrength() {
        return super.getAttackStrength() + 2;
    }

    /**
     * Returns the name of the sound played when the slime jumps.
     */
    protected String getJumpSound() {
        return this.getSlimeSize() > 1 ? "mob.magmacube.big" : "mob.magmacube.small";
    }

    /**
     * Whether or not the current entity is in lava
     */
    public boolean handleLavaMovement() {
        return false;
    }

    /**
     * Returns true if the slime makes a sound when it lands after a jump (based
     * upon the slime's size)
     */
    protected boolean makesSoundOnLand() {
        return true;
    }
}
