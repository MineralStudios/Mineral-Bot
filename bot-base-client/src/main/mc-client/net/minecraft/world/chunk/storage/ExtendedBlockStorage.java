package net.minecraft.world.chunk.storage;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.chunk.NibbleArray;

public class ExtendedBlockStorage {
    /**
     * Contains the bottom-most Y block represented by this ExtendedBlockStorage.
     * Typically a multiple of 16.
     */
    private int yBase;

    /**
     * A total count of the number of non-air blocks in this block storage's Chunk.
     */
    private int blockRefCount;

    /**
     * Contains the number of blocks in this block storage's parent chunk that
     * require random ticking. Used to cull the
     * Chunk from random tick updates for performance reasons.
     */
    private int tickRefCount;

    /**
     * Contains the least significant 8 bits of each block ID belonging to this
     * block storage's parent Chunk.
     */
    private byte[] blockLSBArray;

    /**
     * Contains the most significant 4 bits of each block ID belonging to this block
     * storage's parent Chunk.
     */
    private NibbleArray blockMSBArray;

    /**
     * Stores the metadata associated with blocks in this ExtendedBlockStorage.
     */
    private NibbleArray blockMetadataArray;

    /** The NibbleArray containing a block of Block-light data. */
    private NibbleArray blocklightArray;

    /** The NibbleArray containing a block of Sky-light data. */
    private NibbleArray skylightArray;

    public ExtendedBlockStorage(int yBase, boolean skylight) {
        this.yBase = yBase;
        this.blockLSBArray = new byte[4096];
        this.blockMetadataArray = new NibbleArray(this.blockLSBArray.length, 4);
        this.blocklightArray = new NibbleArray(this.blockLSBArray.length, 4);

        if (skylight)
            this.skylightArray = new NibbleArray(this.blockLSBArray.length, 4);
    }

    public Block getBlockAt(int x, int y, int z) {
        int var4 = this.blockLSBArray[y << 8 | z << 4 | x] & 255;

        if (this.blockMSBArray != null)
            var4 |= this.blockMSBArray.get(x, y, z) << 8;

        return Block.getBlockById(var4);
    }

    public void setBlock(int x, int y, int z, Block block) {
        int var5 = this.blockLSBArray[y << 8 | z << 4 | x] & 255;

        if (this.blockMSBArray != null)
            var5 |= this.blockMSBArray.get(x, y, z) << 8;

        Block var6 = Block.getBlockById(var5);

        if (var6 != Blocks.air) {
            --this.blockRefCount;

            if (var6.getTickRandomly())
                --this.tickRefCount;
        }

        if (block != Blocks.air) {
            ++this.blockRefCount;

            if (block.getTickRandomly())
                ++this.tickRefCount;
        }

        int var7 = Block.getIdFromBlock(block);
        this.blockLSBArray[y << 8 | z << 4 | x] = (byte) (var7 & 255);

        if (var7 > 255) {
            if (this.blockMSBArray == null)
                this.blockMSBArray = new NibbleArray(this.blockLSBArray.length, 4);

            this.blockMSBArray.set(x, y, z, (var7 & 3840) >> 8);
        } else if (this.blockMSBArray != null) {
            this.blockMSBArray.set(x, y, z, 0);
        }
    }

    /**
     * Returns the metadata associated with the block at the given coordinates in
     * this ExtendedBlockStorage.
     */
    public int getExtBlockMetadata(int p_76665_1_, int p_76665_2_, int p_76665_3_) {
        return this.blockMetadataArray.get(p_76665_1_, p_76665_2_, p_76665_3_);
    }

    /**
     * Sets the metadata of the Block at the given coordinates in this
     * ExtendedBlockStorage to the given metadata.
     */
    public void setExtBlockMetadata(int p_76654_1_, int p_76654_2_, int p_76654_3_, int p_76654_4_) {
        this.blockMetadataArray.set(p_76654_1_, p_76654_2_, p_76654_3_, p_76654_4_);
    }

    /**
     * Returns whether or not this block storage's Chunk is fully empty, based on
     * its internal reference count.
     */
    public boolean isEmpty() {
        return this.blockRefCount == 0;
    }

    /**
     * Returns whether or not this block storage's Chunk will require random
     * ticking, used to avoid looping through
     * random block ticks when there are no blocks that would randomly tick.
     */
    public boolean getNeedsRandomTick() {
        return this.tickRefCount > 0;
    }

    /**
     * Returns the Y location of this ExtendedBlockStorage.
     */
    public int getYLocation() {
        return this.yBase;
    }

    /**
     * Sets the saved Sky-light value in the extended block storage structure.
     */
    public void setExtSkylightValue(int p_76657_1_, int p_76657_2_, int p_76657_3_, int p_76657_4_) {
        this.skylightArray.set(p_76657_1_, p_76657_2_, p_76657_3_, p_76657_4_);
    }

    /**
     * Gets the saved Sky-light value in the extended block storage structure.
     */
    public int getExtSkylightValue(int p_76670_1_, int p_76670_2_, int p_76670_3_) {
        return this.skylightArray.get(p_76670_1_, p_76670_2_, p_76670_3_);
    }

    /**
     * Sets the saved Block-light value in the extended block storage structure.
     */
    public void setExtBlocklightValue(int p_76677_1_, int p_76677_2_, int p_76677_3_, int p_76677_4_) {
        this.blocklightArray.set(p_76677_1_, p_76677_2_, p_76677_3_, p_76677_4_);
    }

    /**
     * Gets the saved Block-light value in the extended block storage structure.
     */
    public int getExtBlocklightValue(int p_76674_1_, int p_76674_2_, int p_76674_3_) {
        return this.blocklightArray.get(p_76674_1_, p_76674_2_, p_76674_3_);
    }

    public void removeInvalidBlocks() {
        this.blockRefCount = 0;
        this.tickRefCount = 0;

        for (int var1 = 0; var1 < 16; ++var1) {
            for (int var2 = 0; var2 < 16; ++var2) {
                for (int var3 = 0; var3 < 16; ++var3) {
                    Block var4 = this.getBlockAt(var1, var2, var3);

                    if (var4 != Blocks.air) {
                        ++this.blockRefCount;

                        if (var4.getTickRandomly())
                            ++this.tickRefCount;
                    }
                }
            }
        }
    }

    public char[] getData() {
        char[] dataArray = new char[4096];

        for (int i = 0; i < 4096; i++) {
            int x = i & 15;
            int y = (i >> 8) & 15;
            int z = (i >> 4) & 15;

            int lsb = this.blockLSBArray[i] & 255; // Get the least significant bits
            int msb = (this.blockMSBArray != null) ? this.blockMSBArray.get(x, y, z) << 8 : 0; // Get the most
                                                                                               // significant bits

            int blockId = lsb | msb; // Combine the LSB and MSB to form the full block ID
            int metadata = this.blockMetadataArray.get(x, y, z); // Get the metadata

            // Combine block ID and metadata into a single char value
            dataArray[i] = (char) ((blockId << 4) | (metadata & 15));
        }

        return dataArray;
    }

    public void setData(char[] data) {
        if (data.length != 4096) {
            throw new IllegalArgumentException("The length of the data array must be 4096.");
        }

        for (int i = 0; i < 4096; i++) {
            int x = i & 15;
            int y = (i >> 8) & 15;
            int z = (i >> 4) & 15;

            // Extract the block ID and metadata from the char
            int combined = data[i];
            int blockId = (combined >> 4) & 0xFFF; // Extract the block ID (12 bits)
            int metadata = combined & 0xF; // Extract the metadata (4 bits)

            // Set the block ID in the blockLSBArray and blockMSBArray
            this.blockLSBArray[i] = (byte) (blockId & 0xFF);

            if (blockId > 255) {
                if (this.blockMSBArray == null) {
                    this.blockMSBArray = new NibbleArray(this.blockLSBArray.length, 4);
                }
                this.blockMSBArray.set(x, y, z, (blockId >> 8) & 0xF);
            } else if (this.blockMSBArray != null) {
                this.blockMSBArray.set(x, y, z, 0);
            }

            // Set the metadata in the blockMetadataArray
            this.blockMetadataArray.set(x, y, z, metadata);
        }

        // Update blockRefCount and tickRefCount
        this.removeInvalidBlocks();
    }

    public byte[] getBlockLSBArray() {
        return this.blockLSBArray;
    }

    public void clearMSBArray() {
        this.blockMSBArray = null;
    }

    /**
     * Returns the block ID MSB (bits 11..8) array for this storage array's Chunk.
     */
    public NibbleArray getBlockMSBArray() {
        return this.blockMSBArray;
    }

    public NibbleArray getMetadataArray() {
        return this.blockMetadataArray;
    }

    /**
     * Returns the NibbleArray instance containing Block-light data.
     */
    public NibbleArray getBlocklightArray() {
        return this.blocklightArray;
    }

    /**
     * Returns the NibbleArray instance containing Sky-light data.
     */
    public NibbleArray getSkylightArray() {
        return this.skylightArray;
    }

    /**
     * Sets the array of block ID least significant bits for this
     * ExtendedBlockStorage.
     */
    public void setBlockLSBArray(byte[] p_76664_1_) {
        this.blockLSBArray = p_76664_1_;
    }

    /**
     * Sets the array of blockID most significant bits (blockMSBArray) for this
     * ExtendedBlockStorage.
     */
    public void setBlockMSBArray(NibbleArray p_76673_1_) {
        this.blockMSBArray = p_76673_1_;
    }

    /**
     * Sets the NibbleArray of block metadata (blockMetadataArray) for this
     * ExtendedBlockStorage.
     */
    public void setBlockMetadataArray(NibbleArray p_76668_1_) {
        this.blockMetadataArray = p_76668_1_;
    }

    /**
     * Sets the NibbleArray instance used for Block-light values in this particular
     * storage block.
     */
    public void setBlocklightArray(NibbleArray p_76659_1_) {
        this.blocklightArray = p_76659_1_;
    }

    /**
     * Sets the NibbleArray instance used for Sky-light values in this particular
     * storage block.
     */
    public void setSkylightArray(NibbleArray p_76666_1_) {
        this.skylightArray = p_76666_1_;
    }

    /**
     * Called by a Chunk to initialize the MSB array if getBlockMSBArray returns
     * null. Returns the newly-created
     * NibbleArray instance.
     */
    public NibbleArray createBlockMSBArray() {
        this.blockMSBArray = new NibbleArray(this.blockLSBArray.length, 4);
        return this.blockMSBArray;
    }
}
