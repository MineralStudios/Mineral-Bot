package net.minecraft.world.gen.layer;

import net.minecraft.world.biome.BiomeGenBase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GenLayerHills extends GenLayer {
    private static final Logger logger = LogManager.getLogger(GenLayerHills.class);
    private GenLayer field_151628_d;

    public GenLayerHills(long p_i45479_1_, GenLayer p_i45479_3_, GenLayer p_i45479_4_) {
        super(p_i45479_1_);
        this.parent = p_i45479_3_;
        this.field_151628_d = p_i45479_4_;
    }

    /**
     * Returns a list of integer values generated by this layer. These may be
     * interpreted as temperatures, rainfall
     * amounts, or biomeList[] indices based on the particular GenLayer subclass.
     */
    public int[] getInts(int p_75904_1_, int p_75904_2_, int p_75904_3_, int p_75904_4_) {
        int[] var5 = this.parent.getInts(p_75904_1_ - 1, p_75904_2_ - 1, p_75904_3_ + 2, p_75904_4_ + 2);
        int[] var6 = this.field_151628_d.getInts(p_75904_1_ - 1, p_75904_2_ - 1, p_75904_3_ + 2, p_75904_4_ + 2);
        int[] var7 = IntCache.getIntCache(p_75904_3_ * p_75904_4_);

        for (int var8 = 0; var8 < p_75904_4_; ++var8) {
            for (int var9 = 0; var9 < p_75904_3_; ++var9) {
                this.initChunkSeed((long) (var9 + p_75904_1_), (long) (var8 + p_75904_2_));
                int var10 = var5[var9 + 1 + (var8 + 1) * (p_75904_3_ + 2)];
                int var11 = var6[var9 + 1 + (var8 + 1) * (p_75904_3_ + 2)];
                boolean var12 = (var11 - 2) % 29 == 0;

                if (var10 > 255) {
                    logger.debug("old! " + var10);
                }

                if (var10 != 0 && var11 >= 2 && (var11 - 2) % 29 == 1 && var10 < 128) {
                    if (BiomeGenBase.func_150568_d(var10 + 128) != null) {
                        var7[var9 + var8 * p_75904_3_] = var10 + 128;
                    } else {
                        var7[var9 + var8 * p_75904_3_] = var10;
                    }
                } else if (this.nextInt(3) != 0 && !var12) {
                    var7[var9 + var8 * p_75904_3_] = var10;
                } else {
                    int var13 = var10;
                    int var14;

                    if (var10 == BiomeGenBase.desert.biomeID) {
                        var13 = BiomeGenBase.desertHills.biomeID;
                    } else if (var10 == BiomeGenBase.forest.biomeID) {
                        var13 = BiomeGenBase.forestHills.biomeID;
                    } else if (var10 == BiomeGenBase.field_150583_P.biomeID) {
                        var13 = BiomeGenBase.field_150582_Q.biomeID;
                    } else if (var10 == BiomeGenBase.field_150585_R.biomeID) {
                        var13 = BiomeGenBase.plains.biomeID;
                    } else if (var10 == BiomeGenBase.taiga.biomeID) {
                        var13 = BiomeGenBase.taigaHills.biomeID;
                    } else if (var10 == BiomeGenBase.field_150578_U.biomeID) {
                        var13 = BiomeGenBase.field_150581_V.biomeID;
                    } else if (var10 == BiomeGenBase.field_150584_S.biomeID) {
                        var13 = BiomeGenBase.field_150579_T.biomeID;
                    } else if (var10 == BiomeGenBase.plains.biomeID) {
                        if (this.nextInt(3) == 0) {
                            var13 = BiomeGenBase.forestHills.biomeID;
                        } else {
                            var13 = BiomeGenBase.forest.biomeID;
                        }
                    } else if (var10 == BiomeGenBase.icePlains.biomeID) {
                        var13 = BiomeGenBase.iceMountains.biomeID;
                    } else if (var10 == BiomeGenBase.jungle.biomeID) {
                        var13 = BiomeGenBase.jungleHills.biomeID;
                    } else if (var10 == BiomeGenBase.ocean.biomeID) {
                        var13 = BiomeGenBase.field_150575_M.biomeID;
                    } else if (var10 == BiomeGenBase.extremeHills.biomeID) {
                        var13 = BiomeGenBase.field_150580_W.biomeID;
                    } else if (var10 == BiomeGenBase.field_150588_X.biomeID) {
                        var13 = BiomeGenBase.field_150587_Y.biomeID;
                    } else if (func_151616_a(var10, BiomeGenBase.field_150607_aa.biomeID)) {
                        var13 = BiomeGenBase.field_150589_Z.biomeID;
                    } else if (var10 == BiomeGenBase.field_150575_M.biomeID && this.nextInt(3) == 0) {
                        var14 = this.nextInt(2);

                        if (var14 == 0) {
                            var13 = BiomeGenBase.plains.biomeID;
                        } else {
                            var13 = BiomeGenBase.forest.biomeID;
                        }
                    }

                    if (var12 && var13 != var10) {
                        if (BiomeGenBase.func_150568_d(var13 + 128) != null) {
                            var13 += 128;
                        } else {
                            var13 = var10;
                        }
                    }

                    if (var13 == var10) {
                        var7[var9 + var8 * p_75904_3_] = var10;
                    } else {
                        var14 = var5[var9 + 1 + (var8 + 1 - 1) * (p_75904_3_ + 2)];
                        int var15 = var5[var9 + 1 + 1 + (var8 + 1) * (p_75904_3_ + 2)];
                        int var16 = var5[var9 + 1 - 1 + (var8 + 1) * (p_75904_3_ + 2)];
                        int var17 = var5[var9 + 1 + (var8 + 1 + 1) * (p_75904_3_ + 2)];
                        int var18 = 0;

                        if (func_151616_a(var14, var10)) {
                            ++var18;
                        }

                        if (func_151616_a(var15, var10)) {
                            ++var18;
                        }

                        if (func_151616_a(var16, var10)) {
                            ++var18;
                        }

                        if (func_151616_a(var17, var10)) {
                            ++var18;
                        }

                        if (var18 >= 3) {
                            var7[var9 + var8 * p_75904_3_] = var13;
                        } else {
                            var7[var9 + var8 * p_75904_3_] = var10;
                        }
                    }
                }
            }
        }

        return var7;
    }
}
