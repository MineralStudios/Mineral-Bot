package net.minecraft.network.status.client;

import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.status.INetHandlerStatusServer;

import java.io.IOException;

@Getter
public class C01PacketPing extends Packet {
    private long pingId;

    public C01PacketPing() {
    }

    public C01PacketPing(long p_i45276_1_) {
        this.pingId = p_i45276_1_;
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer p_148837_1_, Minecraft mc) throws IOException {
        this.pingId = p_148837_1_.readLong();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
        p_148840_1_.writeLong(this.pingId);
    }

    public void processPacket(INetHandlerStatusServer p_148833_1_) {
        p_148833_1_.processPing(this);
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

    public long func_149289_c() {
        return this.pingId;
    }

    public void processPacket(INetHandler p_148833_1_) {
        this.processPacket((INetHandlerStatusServer) p_148833_1_);
    }
}
