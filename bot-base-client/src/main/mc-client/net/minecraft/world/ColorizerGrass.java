package net.minecraft.world;

public class ColorizerGrass {
    /** Color buffer for grass */
    private static int[] grassBuffer = new int[65536];

    public static void setGrassBiomeColorizer(int[] p_77479_0_) {
        grassBuffer = p_77479_0_;
    }

    /**
     * Gets grass color from temperature and humidity. Args: temperature, humidity
     */
    public static int getGrassColor(double p_77480_0_, double p_77480_2_) {
        p_77480_2_ *= p_77480_0_;
        int var4 = (int) ((1.0D - p_77480_0_) * 255.0D);
        int var5 = (int) ((1.0D - p_77480_2_) * 255.0D);
        return grassBuffer[var5 << 8 | var4];
    }
}
