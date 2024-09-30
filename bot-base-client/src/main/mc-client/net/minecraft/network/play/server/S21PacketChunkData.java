package net.minecraft.network.play.server;

import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import lombok.Getter;
import lombok.NoArgsConstructor;
import net.minecraft.client.Minecraft;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

@NoArgsConstructor
public class S21PacketChunkData extends Packet {
    @Getter
    private int chunkX, chunkZ, primaryBitMap, addBitMap;
    // Only used in write
    private byte[] compressedData;
    // Only used in read
    @Getter
    private byte[] uncompressedData;
    @Getter
    private boolean groundUpContinuous;
    private int compressedSize;

    public S21PacketChunkData(Minecraft mc, int chunkX, int chunkZ, ExtendedBlockStorage[] chunkSections,
            byte[] biomeArray,
            boolean groundUpContinuous,
            int primaryBitMask) {
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        this.groundUpContinuous = groundUpContinuous;
        S21PacketChunkData.Extracted var4 = func_149269_a(mc, chunkSections, biomeArray,
                groundUpContinuous, primaryBitMask);
        // Deflater var5 = new Deflater(-1);
        this.addBitMap = var4.addBitMap;
        this.primaryBitMap = var4.primaryBitMap;

        try {
            this.uncompressedData = var4.data;
            // var5.setInput(var4.data, 0, var4.data.length);
            // var5.finish();
            // this.compressedData = new byte[var4.data.length];
            // this.compressedSize = var5.deflate(this.compressedData);
        } finally {
            // var5.end();
        }
    }

    public S21PacketChunkData(Minecraft mc, Chunk chunk, boolean groundUpContinuous, int primaryBitMask) {
        this.chunkX = chunk.xPosition;
        this.chunkZ = chunk.zPosition;
        this.groundUpContinuous = groundUpContinuous;
        S21PacketChunkData.Extracted var4 = func_149269_a(mc, chunk, groundUpContinuous, primaryBitMask);
        Deflater var5 = new Deflater(-1);
        this.addBitMap = var4.addBitMap;
        this.primaryBitMap = var4.primaryBitMap;

        try {
            this.uncompressedData = var4.data;
            var5.setInput(var4.data, 0, var4.data.length);
            var5.finish();
            this.compressedData = new byte[var4.data.length];
            this.compressedSize = var5.deflate(this.compressedData);
        } finally {
            var5.end();
        }
    }

    public static int func_149275_c() {
        return 196864;
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    @Override
    public void readPacketData(PacketBuffer p_148837_1_, Minecraft mc) throws IOException {
        this.chunkX = p_148837_1_.readInt();
        this.chunkZ = p_148837_1_.readInt();
        this.groundUpContinuous = p_148837_1_.readBoolean();
        this.primaryBitMap = p_148837_1_.readShort();
        this.addBitMap = p_148837_1_.readShort();
        this.compressedSize = p_148837_1_.readInt();

        if (mc.getReadCompressedDataBuffer().length < this.compressedSize)
            mc.setReadCompressedDataBuffer(new byte[this.compressedSize]);

        p_148837_1_.readBytes(mc.getReadCompressedDataBuffer(), 0, this.compressedSize);
        int var2 = 0;
        int var3;

        for (var3 = 0; var3 < 16; ++var3)
            var2 += this.primaryBitMap >> var3 & 1;

        var3 = 12288 * var2;

        if (this.groundUpContinuous)
            var3 += 256;

        this.uncompressedData = new byte[var3];
        Inflater var4 = new Inflater();
        var4.setInput(mc.getReadCompressedDataBuffer(), 0, this.compressedSize);

        try {
            var4.inflate(this.uncompressedData);
        } catch (DataFormatException var9) {
            throw new IOException("Bad compressed data format");
        } finally {
            var4.end();
        }
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    @Override
    public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
        p_148840_1_.writeInt(this.chunkX);
        p_148840_1_.writeInt(this.chunkZ);
        p_148840_1_.writeBoolean(this.groundUpContinuous);
        p_148840_1_.writeShort((short) (this.primaryBitMap & 65535));
        p_148840_1_.writeShort((short) (this.addBitMap & 65535));
        p_148840_1_.writeInt(this.compressedSize);
        p_148840_1_.writeBytes(this.compressedData, 0, this.compressedSize);
    }

    public void processPacket(INetHandlerPlayClient p_148833_1_) {
        p_148833_1_.handleChunkData(this);
    }

    /**
     * Returns a string formatted as comma separated [field]=[value] values. Used by
     * Minecraft for logging purposes.
     */
    public String serialize() {
        return String.format("x=%d, z=%d, full=%b, sects=%d, add=%d, size=%d",
                new Object[] { Integer.valueOf(this.chunkX), Integer.valueOf(this.chunkZ),
                        Boolean.valueOf(this.groundUpContinuous), Integer.valueOf(this.primaryBitMap),
                        Integer.valueOf(this.addBitMap), Integer.valueOf(this.compressedSize) });
    }

    public S21PacketChunkData.Extracted func_149269_a(Minecraft mc, Chunk chunk, boolean groundUpContinuous,
            int primaryBitMask) {
        int var3 = 0;
        ExtendedBlockStorage[] var4 = chunk.getBlockStorageArray();
        int var5 = 0;
        S21PacketChunkData.Extracted var6 = new S21PacketChunkData.Extracted();
        byte[] var7 = mc.getReadCompressedDataBuffer();

        if (groundUpContinuous)
            chunk.sendUpdates = true;

        int var8;

        for (var8 = 0; var8 < var4.length; ++var8) {
            if (var4[var8] != null && (!groundUpContinuous || !var4[var8].isEmpty())
                    && (primaryBitMask & 1 << var8) != 0) {
                var6.primaryBitMap |= 1 << var8;

                if (var4[var8].getBlockMSBArray() != null) {
                    var6.addBitMap |= 1 << var8;
                    ++var5;
                }
            }
        }

        for (var8 = 0; var8 < var4.length; ++var8) {
            if (var4[var8] != null && (!groundUpContinuous || !var4[var8].isEmpty())
                    && (primaryBitMask & 1 << var8) != 0) {
                byte[] var9 = var4[var8].getBlockLSBArray();
                System.arraycopy(var9, 0, var7, var3, var9.length);
                var3 += var9.length;
            }
        }

        NibbleArray var11;

        for (var8 = 0; var8 < var4.length; ++var8) {
            if (var4[var8] != null && (!groundUpContinuous || !var4[var8].isEmpty())
                    && (primaryBitMask & 1 << var8) != 0) {
                var11 = var4[var8].getMetadataArray();
                System.arraycopy(var11.data, 0, var7, var3, var11.data.length);
                var3 += var11.data.length;
            }
        }

        for (var8 = 0; var8 < var4.length; ++var8) {
            if (var4[var8] != null && (!groundUpContinuous || !var4[var8].isEmpty())
                    && (primaryBitMask & 1 << var8) != 0) {
                var11 = var4[var8].getBlocklightArray();
                System.arraycopy(var11.data, 0, var7, var3, var11.data.length);
                var3 += var11.data.length;
            }
        }

        if (!chunk.worldObj.provider.hasNoSky) {
            for (var8 = 0; var8 < var4.length; ++var8) {
                if (var4[var8] != null && (!groundUpContinuous || !var4[var8].isEmpty())
                        && (primaryBitMask & 1 << var8) != 0) {
                    var11 = var4[var8].getSkylightArray();
                    System.arraycopy(var11.data, 0, var7, var3, var11.data.length);
                    var3 += var11.data.length;
                }
            }
        }

        if (var5 > 0) {
            for (var8 = 0; var8 < var4.length; ++var8) {
                if (var4[var8] != null && (!groundUpContinuous || !var4[var8].isEmpty())
                        && var4[var8].getBlockMSBArray() != null && (primaryBitMask & 1 << var8) != 0) {
                    var11 = var4[var8].getBlockMSBArray();
                    System.arraycopy(var11.data, 0, var7, var3, var11.data.length);
                    var3 += var11.data.length;
                }
            }
        }

        if (groundUpContinuous) {
            byte[] var10 = chunk.getBiomeArray();
            System.arraycopy(var10, 0, var7, var3, var10.length);
            var3 += var10.length;
        }

        var6.data = new byte[var3];
        System.arraycopy(var7, 0, var6.data, 0, var3);
        return var6;
    }

    public S21PacketChunkData.Extracted func_149269_a(Minecraft mc, ExtendedBlockStorage[] blockStorage,
            byte[] biomeArray,
            boolean groundUpContinuous, int primaryBitMask) {
        int var3 = 0;
        int var5 = 0;
        S21PacketChunkData.Extracted var6 = new S21PacketChunkData.Extracted();
        byte[] var7 = mc.getReadCompressedDataBuffer();

        int var8;

        for (var8 = 0; var8 < blockStorage.length; ++var8) {
            if (blockStorage[var8] != null && (!groundUpContinuous || !blockStorage[var8].isEmpty())
                    && (primaryBitMask & 1 << var8) != 0) {
                var6.primaryBitMap |= 1 << var8;

                if (blockStorage[var8].getBlockMSBArray() != null) {
                    var6.addBitMap |= 1 << var8;
                    ++var5;
                }
            }
        }

        for (var8 = 0; var8 < blockStorage.length; ++var8) {
            if (blockStorage[var8] != null && (!groundUpContinuous || !blockStorage[var8].isEmpty())
                    && (primaryBitMask & 1 << var8) != 0) {
                byte[] var9 = blockStorage[var8].getBlockLSBArray();
                System.arraycopy(var9, 0, var7, var3, var9.length);
                var3 += var9.length;
            }
        }

        NibbleArray var11;

        for (var8 = 0; var8 < blockStorage.length; ++var8) {
            if (blockStorage[var8] != null && (!groundUpContinuous || !blockStorage[var8].isEmpty())
                    && (primaryBitMask & 1 << var8) != 0) {
                var11 = blockStorage[var8].getMetadataArray();
                System.arraycopy(var11.data, 0, var7, var3, var11.data.length);
                var3 += var11.data.length;
            }
        }

        for (var8 = 0; var8 < blockStorage.length; ++var8) {
            if (blockStorage[var8] != null && (!groundUpContinuous || !blockStorage[var8].isEmpty())
                    && (primaryBitMask & 1 << var8) != 0) {
                var11 = blockStorage[var8].getBlocklightArray();
                System.arraycopy(var11.data, 0, var7, var3, var11.data.length);
                var3 += var11.data.length;
            }
        }

        for (var8 = 0; var8 < blockStorage.length; ++var8) {
            if (blockStorage[var8] != null && (!groundUpContinuous || !blockStorage[var8].isEmpty())
                    && (primaryBitMask & 1 << var8) != 0) {
                var11 = blockStorage[var8].getSkylightArray();

                if (var11 != null) {
                    System.arraycopy(var11.data, 0, var7, var3, var11.data.length);
                    var3 += var11.data.length;
                }
            }
        }

        if (var5 > 0) {
            for (var8 = 0; var8 < blockStorage.length; ++var8) {
                if (blockStorage[var8] != null && (!groundUpContinuous || !blockStorage[var8].isEmpty())
                        && blockStorage[var8].getBlockMSBArray() != null && (primaryBitMask & 1 << var8) != 0) {
                    var11 = blockStorage[var8].getBlockMSBArray();
                    System.arraycopy(var11.data, 0, var7, var3, var11.data.length);
                    var3 += var11.data.length;
                }
            }
        }

        if (groundUpContinuous) {
            byte[] var10 = biomeArray;
            System.arraycopy(var10, 0, var7, var3, var10.length);
            var3 += var10.length;
        }

        var6.data = new byte[var3];
        System.arraycopy(var7, 0, var6.data, 0, var3);
        return var6;
    }

    public void processPacket(INetHandler p_148833_1_) {
        this.processPacket((INetHandlerPlayClient) p_148833_1_);
    }

    public static class Extracted {
        public byte[] data;
        public int primaryBitMap, addBitMap;
    }
}
