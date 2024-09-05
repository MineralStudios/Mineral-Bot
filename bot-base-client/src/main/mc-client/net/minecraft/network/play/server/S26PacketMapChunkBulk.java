package net.minecraft.network.play.server;

import java.io.IOException;
import java.util.List;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

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
public class S26PacketMapChunkBulk extends Packet {
    private int[] chunkXArr;
    private int[] chunkZArr;
    private int[] primaryBitMapArr;
    private int[] addBitMapArr;
    private byte[] compressedByteArray;
    private byte[][] dataArr;
    private int compressedSize;
    private boolean hasSky;

    public S26PacketMapChunkBulk(Minecraft mc, int[] chunkXArr, int[] chunkZArr, ExtendedBlockStorage[][] chunkSections,
            byte[][] biomeArrays, boolean groundUpContinuous) {
        int chunkCount = chunkXArr.length;
        this.chunkXArr = new int[chunkCount];
        this.chunkZArr = new int[chunkCount];
        this.primaryBitMapArr = new int[chunkCount];
        this.addBitMapArr = new int[chunkCount];
        this.dataArr = new byte[chunkCount][];
        this.hasSky = true;
        int var3 = 0;

        byte[] buffer = mc.getReadBuffer();

        for (int var4 = 0; var4 < chunkCount; ++var4) {
            S21PacketChunkData.Extracted var6 = this.func_149269_a(mc, chunkSections[var4], biomeArrays[var4],
                    groundUpContinuous, 65535);

            if (buffer.length < var3 + var6.data.length) {
                byte[] var7 = new byte[var3 + var6.data.length];
                System.arraycopy(buffer, 0, var7, 0, buffer.length);
                buffer = var7;
            }

            System.arraycopy(var6.data, 0, buffer, var3, var6.data.length);
            var3 += var6.data.length;
            this.chunkXArr[var4] = chunkXArr[var4];
            this.chunkZArr[var4] = chunkZArr[var4];
            this.primaryBitMapArr[var4] = var6.primaryBitMap;
            this.addBitMapArr[var4] = var6.addBitMap;
            this.dataArr[var4] = var6.data;
        }

        Deflater var11 = new Deflater(-1);

        try {
            var11.setInput(buffer, 0, var3);
            var11.finish();
            this.compressedByteArray = new byte[var3];
            this.compressedSize = var11.deflate(this.compressedByteArray);
        } finally {
            var11.end();
        }
    }

    public S26PacketMapChunkBulk(Minecraft mc, List<Chunk> chunks) {
        int chunkCount = chunks.size();
        this.chunkXArr = new int[chunkCount];
        this.chunkZArr = new int[chunkCount];
        this.primaryBitMapArr = new int[chunkCount];
        this.addBitMapArr = new int[chunkCount];
        this.dataArr = new byte[chunkCount][];
        this.hasSky = !chunks.isEmpty() && !((Chunk) chunks.get(0)).worldObj.provider.hasNoSky;
        int var3 = 0;

        byte[] buffer = mc.getReadBuffer();

        for (int var4 = 0; var4 < chunkCount; ++var4) {
            Chunk var5 = (Chunk) chunks.get(var4);
            S21PacketChunkData.Extracted var6 = this.func_149269_a(mc, var5, true, 65535);

            if (buffer.length < var3 + var6.data.length) {
                byte[] var7 = new byte[var3 + var6.data.length];
                System.arraycopy(buffer, 0, var7, 0, buffer.length);
                buffer = var7;
            }

            System.arraycopy(var6.data, 0, buffer, var3, var6.data.length);
            var3 += var6.data.length;
            this.chunkXArr[var4] = var5.xPosition;
            this.chunkZArr[var4] = var5.zPosition;
            this.primaryBitMapArr[var4] = var6.primaryBitMap;
            this.addBitMapArr[var4] = var6.addBitMap;
            this.dataArr[var4] = var6.data;
        }

        Deflater var11 = new Deflater(-1);

        try {
            var11.setInput(buffer, 0, var3);
            var11.finish();
            this.compressedByteArray = new byte[var3];
            this.compressedSize = var11.deflate(this.compressedByteArray);
        } finally {
            var11.end();
        }
    }

    public S21PacketChunkData.Extracted func_149269_a(Minecraft mc, ExtendedBlockStorage[] var4, byte[] biomeArray,
            boolean groundUpContinuous,
            int p_149269_2_) {
        int var3 = 0;
        int var5 = 0;
        S21PacketChunkData.Extracted var6 = new S21PacketChunkData.Extracted();
        byte[] var7 = mc.getReadCompressedDataBuffer();

        int var8;

        for (var8 = 0; var8 < var4.length; ++var8) {
            if (var4[var8] != null && (!groundUpContinuous || !var4[var8].isEmpty())
                    && (p_149269_2_ & 1 << var8) != 0) {
                var6.primaryBitMap |= 1 << var8;

                if (var4[var8].getBlockMSBArray() != null) {
                    var6.addBitMap |= 1 << var8;
                    ++var5;
                }
            }
        }

        for (var8 = 0; var8 < var4.length; ++var8) {
            if (var4[var8] != null && (!groundUpContinuous || !var4[var8].isEmpty())
                    && (p_149269_2_ & 1 << var8) != 0) {
                byte[] var9 = var4[var8].getBlockLSBArray();
                System.arraycopy(var9, 0, var7, var3, var9.length);
                var3 += var9.length;
            }
        }

        NibbleArray var11;

        for (var8 = 0; var8 < var4.length; ++var8) {
            if (var4[var8] != null && (!groundUpContinuous || !var4[var8].isEmpty())
                    && (p_149269_2_ & 1 << var8) != 0) {
                var11 = var4[var8].getMetadataArray();
                System.arraycopy(var11.data, 0, var7, var3, var11.data.length);
                var3 += var11.data.length;
            }
        }

        for (var8 = 0; var8 < var4.length; ++var8) {
            if (var4[var8] != null && (!groundUpContinuous || !var4[var8].isEmpty())
                    && (p_149269_2_ & 1 << var8) != 0) {
                var11 = var4[var8].getBlocklightArray();
                System.arraycopy(var11.data, 0, var7, var3, var11.data.length);
                var3 += var11.data.length;
            }
        }

        for (var8 = 0; var8 < var4.length; ++var8) {
            if (var4[var8] != null && (!groundUpContinuous || !var4[var8].isEmpty())
                    && (p_149269_2_ & 1 << var8) != 0) {
                var11 = var4[var8].getSkylightArray();

                if (var11 != null) {
                    System.arraycopy(var11.data, 0, var7, var3, var11.data.length);
                    var3 += var11.data.length;
                }
            }
        }

        if (var5 > 0) {
            for (var8 = 0; var8 < var4.length; ++var8) {
                if (var4[var8] != null && (!groundUpContinuous || !var4[var8].isEmpty())
                        && var4[var8].getBlockMSBArray() != null && (p_149269_2_ & 1 << var8) != 0) {
                    var11 = var4[var8].getBlockMSBArray();
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

    public S21PacketChunkData.Extracted func_149269_a(Minecraft mc, Chunk chunk, boolean groundUpContinuous,
            int p_149269_2_) {
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
                    && (p_149269_2_ & 1 << var8) != 0) {
                var6.primaryBitMap |= 1 << var8;

                if (var4[var8].getBlockMSBArray() != null) {
                    var6.addBitMap |= 1 << var8;
                    ++var5;
                }
            }
        }

        for (var8 = 0; var8 < var4.length; ++var8) {
            if (var4[var8] != null && (!groundUpContinuous || !var4[var8].isEmpty())
                    && (p_149269_2_ & 1 << var8) != 0) {
                byte[] var9 = var4[var8].getBlockLSBArray();
                System.arraycopy(var9, 0, var7, var3, var9.length);
                var3 += var9.length;
            }
        }

        NibbleArray var11;

        for (var8 = 0; var8 < var4.length; ++var8) {
            if (var4[var8] != null && (!groundUpContinuous || !var4[var8].isEmpty())
                    && (p_149269_2_ & 1 << var8) != 0) {
                var11 = var4[var8].getMetadataArray();
                System.arraycopy(var11.data, 0, var7, var3, var11.data.length);
                var3 += var11.data.length;
            }
        }

        for (var8 = 0; var8 < var4.length; ++var8) {
            if (var4[var8] != null && (!groundUpContinuous || !var4[var8].isEmpty())
                    && (p_149269_2_ & 1 << var8) != 0) {
                var11 = var4[var8].getBlocklightArray();
                System.arraycopy(var11.data, 0, var7, var3, var11.data.length);
                var3 += var11.data.length;
            }
        }

        if (!chunk.worldObj.provider.hasNoSky) {
            for (var8 = 0; var8 < var4.length; ++var8) {
                if (var4[var8] != null && (!groundUpContinuous || !var4[var8].isEmpty())
                        && (p_149269_2_ & 1 << var8) != 0) {
                    var11 = var4[var8].getSkylightArray();
                    System.arraycopy(var11.data, 0, var7, var3, var11.data.length);
                    var3 += var11.data.length;
                }
            }
        }

        if (var5 > 0) {
            for (var8 = 0; var8 < var4.length; ++var8) {
                if (var4[var8] != null && (!groundUpContinuous || !var4[var8].isEmpty())
                        && var4[var8].getBlockMSBArray() != null && (p_149269_2_ & 1 << var8) != 0) {
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

    public static int func_149258_c() {
        return 5;
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer p_148837_1_, Minecraft mc) throws IOException {
        short var2 = p_148837_1_.readShort();
        this.compressedSize = p_148837_1_.readInt();
        this.hasSky = p_148837_1_.readBoolean();
        this.chunkXArr = new int[var2];
        this.chunkZArr = new int[var2];
        this.primaryBitMapArr = new int[var2];
        this.addBitMapArr = new int[var2];
        this.dataArr = new byte[var2][];

        byte[] buffer = mc.getReadBuffer();

        if (buffer.length < this.compressedSize) {
            mc.setReadBuffer(new byte[this.compressedSize]);
        }

        p_148837_1_.readBytes(buffer, 0, this.compressedSize);
        byte[] var3 = new byte[S21PacketChunkData.func_149275_c() * var2];
        Inflater var4 = new Inflater();
        var4.setInput(buffer, 0, this.compressedSize);

        try {
            var4.inflate(var3);
        } catch (DataFormatException var12) {
            throw new IOException("Bad compressed data format");
        } finally {
            var4.end();
        }

        int var5 = 0;

        for (int var6 = 0; var6 < var2; ++var6) {
            this.chunkXArr[var6] = p_148837_1_.readInt();
            this.chunkZArr[var6] = p_148837_1_.readInt();
            this.primaryBitMapArr[var6] = p_148837_1_.readShort();
            this.addBitMapArr[var6] = p_148837_1_.readShort();
            int var7 = 0;
            int var8 = 0;
            int var9;

            for (var9 = 0; var9 < 16; ++var9) {
                var7 += this.primaryBitMapArr[var6] >> var9 & 1;
                var8 += this.addBitMapArr[var6] >> var9 & 1;
            }

            var9 = 2048 * 4 * var7 + 256;
            var9 += 2048 * var8;

            if (this.hasSky) {
                var9 += 2048 * var7;
            }

            this.dataArr[var6] = new byte[var9];
            System.arraycopy(var3, var5, this.dataArr[var6], 0, var9);
            var5 += var9;
        }
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
        p_148840_1_.writeShort(this.chunkXArr.length);
        p_148840_1_.writeInt(this.compressedSize);
        p_148840_1_.writeBoolean(this.hasSky);
        p_148840_1_.writeBytes(this.compressedByteArray, 0, this.compressedSize);

        for (int var2 = 0; var2 < this.chunkXArr.length; ++var2) {
            p_148840_1_.writeInt(this.chunkXArr[var2]);
            p_148840_1_.writeInt(this.chunkZArr[var2]);
            p_148840_1_.writeShort((short) (this.primaryBitMapArr[var2] & 65535));
            p_148840_1_.writeShort((short) (this.addBitMapArr[var2] & 65535));
        }
    }

    public void processPacket(INetHandlerPlayClient p_148833_1_) {
        p_148833_1_.handleMapChunkBulk(this);
    }

    public int func_149255_a(int p_149255_1_) {
        return this.chunkXArr[p_149255_1_];
    }

    public int func_149253_b(int p_149253_1_) {
        return this.chunkZArr[p_149253_1_];
    }

    public int func_149254_d() {
        return this.chunkXArr.length;
    }

    public byte[] func_149256_c(int p_149256_1_) {
        return this.dataArr[p_149256_1_];
    }

    /**
     * Returns a string formatted as comma separated [field]=[value] values. Used by
     * Minecraft for logging purposes.
     */
    public String serialize() {
        StringBuilder var1 = new StringBuilder();

        for (int var2 = 0; var2 < this.chunkXArr.length; ++var2) {
            if (var2 > 0) {
                var1.append(", ");
            }

            var1.append(String.format("{x=%d, z=%d, sections=%d, adds=%d, data=%d}",
                    new Object[] { Integer.valueOf(this.chunkXArr[var2]),
                            Integer.valueOf(this.chunkZArr[var2]), Integer.valueOf(this.primaryBitMapArr[var2]),
                            Integer.valueOf(this.addBitMapArr[var2]),
                            Integer.valueOf(this.dataArr[var2].length) }));
        }

        return String.format("size=%d, chunks=%d[%s]", new Object[] { Integer.valueOf(this.compressedSize),
                Integer.valueOf(this.chunkXArr.length), var1 });
    }

    public int[] func_149252_e() {
        return this.primaryBitMapArr;
    }

    public int[] func_149257_f() {
        return this.addBitMapArr;
    }

    public void processPacket(INetHandler p_148833_1_) {
        this.processPacket((INetHandlerPlayClient) p_148833_1_);
    }
}
