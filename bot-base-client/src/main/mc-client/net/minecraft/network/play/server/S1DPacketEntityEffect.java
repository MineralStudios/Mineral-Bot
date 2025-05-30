package net.minecraft.network.play.server;

import java.io.IOException;

import lombok.AllArgsConstructor;
import net.minecraft.client.Minecraft;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.potion.PotionEffect;

@AllArgsConstructor
public class S1DPacketEntityEffect extends Packet {
    private int field_149434_a;
    private byte field_149432_b;
    private byte field_149433_c;
    private short field_149431_d;

    public S1DPacketEntityEffect() {
    }

    public S1DPacketEntityEffect(int p_i45237_1_, PotionEffect p_i45237_2_) {
        this.field_149434_a = p_i45237_1_;
        this.field_149432_b = (byte) (p_i45237_2_.getPotionID() & 255);
        this.field_149433_c = (byte) (p_i45237_2_.getAmplifier() & 255);

        if (p_i45237_2_.getDuration() > 32767) {
            this.field_149431_d = 32767;
        } else {
            this.field_149431_d = (short) p_i45237_2_.getDuration();
        }
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer p_148837_1_, Minecraft mc) throws IOException {
        this.field_149434_a = p_148837_1_.readInt();
        this.field_149432_b = p_148837_1_.readByte();
        this.field_149433_c = p_148837_1_.readByte();
        this.field_149431_d = p_148837_1_.readShort();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
        p_148840_1_.writeInt(this.field_149434_a);
        p_148840_1_.writeByte(this.field_149432_b);
        p_148840_1_.writeByte(this.field_149433_c);
        p_148840_1_.writeShort(this.field_149431_d);
    }

    public boolean func_149429_c() {
        return this.field_149431_d == 32767;
    }

    public void processPacket(INetHandlerPlayClient p_148833_1_) {
        p_148833_1_.handleEntityEffect(this);
    }

    public int func_149426_d() {
        return this.field_149434_a;
    }

    public byte func_149427_e() {
        return this.field_149432_b;
    }

    public byte func_149428_f() {
        return this.field_149433_c;
    }

    public short func_149425_g() {
        return this.field_149431_d;
    }

    public void processPacket(INetHandler p_148833_1_) {
        this.processPacket((INetHandlerPlayClient) p_148833_1_);
    }
}
