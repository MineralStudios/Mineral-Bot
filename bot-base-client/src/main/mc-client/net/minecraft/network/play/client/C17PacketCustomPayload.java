package net.minecraft.network.play.client;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

@Getter
@NoArgsConstructor
public class C17PacketCustomPayload extends Packet {
    private String channel;
    private int length;
    private byte[] data;

    public C17PacketCustomPayload(String p_i45248_1_, ByteBuf p_i45248_2_) {
        this(p_i45248_1_, p_i45248_2_.array());
    }

    public C17PacketCustomPayload(String p_i45249_1_, byte[] p_i45249_2_) {
        this.channel = p_i45249_1_;
        this.data = p_i45249_2_;

        if (p_i45249_2_ != null) {
            this.length = p_i45249_2_.length;

            if (this.length >= 32767)
                throw new IllegalArgumentException("Payload may not be larger than 32k");
        }
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer p_148837_1_, Minecraft mc) throws IOException {
        this.channel = p_148837_1_.readStringFromBuffer(20);
        this.length = p_148837_1_.readShort();

        if (this.length > 0 && this.length < 32767) {
            this.data = new byte[this.length];
            p_148837_1_.readBytes(this.data);
        }
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
        p_148840_1_.writeStringToBuffer(this.channel);
        p_148840_1_.writeShort((short) this.length);

        if (this.data != null)
            p_148840_1_.writeBytes(this.data);
    }

    public void processPacket(INetHandlerPlayServer p_148833_1_) {
        p_148833_1_.processVanilla250Packet(this);
    }

    public void processPacket(INetHandler p_148833_1_) {
        this.processPacket((INetHandlerPlayServer) p_148833_1_);
    }
}
