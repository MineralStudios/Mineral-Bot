package net.minecraft.network.play.server;

import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.IChatComponent;

public class S40PacketDisconnect extends Packet {
    private IChatComponent field_149167_a;

    public S40PacketDisconnect() {
    }

    public S40PacketDisconnect(IChatComponent p_i46336_1_) {
        this.field_149167_a = p_i46336_1_;
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer p_148837_1_, Minecraft mc) throws IOException {
        this.field_149167_a = IChatComponent.Serializer.fromJson(p_148837_1_.readStringFromBuffer(32767));
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
        p_148840_1_.writeStringToBuffer(IChatComponent.Serializer.toJson(this.field_149167_a));
    }

    public void processPacket(INetHandlerPlayClient p_148833_1_) {
        p_148833_1_.handleDisconnect(this);
    }

    /**
     * If true, the network manager will process the packet immediately when
     * received, otherwise it will queue it for
     * processing. Currently true for: Disconnect, LoginSuccess, KeepAlive,
     * ServerQuery/Info, Ping/Pong
     */
    public boolean hasPriority() {
        return true;
    }

    public IChatComponent func_149165_c() {
        return this.field_149167_a;
    }

    public void processPacket(INetHandler p_148833_1_) {
        this.processPacket((INetHandlerPlayClient) p_148833_1_);
    }
}
