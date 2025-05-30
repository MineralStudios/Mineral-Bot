package net.minecraft.util;

public class LongHashMap {
    /** the array of all elements in the hash */
    private transient LongHashMap.Entry[] hashArray = new LongHashMap.Entry[1024];

    /** the number of elements in the hash array */
    private transient int numHashElements;

    /**
     * the maximum amount of elements in the hash (probably 3/4 the size due to meh
     * hashing function)
     */
    private int capacity;

    /**
     * percent of the hasharray that can be used without hash colliding probably
     */
    private final float percentUseable;

    /** count of times elements have been added/removed */
    private transient volatile int modCount;

    public LongHashMap() {
        this.capacity = (int) (0.75F * (float) this.hashArray.length);
        this.percentUseable = 0.75F;
    }

    /**
     * returns the hashed key given the original key
     */
    private static int getHashedKey(long par0) {
        return (int) (par0 ^ par0 >>> 27);
    }

    /**
     * the hash function
     */
    private static int hash(int par0) {
        par0 ^= par0 >>> 20 ^ par0 >>> 12;
        return par0 ^ par0 >>> 7 ^ par0 >>> 4;
    }

    /**
     * gets the index in the hash given the array length and the hashed key
     */
    private static int getHashIndex(int par0, int par1) {
        return par0 & par1 - 1;
    }

    public int getNumHashElements() {
        return this.numHashElements;
    }

    /**
     * get the value from the map given the key
     */
    public Object getValueByKey(long par1) {
        int var3 = getHashedKey(par1);

        for (LongHashMap.Entry var4 = this.hashArray[getHashIndex(var3,
                this.hashArray.length)]; var4 != null; var4 = var4.nextEntry) {
            if (var4.key == par1) {
                return var4.value;
            }
        }

        return null;
    }

    public boolean containsItem(long par1) {
        return this.getEntry(par1) != null;
    }

    final LongHashMap.Entry getEntry(long par1) {
        int var3 = getHashedKey(par1);

        for (LongHashMap.Entry var4 = this.hashArray[getHashIndex(var3,
                this.hashArray.length)]; var4 != null; var4 = var4.nextEntry) {
            if (var4.key == par1) {
                return var4;
            }
        }

        return null;
    }

    /**
     * Add a key-value pair.
     */
    public void add(long par1, Object par3Obj) {
        int var4 = getHashedKey(par1);
        int var5 = getHashIndex(var4, this.hashArray.length);

        for (LongHashMap.Entry var6 = this.hashArray[var5]; var6 != null; var6 = var6.nextEntry) {
            if (var6.key == par1) {
                var6.value = par3Obj;
                return;
            }
        }

        ++this.modCount;
        this.createKey(var4, par1, par3Obj, var5);
    }

    /**
     * resizes the table
     */
    private void resizeTable(int par1) {
        LongHashMap.Entry[] var2 = this.hashArray;
        int var3 = var2.length;

        if (var3 == 1073741824) {
            this.capacity = Integer.MAX_VALUE;
        } else {
            LongHashMap.Entry[] var4 = new LongHashMap.Entry[par1];
            this.copyHashTableTo(var4);
            this.hashArray = var4;
            float var10001 = (float) par1;
            this.getClass();
            this.capacity = (int) (var10001 * 0.75F);
        }
    }

    /**
     * copies the hash table to the specified array
     */
    private void copyHashTableTo(LongHashMap.Entry[] par1ArrayOfLongHashMapEntry) {
        LongHashMap.Entry[] var2 = this.hashArray;
        int var3 = par1ArrayOfLongHashMapEntry.length;

        for (int var4 = 0; var4 < var2.length; ++var4) {
            LongHashMap.Entry var5 = var2[var4];

            if (var5 != null) {
                var2[var4] = null;
                LongHashMap.Entry var6;

                do {
                    var6 = var5.nextEntry;
                    int var7 = getHashIndex(var5.hash, var3);
                    var5.nextEntry = par1ArrayOfLongHashMapEntry[var7];
                    par1ArrayOfLongHashMapEntry[var7] = var5;
                    var5 = var6;
                } while (var6 != null);
            }
        }
    }

    /**
     * calls the removeKey method and returns removed object
     */
    public Object remove(long par1) {
        LongHashMap.Entry var3 = this.removeKey(par1);
        return var3 == null ? null : var3.value;
    }

    /**
     * removes the key from the hash linked list
     */
    final LongHashMap.Entry removeKey(long par1) {
        int var3 = getHashedKey(par1);
        int var4 = getHashIndex(var3, this.hashArray.length);
        LongHashMap.Entry var5 = this.hashArray[var4];
        LongHashMap.Entry var6;
        LongHashMap.Entry var7;

        for (var6 = var5; var6 != null; var6 = var7) {
            var7 = var6.nextEntry;

            if (var6.key == par1) {
                ++this.modCount;
                --this.numHashElements;

                if (var5 == var6) {
                    this.hashArray[var4] = var7;
                } else {
                    var5.nextEntry = var7;
                }

                return var6;
            }

            var5 = var6;
        }

        return var6;
    }

    /**
     * creates the key in the hash table
     */
    private void createKey(int par1, long par2, Object par4Obj, int par5) {
        LongHashMap.Entry var6 = this.hashArray[par5];
        this.hashArray[par5] = new LongHashMap.Entry(par1, par2, par4Obj, var6);

        if (this.numHashElements++ >= this.capacity) {
            this.resizeTable(2 * this.hashArray.length);
        }
    }

    public double getKeyDistribution() {
        int countValid = 0;

        for (int i = 0; i < this.hashArray.length; ++i) {
            if (this.hashArray[i] != null) {
                ++countValid;
            }
        }

        return 1.0D * (double) countValid / (double) this.numHashElements;
    }

    static class Entry {
        final long key;
        Object value;
        LongHashMap.Entry nextEntry;
        final int hash;

        Entry(int par1, long par2, Object par4Obj, LongHashMap.Entry par5LongHashMapEntry) {
            this.value = par4Obj;
            this.nextEntry = par5LongHashMapEntry;
            this.key = par2;
            this.hash = par1;
        }

        public final long getKey() {
            return this.key;
        }

        public final Object getValue() {
            return this.value;
        }

        public final boolean equals(Object par1Obj) {
            if (!(par1Obj instanceof LongHashMap.Entry)) {
                return false;
            } else {
                LongHashMap.Entry var2 = (LongHashMap.Entry) par1Obj;
                Long var3 = Long.valueOf(this.getKey());
                Long var4 = Long.valueOf(var2.getKey());

                if (var3 == var4 || var3 != null && var3.equals(var4)) {
                    Object var5 = this.getValue();
                    Object var6 = var2.getValue();

                    if (var5 == var6 || var5 != null && var5.equals(var6)) {
                        return true;
                    }
                }

                return false;
            }
        }

        public final int hashCode() {
            return LongHashMap.getHashedKey(this.key);
        }

        public final String toString() {
            return this.getKey() + "=" + this.getValue();
        }
    }
}
