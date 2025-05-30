package net.minecraft.client.util;

import java.util.Comparator;

public class QuadComparator implements Comparator {
    private float field_147630_a;
    private float field_147628_b;
    private float field_147629_c;
    private int[] field_147627_d;

    public QuadComparator(int[] p_i45077_1_, float p_i45077_2_, float p_i45077_3_, float p_i45077_4_) {
        this.field_147627_d = p_i45077_1_;
        this.field_147630_a = p_i45077_2_;
        this.field_147628_b = p_i45077_3_;
        this.field_147629_c = p_i45077_4_;
    }

    public int compare(Integer p_compare_1_, Integer p_compare_2_) {
        float var3 = Float.intBitsToFloat(this.field_147627_d[p_compare_1_.intValue()]) - this.field_147630_a;
        float var4 = Float.intBitsToFloat(this.field_147627_d[p_compare_1_.intValue() + 1]) - this.field_147628_b;
        float var5 = Float.intBitsToFloat(this.field_147627_d[p_compare_1_.intValue() + 2]) - this.field_147629_c;
        float var6 = Float.intBitsToFloat(this.field_147627_d[p_compare_1_.intValue() + 8]) - this.field_147630_a;
        float var7 = Float.intBitsToFloat(this.field_147627_d[p_compare_1_.intValue() + 9]) - this.field_147628_b;
        float var8 = Float.intBitsToFloat(this.field_147627_d[p_compare_1_.intValue() + 10]) - this.field_147629_c;
        float var9 = Float.intBitsToFloat(this.field_147627_d[p_compare_1_.intValue() + 16]) - this.field_147630_a;
        float var10 = Float.intBitsToFloat(this.field_147627_d[p_compare_1_.intValue() + 17]) - this.field_147628_b;
        float var11 = Float.intBitsToFloat(this.field_147627_d[p_compare_1_.intValue() + 18]) - this.field_147629_c;
        float var12 = Float.intBitsToFloat(this.field_147627_d[p_compare_1_.intValue() + 24]) - this.field_147630_a;
        float var13 = Float.intBitsToFloat(this.field_147627_d[p_compare_1_.intValue() + 25]) - this.field_147628_b;
        float var14 = Float.intBitsToFloat(this.field_147627_d[p_compare_1_.intValue() + 26]) - this.field_147629_c;
        float var15 = Float.intBitsToFloat(this.field_147627_d[p_compare_2_.intValue()]) - this.field_147630_a;
        float var16 = Float.intBitsToFloat(this.field_147627_d[p_compare_2_.intValue() + 1]) - this.field_147628_b;
        float var17 = Float.intBitsToFloat(this.field_147627_d[p_compare_2_.intValue() + 2]) - this.field_147629_c;
        float var18 = Float.intBitsToFloat(this.field_147627_d[p_compare_2_.intValue() + 8]) - this.field_147630_a;
        float var19 = Float.intBitsToFloat(this.field_147627_d[p_compare_2_.intValue() + 9]) - this.field_147628_b;
        float var20 = Float.intBitsToFloat(this.field_147627_d[p_compare_2_.intValue() + 10]) - this.field_147629_c;
        float var21 = Float.intBitsToFloat(this.field_147627_d[p_compare_2_.intValue() + 16]) - this.field_147630_a;
        float var22 = Float.intBitsToFloat(this.field_147627_d[p_compare_2_.intValue() + 17]) - this.field_147628_b;
        float var23 = Float.intBitsToFloat(this.field_147627_d[p_compare_2_.intValue() + 18]) - this.field_147629_c;
        float var24 = Float.intBitsToFloat(this.field_147627_d[p_compare_2_.intValue() + 24]) - this.field_147630_a;
        float var25 = Float.intBitsToFloat(this.field_147627_d[p_compare_2_.intValue() + 25]) - this.field_147628_b;
        float var26 = Float.intBitsToFloat(this.field_147627_d[p_compare_2_.intValue() + 26]) - this.field_147629_c;
        float var27 = (var3 + var6 + var9 + var12) * 0.25F;
        float var28 = (var4 + var7 + var10 + var13) * 0.25F;
        float var29 = (var5 + var8 + var11 + var14) * 0.25F;
        float var30 = (var15 + var18 + var21 + var24) * 0.25F;
        float var31 = (var16 + var19 + var22 + var25) * 0.25F;
        float var32 = (var17 + var20 + var23 + var26) * 0.25F;
        float var33 = var27 * var27 + var28 * var28 + var29 * var29;
        float var34 = var30 * var30 + var31 * var31 + var32 * var32;
        return Float.compare(var34, var33);
    }

    public int compare(Object p_compare_1_, Object p_compare_2_) {
        return this.compare((Integer) p_compare_1_, (Integer) p_compare_2_);
    }
}
