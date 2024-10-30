package net.minecraft.world;

import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

public class ChunkPosition {
    public final int x;
    public final int y;
    public final int z;

    public ChunkPosition(int p_i45363_1_, int p_i45363_2_, int p_i45363_3_) {
        this.x = p_i45363_1_;
        this.y = p_i45363_2_;
        this.z = p_i45363_3_;
    }

    public ChunkPosition(Vec3 p_i45364_1_) {
        this(MathHelper.floor_double(p_i45364_1_.xCoord), MathHelper.floor_double(p_i45364_1_.yCoord),
                MathHelper.floor_double(p_i45364_1_.zCoord));
    }

    public boolean equals(Object p_equals_1_) {
        if (!(p_equals_1_ instanceof ChunkPosition)) {
            return false;
        } else {
            ChunkPosition var2 = (ChunkPosition) p_equals_1_;
            return var2.x == this.x && var2.y == this.y
                    && var2.z == this.z;
        }
    }

    public int hashCode() {
        return this.x * 8976890 + this.y * 981131 + this.z;
    }
}
