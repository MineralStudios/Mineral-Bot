package net.minecraft.nbt;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.util.ReportedException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class NBTTagCompound extends NBTBase {
    private static final Logger logger = LogManager.getLogger(NBTTagCompound.class);

    /**
     * The key-value pairs for the tag. Each key is a UTF string, each value is a
     * tag.
     */
    private Map<String, NBTBase> tagMap = new Object2ObjectOpenHashMap<>();

    /**
     * Write the actual data contents of the tag, implemented in NBT extension
     * classes
     */
    void write(DataOutput p_74734_1_) throws IOException {
        Iterator<Entry<String, NBTBase>> var2 = this.tagMap.entrySet().iterator();

        while (var2.hasNext()) {
            Entry<String, NBTBase> e = var2.next();
            String var3 = e.getKey();
            NBTBase var4 = e.getValue();
            func_150298_a(var3, var4, p_74734_1_);
        }

        p_74734_1_.writeByte(0);
    }

    void func_152446_a(DataInput p_152446_1_, int p_152446_2_, NBTSizeTracker p_152446_3_) throws IOException {
        if (p_152446_2_ > 512) {
            throw new RuntimeException("Tried to read NBT tag with too high complexity, depth > 512");
        } else {
            this.tagMap.clear();
            byte var4;

            while ((var4 = func_152447_a(p_152446_1_, p_152446_3_)) != 0) {
                String var5 = func_152448_b(p_152446_1_, p_152446_3_);
                p_152446_3_.func_152450_a((long) (16 * var5.length()));
                NBTBase var6 = func_152449_a(var4, var5, p_152446_1_, p_152446_2_ + 1, p_152446_3_);
                this.tagMap.put(var5, var6);
            }
        }
    }

    public Set func_150296_c() {
        return this.tagMap.keySet();
    }

    /**
     * Gets the type byte for the tag.
     */
    public byte getId() {
        return (byte) 10;
    }

    /**
     * Stores the given tag into the map with the given string key. This is mostly
     * used to store tag lists.
     */
    public void setTag(String key, NBTBase val) {
        this.tagMap.put(key, val);
    }

    /**
     * Stores a new NBTTagByte with the given byte value into the map with the given
     * string key.
     */
    public void setByte(String p_74774_1_, byte p_74774_2_) {
        this.tagMap.put(p_74774_1_, new NBTTagByte(p_74774_2_));
    }

    /**
     * Stores a new NBTTagShort with the given short value into the map with the
     * given string key.
     */
    public void setShort(String p_74777_1_, short p_74777_2_) {
        this.tagMap.put(p_74777_1_, new NBTTagShort(p_74777_2_));
    }

    /**
     * Stores a new NBTTagInt with the given integer value into the map with the
     * given string key.
     */
    public void setInteger(String p_74768_1_, int p_74768_2_) {
        this.tagMap.put(p_74768_1_, new NBTTagInt(p_74768_2_));
    }

    /**
     * Stores a new NBTTagLong with the given long value into the map with the given
     * string key.
     */
    public void setLong(String p_74772_1_, long p_74772_2_) {
        this.tagMap.put(p_74772_1_, new NBTTagLong(p_74772_2_));
    }

    /**
     * Stores a new NBTTagFloat with the given float value into the map with the
     * given string key.
     */
    public void setFloat(String p_74776_1_, float p_74776_2_) {
        this.tagMap.put(p_74776_1_, new NBTTagFloat(p_74776_2_));
    }

    /**
     * Stores a new NBTTagDouble with the given double value into the map with the
     * given string key.
     */
    public void setDouble(String p_74780_1_, double p_74780_2_) {
        this.tagMap.put(p_74780_1_, new NBTTagDouble(p_74780_2_));
    }

    /**
     * Stores a new NBTTagString with the given string value into the map with the
     * given string key.
     */
    public void setString(String p_74778_1_, String p_74778_2_) {
        this.tagMap.put(p_74778_1_, new NBTTagString(p_74778_2_));
    }

    /**
     * Stores a new NBTTagByteArray with the given array as data into the map with
     * the given string key.
     */
    public void setByteArray(String p_74773_1_, byte[] p_74773_2_) {
        this.tagMap.put(p_74773_1_, new NBTTagByteArray(p_74773_2_));
    }

    /**
     * Stores a new NBTTagIntArray with the given array as data into the map with
     * the given string key.
     */
    public void setIntArray(String p_74783_1_, int[] p_74783_2_) {
        this.tagMap.put(p_74783_1_, new NBTTagIntArray(p_74783_2_));
    }

    /**
     * Stores the given boolean value as a NBTTagByte, storing 1 for true and 0 for
     * false, using the given string key.
     */
    public void setBoolean(String p_74757_1_, boolean p_74757_2_) {
        this.setByte(p_74757_1_, (byte) (p_74757_2_ ? 1 : 0));
    }

    /**
     * gets a generic tag with the specified name
     */
    public NBTBase getTag(String p_74781_1_) {
        return (NBTBase) this.tagMap.get(p_74781_1_);
    }

    public byte func_150299_b(String p_150299_1_) {
        NBTBase var2 = (NBTBase) this.tagMap.get(p_150299_1_);
        return var2 != null ? var2.getId() : 0;
    }

    /**
     * Returns whether the given string has been previously stored as a key in the
     * map.
     */
    public boolean hasKey(String key) {
        return this.tagMap.containsKey(key);
    }

    public boolean func_150297_b(String p_150297_1_, int p_150297_2_) {
        byte var3 = this.func_150299_b(p_150297_1_);
        return var3 == p_150297_2_ ? true
                : (p_150297_2_ != 99 ? false
                : var3 == 1 || var3 == 2 || var3 == 3 || var3 == 4 || var3 == 5 || var3 == 6);
    }

    /**
     * Retrieves a byte value using the specified key, or 0 if no such key was
     * stored.
     */
    public byte getByte(String p_74771_1_) {
        try {
            return !this.tagMap.containsKey(p_74771_1_) ? 0
                    : ((NBTBase.NBTPrimitive) this.tagMap.get(p_74771_1_)).func_150290_f();
        } catch (ClassCastException var3) {
            return (byte) 0;
        }
    }

    /**
     * Retrieves a short value using the specified key, or 0 if no such key was
     * stored.
     */
    public short getShort(String p_74765_1_) {
        try {
            return !this.tagMap.containsKey(p_74765_1_) ? 0
                    : ((NBTBase.NBTPrimitive) this.tagMap.get(p_74765_1_)).func_150289_e();
        } catch (ClassCastException var3) {
            return (short) 0;
        }
    }

    /**
     * Retrieves an integer value using the specified key, or 0 if no such key was
     * stored.
     */
    public int getInteger(String p_74762_1_) {
        try {
            return !this.tagMap.containsKey(p_74762_1_) ? 0
                    : ((NBTBase.NBTPrimitive) this.tagMap.get(p_74762_1_)).func_150287_d();
        } catch (ClassCastException var3) {
            return 0;
        }
    }

    /**
     * Retrieves a long value using the specified key, or 0 if no such key was
     * stored.
     */
    public long getLong(String p_74763_1_) {
        try {
            return !this.tagMap.containsKey(p_74763_1_) ? 0L
                    : ((NBTBase.NBTPrimitive) this.tagMap.get(p_74763_1_)).func_150291_c();
        } catch (ClassCastException var3) {
            return 0L;
        }
    }

    /**
     * Retrieves a float value using the specified key, or 0 if no such key was
     * stored.
     */
    public float getFloat(String p_74760_1_) {
        try {
            return !this.tagMap.containsKey(p_74760_1_) ? 0.0F
                    : ((NBTBase.NBTPrimitive) this.tagMap.get(p_74760_1_)).func_150288_h();
        } catch (ClassCastException var3) {
            return 0.0F;
        }
    }

    /**
     * Retrieves a double value using the specified key, or 0 if no such key was
     * stored.
     */
    public double getDouble(String p_74769_1_) {
        try {
            return !this.tagMap.containsKey(p_74769_1_) ? 0.0D
                    : ((NBTBase.NBTPrimitive) this.tagMap.get(p_74769_1_)).func_150286_g();
        } catch (ClassCastException var3) {
            return 0.0D;
        }
    }

    /**
     * Retrieves a string value using the specified key, or an empty string if no
     * such key was stored.
     */
    public String getString(String p_74779_1_) {
        try {
            return !this.tagMap.containsKey(p_74779_1_) ? "" : ((NBTBase) this.tagMap.get(p_74779_1_)).func_150285_a_();
        } catch (ClassCastException var3) {
            return "";
        }
    }

    /**
     * Retrieves a byte array using the specified key, or a zero-length array if no
     * such key was stored.
     */
    public byte[] getByteArray(String p_74770_1_) {
        try {
            return !this.tagMap.containsKey(p_74770_1_) ? new byte[0]
                    : ((NBTTagByteArray) this.tagMap.get(p_74770_1_)).func_150292_c();
        } catch (ClassCastException var3) {
            throw new ReportedException(this.createCrashReport(p_74770_1_, 7, var3));
        }
    }

    /**
     * Retrieves an int array using the specified key, or a zero-length array if no
     * such key was stored.
     */
    public int[] getIntArray(String p_74759_1_) {
        try {
            return !this.tagMap.containsKey(p_74759_1_) ? new int[0]
                    : ((NBTTagIntArray) this.tagMap.get(p_74759_1_)).func_150302_c();
        } catch (ClassCastException var3) {
            throw new ReportedException(this.createCrashReport(p_74759_1_, 11, var3));
        }
    }

    /**
     * Retrieves a NBTTagCompound subtag matching the specified key, or a new empty
     * NBTTagCompound if no such key was
     * stored.
     */
    public NBTTagCompound getCompoundTag(String p_74775_1_) {
        try {
            return !this.tagMap.containsKey(p_74775_1_) ? new NBTTagCompound()
                    : (NBTTagCompound) this.tagMap.get(p_74775_1_);
        } catch (ClassCastException var3) {
            throw new ReportedException(this.createCrashReport(p_74775_1_, 10, var3));
        }
    }

    /**
     * Gets the NBTTagList object with the given name. Args: name, NBTBase type
     */
    public NBTTagList getTagList(String p_150295_1_, int p_150295_2_) {
        try {
            if (this.func_150299_b(p_150295_1_) != 9) {
                return new NBTTagList();
            } else {
                NBTTagList var3 = (NBTTagList) this.tagMap.get(p_150295_1_);
                return var3.tagCount() > 0 && var3.func_150303_d() != p_150295_2_ ? new NBTTagList() : var3;
            }
        } catch (ClassCastException var4) {
            throw new ReportedException(this.createCrashReport(p_150295_1_, 9, var4));
        }
    }

    /**
     * Retrieves a boolean value using the specified key, or false if no such key
     * was stored. This uses the getByte
     * method.
     */
    public boolean getBoolean(String p_74767_1_) {
        return this.getByte(p_74767_1_) != 0;
    }

    /**
     * Remove the specified tag.
     */
    public void removeTag(String p_82580_1_) {
        this.tagMap.remove(p_82580_1_);
    }

    public String toString() {
        String var1 = "{";
        String var3;

        for (Iterator var2 = this.tagMap.keySet().iterator(); var2
                .hasNext(); var1 = var1 + var3 + ':' + this.tagMap.get(var3) + ',') {
            var3 = (String) var2.next();
        }

        return var1 + "}";
    }

    /**
     * Return whether this compound has no tags.
     */
    public boolean hasNoTags() {
        return this.tagMap.isEmpty();
    }

    /**
     * Create a crash report which indicates a NBT read error.
     */
    private CrashReport createCrashReport(final String p_82581_1_, final int p_82581_2_,
                                          ClassCastException p_82581_3_) {
        CrashReport var4 = CrashReport.makeCrashReport(p_82581_3_, "Reading NBT data");
        CrashReportCategory var5 = var4.makeCategoryDepth("Corrupt NBT tag", 1);
        var5.addCrashSectionCallable("Tag type found",
                () -> NBTBase.NBTTypes[((NBTBase) NBTTagCompound.this.tagMap.get(p_82581_1_)).getId()]);
        var5.addCrashSectionCallable("Tag type expected", () -> NBTBase.NBTTypes[p_82581_2_]);
        var5.addCrashSection("Tag name", p_82581_1_);
        return var4;
    }

    /**
     * Creates a clone of the tag.
     */
    public NBTBase copy() {
        NBTTagCompound var1 = new NBTTagCompound();
        Iterator var2 = this.tagMap.keySet().iterator();

        while (var2.hasNext()) {
            String var3 = (String) var2.next();
            var1.setTag(var3, ((NBTBase) this.tagMap.get(var3)).copy());
        }

        return var1;
    }

    public boolean equals(Object p_equals_1_) {
        if (super.equals(p_equals_1_)) {
            NBTTagCompound var2 = (NBTTagCompound) p_equals_1_;
            return this.tagMap.entrySet().equals(var2.tagMap.entrySet());
        } else {
            return false;
        }
    }

    public int hashCode() {
        return super.hashCode() ^ this.tagMap.hashCode();
    }

    private static void func_150298_a(String p_150298_0_, NBTBase p_150298_1_, DataOutput p_150298_2_)
            throws IOException {
        p_150298_2_.writeByte(p_150298_1_.getId());

        if (p_150298_1_.getId() != 0) {
            p_150298_2_.writeUTF(p_150298_0_);
            p_150298_1_.write(p_150298_2_);
        }
    }

    private static byte func_152447_a(DataInput p_152447_0_, NBTSizeTracker p_152447_1_) throws IOException {
        return p_152447_0_.readByte();
    }

    private static String func_152448_b(DataInput p_152448_0_, NBTSizeTracker p_152448_1_) throws IOException {
        return p_152448_0_.readUTF();
    }

    static NBTBase func_152449_a(byte p_152449_0_, String p_152449_1_, DataInput p_152449_2_, int p_152449_3_,
                                 NBTSizeTracker p_152449_4_) {
        NBTBase var5 = NBTBase.func_150284_a(p_152449_0_);

        try {
            var5.func_152446_a(p_152449_2_, p_152449_3_, p_152449_4_);
            return var5;
        } catch (IOException var9) {
            CrashReport var7 = CrashReport.makeCrashReport(var9, "Loading NBT data");
            CrashReportCategory var8 = var7.makeCategory("NBT Tag");
            var8.addCrashSection("Tag name", p_152449_1_);
            var8.addCrashSection("Tag type", Byte.valueOf(p_152449_0_));
            throw new ReportedException(var7);
        }
    }
}
