package net.minecraft.util;

import org.jetbrains.annotations.NotNull;

public class ChunkCoordinates implements Comparable {
    public int posX;

    /**
     * the y coordinate
     */
    public int posY;

    /**
     * the z coordinate
     */
    public int posZ;

    public ChunkCoordinates() {
    }

    public ChunkCoordinates(int p_i1354_1_, int p_i1354_2_, int p_i1354_3_) {
        this.posX = p_i1354_1_;
        this.posY = p_i1354_2_;
        this.posZ = p_i1354_3_;
    }

    public ChunkCoordinates(ChunkCoordinates p_i1355_1_) {
        this.posX = p_i1355_1_.posX;
        this.posY = p_i1355_1_.posY;
        this.posZ = p_i1355_1_.posZ;
    }

    public boolean equals(Object p_equals_1_) {
        if (!(p_equals_1_ instanceof ChunkCoordinates)) {
            return false;
        } else {
            ChunkCoordinates var2 = (ChunkCoordinates) p_equals_1_;
            return this.posX == var2.posX && this.posY == var2.posY && this.posZ == var2.posZ;
        }
    }

    public int hashCode() {
        return this.posX + this.posZ << 8 + this.posY << 16;
    }

    public int compareTo(ChunkCoordinates p_compareTo_1_) {
        return this.posY == p_compareTo_1_.posY
                ? (this.posZ == p_compareTo_1_.posZ ? this.posX - p_compareTo_1_.posX : this.posZ - p_compareTo_1_.posZ)
                : this.posY - p_compareTo_1_.posY;
    }

    public void set(int p_71571_1_, int p_71571_2_, int p_71571_3_) {
        this.posX = p_71571_1_;
        this.posY = p_71571_2_;
        this.posZ = p_71571_3_;
    }

    /**
     * Returns the squared distance between this coordinates and the coordinates
     * given as argument.
     */
    public float getDistanceSquared(int p_71569_1_, int p_71569_2_, int p_71569_3_) {
        float var4 = (float) (this.posX - p_71569_1_);
        float var5 = (float) (this.posY - p_71569_2_);
        float var6 = (float) (this.posZ - p_71569_3_);
        return var4 * var4 + var5 * var5 + var6 * var6;
    }

    /**
     * Return the squared distance between this coordinates and the ChunkCoordinates
     * given as argument.
     */
    public float getDistanceSquaredToChunkCoordinates(ChunkCoordinates p_82371_1_) {
        return this.getDistanceSquared(p_82371_1_.posX, p_82371_1_.posY, p_82371_1_.posZ);
    }

    public String toString() {
        return "Pos{x=" + this.posX + ", y=" + this.posY + ", z=" + this.posZ + '}';
    }

    public int compareTo(@NotNull Object p_compareTo_1_) {
        return this.compareTo((ChunkCoordinates) p_compareTo_1_);
    }
}
