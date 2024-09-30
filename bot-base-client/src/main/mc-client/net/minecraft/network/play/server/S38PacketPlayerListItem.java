package net.minecraft.network.play.server;

import java.io.IOException;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.minecraft.client.Minecraft;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

@AllArgsConstructor
@NoArgsConstructor
public class S38PacketPlayerListItem extends Packet {
    private String playerName;
    private boolean online;
    private int ping;

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer p_148837_1_, Minecraft mc) throws IOException {
        this.playerName = p_148837_1_.readStringFromBuffer(16);
        this.online = p_148837_1_.readBoolean();
        this.ping = p_148837_1_.readShort();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
        p_148840_1_.writeStringToBuffer(this.playerName);
        p_148840_1_.writeBoolean(this.online);
        p_148840_1_.writeShort(this.ping);
    }

    public void processPacket(INetHandlerPlayClient p_148833_1_) {
        p_148833_1_.handlePlayerListItem(this);
    }

    public String func_149122_c() {
        return this.playerName;
    }

    public boolean func_149121_d() {
        return this.online;
    }

    public int func_149120_e() {
        return this.ping;
    }

    public void processPacket(INetHandler p_148833_1_) {
        this.processPacket((INetHandlerPlayClient) p_148833_1_);
    }
}
