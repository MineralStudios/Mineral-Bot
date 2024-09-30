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
public class C0FPacketConfirmTransaction extends Packet {
    private int windowId;
    private short actionNumber;
    private boolean accepted;

    public void processPacket(INetHandlerPlayServer p_148833_1_) {
        p_148833_1_.processConfirmTransaction(this);
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer p_148837_1_, Minecraft mc) throws IOException {
        this.windowId = p_148837_1_.readByte();
        this.actionNumber = p_148837_1_.readShort();
        this.accepted = p_148837_1_.readByte() != 0;
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
        p_148840_1_.writeByte(this.windowId);
        p_148840_1_.writeShort(this.actionNumber);
        p_148840_1_.writeByte(this.accepted ? 1 : 0);
    }

    /**
     * Returns a string formatted as comma separated [field]=[value] values. Used by
     * Minecraft for logging purposes.
     */
    public String serialize() {
        return String.format("id=%d, uid=%d, accepted=%b", new Object[] { Integer.valueOf(this.windowId),
                Short.valueOf(this.actionNumber), Boolean.valueOf(this.accepted) });
    }

    public void processPacket(INetHandler p_148833_1_) {
        this.processPacket((INetHandlerPlayServer) p_148833_1_);
    }
}
