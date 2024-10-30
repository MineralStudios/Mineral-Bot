package gg.mineral.bot.plugin.network.packet.chunk;

import java.util.concurrent.TimeUnit;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import lombok.val;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

public class ChunkDataDecoder {
    private static Cache<ChunkParams, ChunkFillData> chunkCache = Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .build();

    public static ChunkFillData decode(byte[] data, int primaryBitMask, boolean groundUpContinuous) {
        val chunkParams = new ChunkParams(data, primaryBitMask, groundUpContinuous);
        return chunkCache.get(chunkParams,
                key -> {
                    var i = 0;
                    val hasSky = true;

                    val storageArrays = new ExtendedBlockStorage[16];
                    val blockBiomeArray = new byte[256];

                    // Process each section of the chunk
                    for (int j = 0; j < storageArrays.length; ++j) {
                        if ((primaryBitMask & (1 << j)) != 0) {
                            if (storageArrays[j] == null)
                                storageArrays[j] = new ExtendedBlockStorage(j << 4, hasSky);

                            val storage = storageArrays[j];

                            // Iterate through the block storage and directly assign values
                            for (int k = 0; k < 4096; ++k) {
                                if (i + 1 >= data.length)
                                    break;

                                val x = k & 15;
                                val y = (k >> 8) & 15;
                                val z = (k >> 4) & 15;

                                // Retrieve block ID and metadata from the data array
                                var blockId = ((data[i + 1] & 255) << 8) | (data[i] & 255);
                                val metadata = blockId & 15; // The last 4 bits are the metadata
                                blockId >>= 4; // The remaining bits are the block ID

                                // Set the block ID and metadata directly into the storage arrays
                                storage.getBlockLSBArray()[k] = (byte) (blockId & 255);

                                if (blockId > 255) {
                                    if (storage.getBlockMSBArray() == null)
                                        storage.setBlockMSBArray(new NibbleArray(4096, 4));

                                    storage.getBlockMSBArray().set(x, y, z, (blockId >> 8) & 15);
                                } else if (storage.getBlockMSBArray() != null)
                                    storage.getBlockMSBArray().set(x, y, z, 0);

                                storage.getMetadataArray().set(x, y, z, metadata);

                                i += 2;
                            }
                        } else if (groundUpContinuous && storageArrays[j] != null)
                            storageArrays[j] = null;
                    }

                    // Copy block light array data
                    for (int l = 0; l < storageArrays.length; ++l) {
                        if ((primaryBitMask & (1 << l)) != 0 && storageArrays[l] != null) {
                            val blocklightArray = storageArrays[l].getBlocklightArray();
                            if (i + blocklightArray.getData().length > data.length)
                                break;

                            System.arraycopy(data, i, blocklightArray.getData(), 0, blocklightArray.getData().length);
                            i += blocklightArray.getData().length;
                        }
                    }

                    // Copy sky light array data if applicable
                    if (hasSky) {
                        for (int m = 0; m < storageArrays.length; ++m) {
                            if ((primaryBitMask & (1 << m)) != 0 && storageArrays[m] != null) {
                                val skylightArray = storageArrays[m].getSkylightArray();
                                if (i + skylightArray.getData().length > data.length)
                                    break;

                                System.arraycopy(data, i, skylightArray.getData(), 0, skylightArray.getData().length);
                                i += skylightArray.getData().length;
                            }
                        }
                    }

                    // Copy biome data if ground-up continuous
                    if (groundUpContinuous)
                        if (i + blockBiomeArray.length <= data.length)
                            System.arraycopy(data, i, blockBiomeArray, 0, blockBiomeArray.length);

                    // Recalculate reference counts for each section
                    for (int n = 0; n < storageArrays.length; ++n)
                        if (storageArrays[n] != null && (primaryBitMask & (1 << n)) != 0)
                            storageArrays[n].removeInvalidBlocks();

                    return new ChunkFillData(storageArrays, blockBiomeArray);
                });
    }

    public static record ChunkFillData(ExtendedBlockStorage[] storageArrays, byte[] blockBiomeArray) {
    }

    private static record ChunkParams(byte[] data, int primaryBitMask, boolean groundUpContinuous) {
    }
}
