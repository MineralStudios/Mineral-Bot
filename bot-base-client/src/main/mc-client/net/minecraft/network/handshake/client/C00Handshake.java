package net.minecraft.network.handshake.client;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.minecraft.client.Minecraft;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.handshake.INetHandlerHandshakeServer;

import java.io.IOException;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class C00Handshake extends Packet {
    private int protocolVersion;
    private String serverAddress;
    private int serverPort;
    private EnumConnectionState connectionState;

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer p_148837_1_, Minecraft mc) throws IOException {
        this.protocolVersion = p_148837_1_.readVarIntFromBuffer();
        this.serverAddress = p_148837_1_.readStringFromBuffer(255);
        this.serverPort = p_148837_1_.readUnsignedShort();
        this.connectionState = EnumConnectionState.func_150760_a(p_148837_1_.readVarIntFromBuffer());
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
        p_148840_1_.writeVarIntToBuffer(this.protocolVersion);
        p_148840_1_.writeStringToBuffer(this.serverAddress);
        p_148840_1_.writeShort(this.serverPort);
        p_148840_1_.writeVarIntToBuffer(this.connectionState.func_150759_c());
    }

    public void processPacket(INetHandlerHandshakeServer p_148833_1_) {
        p_148833_1_.processHandshake(this);
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

    public EnumConnectionState func_149594_c() {
        return this.connectionState;
    }

    public int func_149595_d() {
        return this.protocolVersion;
    }

    public void processPacket(INetHandler p_148833_1_) {
        this.processPacket((INetHandlerHandshakeServer) p_148833_1_);
    }
}
