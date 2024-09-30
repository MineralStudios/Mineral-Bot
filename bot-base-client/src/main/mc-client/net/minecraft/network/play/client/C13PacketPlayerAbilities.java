package net.minecraft.network.play.client;

import java.io.IOException;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

@NoArgsConstructor
@Getter
@Setter
public class C13PacketPlayerAbilities extends Packet {
    private boolean disableDamage, flying, allowFlying, creativeMode;
    private float flySpeed, walkSpeed;

    public C13PacketPlayerAbilities(PlayerCapabilities p_i45257_1_) {
        this.setDisableDamage(p_i45257_1_.disableDamage);
        this.setFlying(p_i45257_1_.isFlying);
        this.setAllowFlying(p_i45257_1_.allowFlying);
        this.setCreativeMode(p_i45257_1_.isCreativeMode);
        this.setFlySpeed(p_i45257_1_.getFlySpeed());
        this.setWalkSpeed(p_i45257_1_.getWalkSpeed());
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer p_148837_1_, Minecraft mc) throws IOException {
        byte var2 = p_148837_1_.readByte();
        this.setDisableDamage((var2 & 1) > 0);
        this.setFlying((var2 & 2) > 0);
        this.setAllowFlying((var2 & 4) > 0);
        this.setCreativeMode((var2 & 8) > 0);
        this.setFlySpeed(p_148837_1_.readFloat());
        this.setWalkSpeed(p_148837_1_.readFloat());
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
        byte var2 = 0;

        if (this.isDisableDamage())
            var2 = (byte) (var2 | 1);

        if (this.isFlying())
            var2 = (byte) (var2 | 2);

        if (this.isAllowFlying())
            var2 = (byte) (var2 | 4);

        if (this.isCreativeMode())
            var2 = (byte) (var2 | 8);

        p_148840_1_.writeByte(var2);
        p_148840_1_.writeFloat(this.flySpeed);
        p_148840_1_.writeFloat(this.walkSpeed);
    }

    public void processPacket(INetHandlerPlayServer p_148833_1_) {
        p_148833_1_.processPlayerAbilities(this);
    }

    /**
     * Returns a string formatted as comma separated [field]=[value] values. Used by
     * Minecraft for logging purposes.
     */
    public String serialize() {
        return String.format("invuln=%b, flying=%b, canfly=%b, instabuild=%b, flyspeed=%.4f, walkspped=%.4f",
                new Object[] { Boolean.valueOf(this.isDisableDamage()), Boolean.valueOf(this.isFlying()),
                        Boolean.valueOf(this.isAllowFlying()), Boolean.valueOf(this.isCreativeMode()),
                        Float.valueOf(this.getFlySpeed()), Float.valueOf(this.getWalkSpeed()) });
    }

    public void processPacket(INetHandler p_148833_1_) {
        this.processPacket((INetHandlerPlayServer) p_148833_1_);
    }
}
