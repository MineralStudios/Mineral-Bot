package net.minecraft.network.play.server;

import java.io.IOException;

import lombok.NoArgsConstructor;
import net.minecraft.client.Minecraft;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

@NoArgsConstructor
public class S06PacketUpdateHealth extends Packet {
    private float field_149336_a;
    private int field_149334_b;
    private float field_149335_c;

    public S06PacketUpdateHealth(float p_i45223_1_, int p_i45223_2_, float p_i45223_3_) {
        this.field_149336_a = p_i45223_1_;
        this.field_149334_b = p_i45223_2_;
        this.field_149335_c = p_i45223_3_;
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer p_148837_1_, Minecraft mc) throws IOException {
        this.field_149336_a = p_148837_1_.readFloat();
        this.field_149334_b = p_148837_1_.readShort();
        this.field_149335_c = p_148837_1_.readFloat();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
        p_148840_1_.writeFloat(this.field_149336_a);
        p_148840_1_.writeShort(this.field_149334_b);
        p_148840_1_.writeFloat(this.field_149335_c);
    }

    public void processPacket(INetHandlerPlayClient p_148833_1_) {
        p_148833_1_.handleUpdateHealth(this);
    }

    public float func_149332_c() {
        return this.field_149336_a;
    }

    public int func_149330_d() {
        return this.field_149334_b;
    }

    public float func_149331_e() {
        return this.field_149335_c;
    }

    public void processPacket(INetHandler p_148833_1_) {
        this.processPacket((INetHandlerPlayClient) p_148833_1_);
    }
}
