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
public class C11PacketEnchantItem extends Packet {
    private int windowId, enchantment;

    public void processPacket(INetHandlerPlayServer p_148833_1_) {
        p_148833_1_.processEnchantItem(this);
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer p_148837_1_, Minecraft mc) throws IOException {
        this.windowId = p_148837_1_.readByte();
        this.enchantment = p_148837_1_.readByte();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
        p_148840_1_.writeByte(this.windowId);
        p_148840_1_.writeByte(this.enchantment);
    }

    /**
     * Returns a string formatted as comma separated [field]=[value] values. Used by
     * Minecraft for logging purposes.
     */
    public String serialize() {
        return String.format("id=%d, button=%d",
                new Object[] { Integer.valueOf(this.windowId), Integer.valueOf(this.enchantment) });
    }

    public void processPacket(INetHandler p_148833_1_) {
        this.processPacket((INetHandlerPlayServer) p_148833_1_);
    }
}
