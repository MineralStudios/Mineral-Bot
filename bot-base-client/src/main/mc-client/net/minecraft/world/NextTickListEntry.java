package net.minecraft.world;

import net.minecraft.block.Block;
import org.jetbrains.annotations.NotNull;

public class NextTickListEntry implements Comparable {
    /**
     * The id number for the next tick entry
     */
    private static long nextTickEntryID;
    private final Block field_151352_g;

    /**
     * X position this tick is occuring at
     */
    public int xCoord;

    /**
     * Y position this tick is occuring at
     */
    public int yCoord;

    /**
     * Z position this tick is occuring at
     */
    public int zCoord;

    /**
     * Time this tick is scheduled to occur at
     */
    public long scheduledTime;
    public int priority;

    /**
     * The id of the tick entry
     */
    private long tickEntryID;

    public NextTickListEntry(int p_i45370_1_, int p_i45370_2_, int p_i45370_3_, Block p_i45370_4_) {
        this.tickEntryID = (long) (nextTickEntryID++);
        this.xCoord = p_i45370_1_;
        this.yCoord = p_i45370_2_;
        this.zCoord = p_i45370_3_;
        this.field_151352_g = p_i45370_4_;
    }

    public boolean equals(Object p_equals_1_) {
        if (!(p_equals_1_ instanceof NextTickListEntry)) {
            return false;
        } else {
            NextTickListEntry var2 = (NextTickListEntry) p_equals_1_;
            return this.xCoord == var2.xCoord && this.yCoord == var2.yCoord && this.zCoord == var2.zCoord
                    && Block.isEqualTo(this.field_151352_g, var2.field_151352_g);
        }
    }

    public int hashCode() {
        return (this.xCoord * 1024 * 1024 + this.zCoord * 1024 + this.yCoord) * 256;
    }

    /**
     * Sets the scheduled time for this tick entry
     */
    public NextTickListEntry setScheduledTime(long p_77176_1_) {
        this.scheduledTime = p_77176_1_;
        return this;
    }

    public void setPriority(int p_82753_1_) {
        this.priority = p_82753_1_;
    }

    public int compareTo(NextTickListEntry p_compareTo_1_) {
        return this.scheduledTime < p_compareTo_1_.scheduledTime ? -1
                : (this.scheduledTime > p_compareTo_1_.scheduledTime ? 1
                : (this.priority != p_compareTo_1_.priority ? this.priority - p_compareTo_1_.priority
                : (this.tickEntryID < p_compareTo_1_.tickEntryID ? -1
                : (this.tickEntryID > p_compareTo_1_.tickEntryID ? 1 : 0))));
    }

    public String toString() {
        return Block.getIdFromBlock(this.field_151352_g) + ": (" + this.xCoord + ", " + this.yCoord + ", " + this.zCoord
                + "), " + this.scheduledTime + ", " + this.priority + ", " + this.tickEntryID;
    }

    public Block func_151351_a() {
        return this.field_151352_g;
    }

    public int compareTo(@NotNull Object p_compareTo_1_) {
        return this.compareTo((NextTickListEntry) p_compareTo_1_);
    }
}
