package net.minecraft.world.gen.layer;

public class GenLayerAddIsland extends GenLayer {

    public GenLayerAddIsland(long p_i2119_1_, GenLayer p_i2119_3_) {
        super(p_i2119_1_);
        this.parent = p_i2119_3_;
    }

    /**
     * Returns a list of integer values generated by this layer. These may be
     * interpreted as temperatures, rainfall
     * amounts, or biomeList[] indices based on the particular GenLayer subclass.
     */
    public int[] getInts(int p_75904_1_, int p_75904_2_, int p_75904_3_, int p_75904_4_) {
        int var5 = p_75904_1_ - 1;
        int var6 = p_75904_2_ - 1;
        int var7 = p_75904_3_ + 2;
        int var8 = p_75904_4_ + 2;
        int[] var9 = this.parent.getInts(var5, var6, var7, var8);
        int[] var10 = IntCache.getIntCache(p_75904_3_ * p_75904_4_);

        for (int var11 = 0; var11 < p_75904_4_; ++var11) {
            for (int var12 = 0; var12 < p_75904_3_; ++var12) {
                int var13 = var9[var12 + 0 + (var11 + 0) * var7];
                int var14 = var9[var12 + 2 + (var11 + 0) * var7];
                int var15 = var9[var12 + 0 + (var11 + 2) * var7];
                int var16 = var9[var12 + 2 + (var11 + 2) * var7];
                int var17 = var9[var12 + 1 + (var11 + 1) * var7];
                this.initChunkSeed((long) (var12 + p_75904_1_), (long) (var11 + p_75904_2_));

                if (var17 == 0 && (var13 != 0 || var14 != 0 || var15 != 0 || var16 != 0)) {
                    int var18 = 1;
                    int var19 = 1;

                    if (var13 != 0 && this.nextInt(var18++) == 0) {
                        var19 = var13;
                    }

                    if (var14 != 0 && this.nextInt(var18++) == 0) {
                        var19 = var14;
                    }

                    if (var15 != 0 && this.nextInt(var18++) == 0) {
                        var19 = var15;
                    }

                    if (var16 != 0 && this.nextInt(var18++) == 0) {
                        var19 = var16;
                    }

                    if (this.nextInt(3) == 0) {
                        var10[var12 + var11 * p_75904_3_] = var19;
                    } else if (var19 == 4) {
                        var10[var12 + var11 * p_75904_3_] = 4;
                    } else {
                        var10[var12 + var11 * p_75904_3_] = 0;
                    }
                } else if (var17 > 0 && (var13 == 0 || var14 == 0 || var15 == 0 || var16 == 0)) {
                    if (this.nextInt(5) == 0) {
                        if (var17 == 4) {
                            var10[var12 + var11 * p_75904_3_] = 4;
                        } else {
                            var10[var12 + var11 * p_75904_3_] = 0;
                        }
                    } else {
                        var10[var12 + var11 * p_75904_3_] = var17;
                    }
                } else {
                    var10[var12 + var11 * p_75904_3_] = var17;
                }
            }
        }

        return var10;
    }
}
