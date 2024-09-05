package net.minecraft.network.play.client;

import java.io.IOException;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.minecraft.client.Minecraft;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class C0CPacketInput extends Packet {
    private float sideways, forward;
    private boolean jump, unmount;

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer p_148837_1_, Minecraft mc) throws IOException {
        this.sideways = p_148837_1_.readFloat();
        this.forward = p_148837_1_.readFloat();
        this.jump = p_148837_1_.readBoolean();
        this.unmount = p_148837_1_.readBoolean();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
        p_148840_1_.writeFloat(this.sideways);
        p_148840_1_.writeFloat(this.forward);
        p_148840_1_.writeBoolean(this.jump);
        p_148840_1_.writeBoolean(this.unmount);
    }

    public void processPacket(INetHandlerPlayServer p_148833_1_) {
        p_148833_1_.processInput(this);
    }

    public void processPacket(INetHandler p_148833_1_) {
        this.processPacket((INetHandlerPlayServer) p_148833_1_);
    }
}
