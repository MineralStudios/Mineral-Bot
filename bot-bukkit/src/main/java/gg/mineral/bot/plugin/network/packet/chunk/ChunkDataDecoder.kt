package gg.mineral.bot.plugin.network.packet.chunk

import com.github.benmanes.caffeine.cache.Caffeine
import net.minecraft.world.chunk.NibbleArray
import net.minecraft.world.chunk.storage.ExtendedBlockStorage
import java.util.concurrent.TimeUnit

object ChunkDataDecoder {
    private val chunkCache = Caffeine.newBuilder()
        .maximumSize(100)
        .expireAfterWrite(5, TimeUnit.MINUTES)
        .build<ChunkParams, ChunkFillData>()

    fun decode(data: ByteArray, primaryBitMask: Int, groundUpContinuous: Boolean): ChunkFillData {
        val chunkParams = ChunkParams(data, primaryBitMask, groundUpContinuous)
        return chunkCache[chunkParams, {
            var i = 0
            val hasSky = true

            val storageArrays =
                arrayOfNulls<ExtendedBlockStorage>(16)
            val blockBiomeArray = ByteArray(256)

            // Process each section of the chunk
            for (j in storageArrays.indices) {
                if ((primaryBitMask and (1 shl j)) != 0) {
                    if (storageArrays[j] == null) storageArrays[j] =
                        ExtendedBlockStorage(j shl 4, hasSky)

                    val storage = storageArrays[j]

                    // Iterate through the block storage and directly assign values
                    for (k in 0..4095) {
                        if (i + 1 >= data.size) break

                        val x = k and 15
                        val y = (k shr 8) and 15
                        val z = (k shr 4) and 15

                        // Retrieve block ID and metadata from the data array
                        var blockId =
                            ((data[i + 1].toInt() and 255) shl 8) or (data[i].toInt() and 255)
                        val metadata = blockId and 15 // The last 4 bits are the metadata
                        blockId = blockId shr 4 // The remaining bits are the block ID

                        // Set the block ID and metadata directly into the storage arrays
                        storage!!.blockLSBArray[k] = (blockId and 255).toByte()

                        if (blockId > 255) {
                            if (storage.blockMSBArray == null) storage.blockMSBArray =
                                NibbleArray(4096, 4)

                            storage.blockMSBArray[x, y, z] = (blockId shr 8) and 15
                        } else if (storage.blockMSBArray != null) storage.blockMSBArray[x, y, z] = 0

                        storage.metadataArray[x, y, z] = metadata

                        i += 2
                    }
                } else if (groundUpContinuous && storageArrays[j] != null) storageArrays[j] = null
            }

            // Copy block light array data
            for (l in storageArrays.indices) {
                if ((primaryBitMask and (1 shl l)) != 0 && storageArrays[l] != null) {
                    val blocklightArray = storageArrays[l]!!.blocklightArray
                    if (i + blocklightArray.getData().size > data.size) break

                    System.arraycopy(
                        data,
                        i,
                        blocklightArray.getData(),
                        0,
                        blocklightArray.getData().size
                    )
                    i += blocklightArray.getData().size
                }
            }

            // Copy sky light array data if applicable
            if (hasSky) {
                for (m in storageArrays.indices) {
                    if ((primaryBitMask and (1 shl m)) != 0 && storageArrays[m] != null) {
                        val skylightArray = storageArrays[m]!!.skylightArray
                        if (i + skylightArray.getData().size > data.size) break

                        System.arraycopy(
                            data,
                            i,
                            skylightArray.getData(),
                            0,
                            skylightArray.getData().size
                        )
                        i += skylightArray.getData().size
                    }
                }
            }

            // Copy biome data if ground-up continuous
            if (groundUpContinuous) if (i + blockBiomeArray.size <= data.size) System.arraycopy(
                data,
                i,
                blockBiomeArray,
                0,
                blockBiomeArray.size
            )

            // Recalculate reference counts for each section
            for (n in storageArrays.indices) if (storageArrays[n] != null && (primaryBitMask and (1 shl n)) != 0) storageArrays[n]!!
                .removeInvalidBlocks()
            ChunkFillData(storageArrays, blockBiomeArray)
        }]
    }

    @JvmRecord
    data class ChunkFillData(val storageArrays: Array<ExtendedBlockStorage?>, val blockBiomeArray: ByteArray) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as ChunkFillData

            if (!storageArrays.contentEquals(other.storageArrays)) return false
            if (!blockBiomeArray.contentEquals(other.blockBiomeArray)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = storageArrays.contentHashCode()
            result = 31 * result + blockBiomeArray.contentHashCode()
            return result
        }
    }

    @JvmRecord
    private data class ChunkParams(val data: ByteArray, val primaryBitMask: Int, val groundUpContinuous: Boolean) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as ChunkParams

            if (primaryBitMask != other.primaryBitMask) return false
            if (groundUpContinuous != other.groundUpContinuous) return false
            if (!data.contentEquals(other.data)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = primaryBitMask
            result = 31 * result + groundUpContinuous.hashCode()
            result = 31 * result + data.contentHashCode()
            return result
        }
    }
}
