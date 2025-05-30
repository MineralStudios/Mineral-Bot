package net.minecraft.network.play.server;

import java.io.IOException;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.minecraft.client.Minecraft;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.potion.PotionEffect;

@AllArgsConstructor
@NoArgsConstructor
public class S1EPacketRemoveEntityEffect extends Packet {
    private int field_149079_a;
    private int field_149078_b;

    public S1EPacketRemoveEntityEffect(int p_i45212_1_, PotionEffect p_i45212_2_) {
        this.field_149079_a = p_i45212_1_;
        this.field_149078_b = p_i45212_2_.getPotionID();
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer p_148837_1_, Minecraft mc) throws IOException {
        this.field_149079_a = p_148837_1_.readInt();
        this.field_149078_b = p_148837_1_.readUnsignedByte();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
        p_148840_1_.writeInt(this.field_149079_a);
        p_148840_1_.writeByte(this.field_149078_b);
    }

    public void processPacket(INetHandlerPlayClient p_148833_1_) {
        p_148833_1_.handleRemoveEntityEffect(this);
    }

    public int func_149076_c() {
        return this.field_149079_a;
    }

    public int func_149075_d() {
        return this.field_149078_b;
    }

    public void processPacket(INetHandler p_148833_1_) {
        this.processPacket((INetHandlerPlayClient) p_148833_1_);
    }
}
