package net.minecraft.network.play.server;

import java.io.IOException;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.minecraft.client.Minecraft;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

@AllArgsConstructor
@NoArgsConstructor
public class S28PacketEffect extends Packet {
    private int field_149251_a;
    private int field_149249_b;
    private int field_149250_c;
    private int field_149247_d;
    private int field_149248_e;
    private boolean field_149246_f;

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer p_148837_1_, Minecraft mc) throws IOException {
        this.field_149251_a = p_148837_1_.readInt();
        this.field_149250_c = p_148837_1_.readInt();
        this.field_149247_d = p_148837_1_.readByte() & 255;
        this.field_149248_e = p_148837_1_.readInt();
        this.field_149249_b = p_148837_1_.readInt();
        this.field_149246_f = p_148837_1_.readBoolean();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
        p_148840_1_.writeInt(this.field_149251_a);
        p_148840_1_.writeInt(this.field_149250_c);
        p_148840_1_.writeByte(this.field_149247_d & 255);
        p_148840_1_.writeInt(this.field_149248_e);
        p_148840_1_.writeInt(this.field_149249_b);
        p_148840_1_.writeBoolean(this.field_149246_f);
    }

    public void processPacket(INetHandlerPlayClient p_148833_1_) {
        p_148833_1_.handleEffect(this);
    }

    public boolean func_149244_c() {
        return this.field_149246_f;
    }

    public int func_149242_d() {
        return this.field_149251_a;
    }

    public int func_149241_e() {
        return this.field_149249_b;
    }

    public int func_149240_f() {
        return this.field_149250_c;
    }

    public int func_149243_g() {
        return this.field_149247_d;
    }

    public int func_149239_h() {
        return this.field_149248_e;
    }

    public void processPacket(INetHandler p_148833_1_) {
        this.processPacket((INetHandlerPlayClient) p_148833_1_);
    }
}
