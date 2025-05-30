package net.minecraft.network.login.server;

import java.io.IOException;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.minecraft.client.Minecraft;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.login.INetHandlerLoginClient;
import net.minecraft.util.IChatComponent;

@NoArgsConstructor
@AllArgsConstructor
public class S00PacketDisconnect extends Packet {
    private IChatComponent field_149605_a;

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer p_148837_1_, Minecraft mc) throws IOException {
        this.field_149605_a = IChatComponent.Serializer.fromJson(p_148837_1_.readStringFromBuffer(32767));
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
        p_148840_1_.writeStringToBuffer(IChatComponent.Serializer.toJson(this.field_149605_a));
    }

    public void processPacket(INetHandlerLoginClient p_148833_1_) {
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

    public IChatComponent func_149603_c() {
        return this.field_149605_a;
    }

    public void processPacket(INetHandler p_148833_1_) {
        this.processPacket((INetHandlerLoginClient) p_148833_1_);
    }
}
