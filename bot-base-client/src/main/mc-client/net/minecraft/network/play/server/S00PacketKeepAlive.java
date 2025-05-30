package net.minecraft.network.play.server;

import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S00PacketKeepAlive extends Packet {
    private int field_149136_a;

    public S00PacketKeepAlive() {
    }

    public S00PacketKeepAlive(int p_i45195_1_) {
        this.field_149136_a = p_i45195_1_;
    }

    public void processPacket(INetHandlerPlayClient p_148833_1_) {
        p_148833_1_.handleKeepAlive(this);
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer p_148837_1_, Minecraft mc) throws IOException {
        this.field_149136_a = p_148837_1_.readInt();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
        p_148840_1_.writeInt(this.field_149136_a);
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

    public int func_149134_c() {
        return this.field_149136_a;
    }

    public void processPacket(INetHandler netHandler) {
        this.processPacket((INetHandlerPlayClient) netHandler);
    }
}
