package net.minecraft.world.chunk.storage;

public class NibbleArrayReader {
    public final byte[] data;
    private final int depthBits;
    private final int depthBitsPlusFour;

    public NibbleArrayReader(byte[] p_i1998_1_, int p_i1998_2_) {
        this.data = p_i1998_1_;
        this.depthBits = p_i1998_2_;
        this.depthBitsPlusFour = p_i1998_2_ + 4;
    }

    public int get(int p_76686_1_, int p_76686_2_, int p_76686_3_) {
        int var4 = p_76686_1_ << this.depthBitsPlusFour | p_76686_3_ << this.depthBits | p_76686_2_;
        int var5 = var4 >> 1;
        int var6 = var4 & 1;
        return var6 == 0 ? this.data[var5] & 15 : this.data[var5] >> 4 & 15;
    }
}
