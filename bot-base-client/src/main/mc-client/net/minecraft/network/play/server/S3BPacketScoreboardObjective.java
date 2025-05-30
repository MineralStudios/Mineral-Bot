package net.minecraft.network.play.server;

import java.io.IOException;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.minecraft.client.Minecraft;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.scoreboard.ScoreObjective;

@NoArgsConstructor
@AllArgsConstructor
public class S3BPacketScoreboardObjective extends Packet {
    private String field_149343_a;
    private String field_149341_b;
    private int field_149342_c;

    public S3BPacketScoreboardObjective(ScoreObjective p_i45224_1_, int p_i45224_2_) {
        this(p_i45224_1_.getName(), p_i45224_1_.getDisplayName(), p_i45224_2_);
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer p_148837_1_, Minecraft mc) throws IOException {
        this.field_149343_a = p_148837_1_.readStringFromBuffer(16);
        this.field_149341_b = p_148837_1_.readStringFromBuffer(32);
        this.field_149342_c = p_148837_1_.readByte();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
        p_148840_1_.writeStringToBuffer(this.field_149343_a);
        p_148840_1_.writeStringToBuffer(this.field_149341_b);
        p_148840_1_.writeByte(this.field_149342_c);
    }

    public void processPacket(INetHandlerPlayClient p_148833_1_) {
        p_148833_1_.handleScoreboardObjective(this);
    }

    public String func_149339_c() {
        return this.field_149343_a;
    }

    public String func_149337_d() {
        return this.field_149341_b;
    }

    public int func_149338_e() {
        return this.field_149342_c;
    }

    public void processPacket(INetHandler p_148833_1_) {
        this.processPacket((INetHandlerPlayClient) p_148833_1_);
    }
}
