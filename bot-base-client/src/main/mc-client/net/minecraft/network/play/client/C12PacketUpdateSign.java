package net.minecraft.network.play.client;

import java.io.IOException;

import lombok.Getter;
import lombok.NoArgsConstructor;
import net.minecraft.client.Minecraft;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

@NoArgsConstructor
@Getter
public class C12PacketUpdateSign extends Packet {
    private int x, y, z;
    private String[] lines;

    public C12PacketUpdateSign(int p_i45264_1_, int p_i45264_2_, int p_i45264_3_, String[] p_i45264_4_) {
        this.x = p_i45264_1_;
        this.y = p_i45264_2_;
        this.z = p_i45264_3_;
        this.lines = new String[] { p_i45264_4_[0], p_i45264_4_[1], p_i45264_4_[2], p_i45264_4_[3] };
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer p_148837_1_, Minecraft mc) throws IOException {
        this.x = p_148837_1_.readInt();
        this.y = p_148837_1_.readShort();
        this.z = p_148837_1_.readInt();
        this.lines = new String[4];

        for (int var2 = 0; var2 < 4; ++var2)
            this.lines[var2] = p_148837_1_.readStringFromBuffer(15);
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
        p_148840_1_.writeInt(this.x);
        p_148840_1_.writeShort(this.y);
        p_148840_1_.writeInt(this.z);

        for (int var2 = 0; var2 < 4; ++var2)
            p_148840_1_.writeStringToBuffer(this.lines[var2]);
    }

    public void processPacket(INetHandlerPlayServer p_148833_1_) {
        p_148833_1_.processUpdateSign(this);
    }

    public void processPacket(INetHandler p_148833_1_) {
        this.processPacket((INetHandlerPlayServer) p_148833_1_);
    }
}
