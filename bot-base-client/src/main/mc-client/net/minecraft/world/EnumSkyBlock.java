package net.minecraft.world;

public enum EnumSkyBlock {
    Sky(15), Block(0);

    public final int defaultLightValue;

    private EnumSkyBlock(int p_i1961_3_) {
        this.defaultLightValue = p_i1961_3_;
    }
}
