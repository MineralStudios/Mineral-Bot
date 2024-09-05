package net.minecraft.network.play.client;

import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;
import org.apache.commons.lang3.StringUtils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class C14PacketTabComplete extends Packet {
    private String message;

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer p_148837_1_, Minecraft mc) throws IOException {
        this.message = p_148837_1_.readStringFromBuffer(32767);
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
        p_148840_1_.writeStringToBuffer(StringUtils.substring(this.message, 0, 32767));
    }

    public void processPacket(INetHandlerPlayServer p_148833_1_) {
        p_148833_1_.processTabComplete(this);
    }

    /**
     * Returns a string formatted as comma separated [field]=[value] values. Used by
     * Minecraft for logging purposes.
     */
    public String serialize() {
        return String.format("message=\'%s\'", new Object[] { this.message });
    }

    public void processPacket(INetHandler p_148833_1_) {
        this.processPacket((INetHandlerPlayServer) p_148833_1_);
    }
}
