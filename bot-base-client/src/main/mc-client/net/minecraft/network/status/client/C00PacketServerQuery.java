package net.minecraft.network.status.client;

import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.status.INetHandlerStatusServer;

public class C00PacketServerQuery extends Packet {

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer p_148837_1_, Minecraft mc) throws IOException {
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
    }

    public void processPacket(INetHandlerStatusServer p_148833_1_) {
        p_148833_1_.processServerQuery(this);
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

    public void processPacket(INetHandler p_148833_1_) {
        this.processPacket((INetHandlerStatusServer) p_148833_1_);
    }
}
