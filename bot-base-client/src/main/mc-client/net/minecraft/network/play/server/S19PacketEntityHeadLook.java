package net.minecraft.network.play.server;

import java.io.IOException;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.world.World;

@AllArgsConstructor
@NoArgsConstructor
public class S19PacketEntityHeadLook extends Packet {
    private int field_149384_a;
    private byte field_149383_b;

    public S19PacketEntityHeadLook(Entity p_i45214_1_, byte p_i45214_2_) {
        this.field_149384_a = p_i45214_1_.getEntityId();
        this.field_149383_b = p_i45214_2_;
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer p_148837_1_, Minecraft mc) throws IOException {
        this.field_149384_a = p_148837_1_.readInt();
        this.field_149383_b = p_148837_1_.readByte();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
        p_148840_1_.writeInt(this.field_149384_a);
        p_148840_1_.writeByte(this.field_149383_b);
    }

    public void processPacket(INetHandlerPlayClient p_148833_1_) {
        p_148833_1_.handleEntityHeadLook(this);
    }

    /**
     * Returns a string formatted as comma separated [field]=[value] values. Used by
     * Minecraft for logging purposes.
     */
    public String serialize() {
        return String.format("id=%d, rot=%d",
                new Object[] { Integer.valueOf(this.field_149384_a), Byte.valueOf(this.field_149383_b) });
    }

    public Entity func_149381_a(World p_149381_1_) {
        return p_149381_1_.getEntityByID(this.field_149384_a);
    }

    public byte func_149380_c() {
        return this.field_149383_b;
    }

    public void processPacket(INetHandler p_148833_1_) {
        this.processPacket((INetHandlerPlayClient) p_148833_1_);
    }
}
