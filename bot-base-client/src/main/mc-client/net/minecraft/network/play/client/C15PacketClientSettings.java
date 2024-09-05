package net.minecraft.network.play.client;

import java.io.IOException;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.world.EnumDifficulty;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class C15PacketClientSettings extends Packet {
    private String locale;
    private int viewDistance;
    private EntityPlayer.EnumChatVisibility chatFlags;
    private boolean chatColors;
    private EnumDifficulty difficulty;
    private boolean showCape;

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer p_148837_1_, Minecraft mc) throws IOException {
        this.locale = p_148837_1_.readStringFromBuffer(7);
        this.viewDistance = p_148837_1_.readByte();
        this.chatFlags = EntityPlayer.EnumChatVisibility.getEnumChatVisibility(p_148837_1_.readByte());
        this.chatColors = p_148837_1_.readBoolean();
        this.difficulty = EnumDifficulty.getDifficultyEnum(p_148837_1_.readByte());
        this.showCape = p_148837_1_.readBoolean();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
        p_148840_1_.writeStringToBuffer(this.locale);
        p_148840_1_.writeByte(this.viewDistance);
        p_148840_1_.writeByte(this.chatFlags.getChatVisibility());
        p_148840_1_.writeBoolean(this.chatColors);
        p_148840_1_.writeByte(this.difficulty.getDifficultyId());
        p_148840_1_.writeBoolean(this.showCape);
    }

    public void processPacket(INetHandlerPlayServer p_148833_1_) {
        p_148833_1_.processClientSettings(this);
    }

    /**
     * Returns a string formatted as comma separated [field]=[value] values. Used by
     * Minecraft for logging purposes.
     */
    public String serialize() {
        return String.format("lang=\'%s\', view=%d, chat=%s, col=%b, difficulty=%s, cape=%b",
                new Object[] { this.locale, Integer.valueOf(this.viewDistance), this.chatFlags,
                        Boolean.valueOf(this.chatColors), this.difficulty,
                        Boolean.valueOf(this.showCape) });
    }

    public void processPacket(INetHandler p_148833_1_) {
        this.processPacket((INetHandlerPlayServer) p_148833_1_);
    }
}
