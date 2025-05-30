package net.minecraft.entity.ai;

import java.util.Random;
import net.minecraft.entity.EntityCreature;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

public class RandomPositionGenerator {
    /**
     * used to store a driection when the user passes a point to move towards or
     * away from. WARNING: NEVER THREAD SAFE.
     * MULTIPLE findTowards and findAway calls, will share this var
     */
    private static Vec3 staticVector = Vec3.createVectorHelper(0.0D, 0.0D, 0.0D);

    /**
     * finds a random target within par1(x,z) and par2 (y) blocks
     */
    public static Vec3 findRandomTarget(EntityCreature p_75463_0_, int p_75463_1_, int p_75463_2_) {
        return findRandomTargetBlock(p_75463_0_, p_75463_1_, p_75463_2_, (Vec3) null);
    }

    /**
     * finds a random target within par1(x,z) and par2 (y) blocks in the direction
     * of the point par3
     */
    public static Vec3 findRandomTargetBlockTowards(EntityCreature p_75464_0_, int p_75464_1_, int p_75464_2_,
            Vec3 p_75464_3_) {
        staticVector.xCoord = p_75464_3_.xCoord - p_75464_0_.posX;
        staticVector.yCoord = p_75464_3_.yCoord - p_75464_0_.posY;
        staticVector.zCoord = p_75464_3_.zCoord - p_75464_0_.posZ;
        return findRandomTargetBlock(p_75464_0_, p_75464_1_, p_75464_2_, staticVector);
    }

    /**
     * finds a random target within par1(x,z) and par2 (y) blocks in the reverse
     * direction of the point par3
     */
    public static Vec3 findRandomTargetBlockAwayFrom(EntityCreature p_75461_0_, int p_75461_1_, int p_75461_2_,
            Vec3 p_75461_3_) {
        staticVector.xCoord = p_75461_0_.posX - p_75461_3_.xCoord;
        staticVector.yCoord = p_75461_0_.posY - p_75461_3_.yCoord;
        staticVector.zCoord = p_75461_0_.posZ - p_75461_3_.zCoord;
        return findRandomTargetBlock(p_75461_0_, p_75461_1_, p_75461_2_, staticVector);
    }

    /**
     * searches 10 blocks at random in a within par1(x,z) and par2 (y) distance,
     * ignores those not in the direction of
     * par3Vec3, then points to the tile for which creature.getBlockPathWeight
     * returns the highest number
     */
    private static Vec3 findRandomTargetBlock(EntityCreature p_75462_0_, int p_75462_1_, int p_75462_2_,
            Vec3 p_75462_3_) {
        Random var4 = p_75462_0_.getRNG();
        boolean var5 = false;
        int var6 = 0;
        int var7 = 0;
        int var8 = 0;
        float var9 = -99999.0F;
        boolean var10;

        if (p_75462_0_.hasHome()) {
            double var11 = (double) (p_75462_0_.getHomePosition().getDistanceSquared(
                    MathHelper.floor_double(p_75462_0_.posX), MathHelper.floor_double(p_75462_0_.posY),
                    MathHelper.floor_double(p_75462_0_.posZ)) + 4.0F);
            double var13 = (double) (p_75462_0_.func_110174_bM() + (float) p_75462_1_);
            var10 = var11 < var13 * var13;
        } else {
            var10 = false;
        }

        for (int var16 = 0; var16 < 10; ++var16) {
            int var12 = var4.nextInt(2 * p_75462_1_) - p_75462_1_;
            int var17 = var4.nextInt(2 * p_75462_2_) - p_75462_2_;
            int var14 = var4.nextInt(2 * p_75462_1_) - p_75462_1_;

            if (p_75462_3_ == null || (double) var12 * p_75462_3_.xCoord + (double) var14 * p_75462_3_.zCoord >= 0.0D) {
                var12 += MathHelper.floor_double(p_75462_0_.posX);
                var17 += MathHelper.floor_double(p_75462_0_.posY);
                var14 += MathHelper.floor_double(p_75462_0_.posZ);

                if (!var10 || p_75462_0_.isWithinHomeDistance(var12, var17, var14)) {
                    float var15 = p_75462_0_.getBlockPathWeight(var12, var17, var14);

                    if (var15 > var9) {
                        var9 = var15;
                        var6 = var12;
                        var7 = var17;
                        var8 = var14;
                        var5 = true;
                    }
                }
            }
        }

        if (var5) {
            return Vec3.createVectorHelper((double) var6, (double) var7, (double) var8);
        } else {
            return null;
        }
    }
}
