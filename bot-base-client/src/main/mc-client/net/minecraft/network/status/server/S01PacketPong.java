package net.minecraft.network.status.server;

import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.status.INetHandlerStatusClient;

public class S01PacketPong extends Packet {
    private long field_149293_a;

    public S01PacketPong() {
    }

    public S01PacketPong(long p_i45272_1_) {
        this.field_149293_a = p_i45272_1_;
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer p_148837_1_, Minecraft mc) throws IOException {
        this.field_149293_a = p_148837_1_.readLong();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
        p_148840_1_.writeLong(this.field_149293_a);
    }

    public void processPacket(INetHandlerStatusClient p_148833_1_) {
        p_148833_1_.handlePong(this);
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

    public long func_149292_c() {
        return this.field_149293_a;
    }

    public void processPacket(INetHandler p_148833_1_) {
        this.processPacket((INetHandlerStatusClient) p_148833_1_);
    }
}
