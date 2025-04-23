package net.minecraft.network.login.client;

import com.mojang.authlib.GameProfile;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.minecraft.client.Minecraft;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.login.INetHandlerLoginServer;

import java.io.IOException;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class C00PacketLoginStart extends Packet {
    private GameProfile gameProfile;

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer p_148837_1_, Minecraft mc) throws IOException {
        this.gameProfile = new GameProfile(null, p_148837_1_.readStringFromBuffer(16));
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
        p_148840_1_.writeStringToBuffer(this.gameProfile.getName());
    }

    public void processPacket(INetHandlerLoginServer p_148833_1_) {
        p_148833_1_.processLoginStart(this);
    }

    public void processPacket(INetHandler p_148833_1_) {
        this.processPacket((INetHandlerLoginServer) p_148833_1_);
    }
}
