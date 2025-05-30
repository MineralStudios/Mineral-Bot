package net.minecraft.network.play.server;

import java.io.IOException;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.minecraft.client.Minecraft;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.scoreboard.Score;

@AllArgsConstructor
@NoArgsConstructor
public class S3CPacketUpdateScore extends Packet {
    private String field_149329_a = "";
    private String field_149327_b = "";
    private int field_149328_c;
    private int field_149326_d;

    public S3CPacketUpdateScore(Score p_i45227_1_, int p_i45227_2_) {
        this.field_149329_a = p_i45227_1_.getPlayerName();
        this.field_149327_b = p_i45227_1_.func_96645_d().getName();
        this.field_149328_c = p_i45227_1_.getScorePoints();
        this.field_149326_d = p_i45227_2_;
    }

    public S3CPacketUpdateScore(String p_i45228_1_) {
        this.field_149329_a = p_i45228_1_;
        this.field_149327_b = "";
        this.field_149328_c = 0;
        this.field_149326_d = 1;
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer p_148837_1_, Minecraft mc) throws IOException {
        this.field_149329_a = p_148837_1_.readStringFromBuffer(16);
        this.field_149326_d = p_148837_1_.readByte();

        if (this.field_149326_d != 1) {
            this.field_149327_b = p_148837_1_.readStringFromBuffer(16);
            this.field_149328_c = p_148837_1_.readInt();
        }
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
        p_148840_1_.writeStringToBuffer(this.field_149329_a);
        p_148840_1_.writeByte(this.field_149326_d);

        if (this.field_149326_d != 1) {
            p_148840_1_.writeStringToBuffer(this.field_149327_b);
            p_148840_1_.writeInt(this.field_149328_c);
        }
    }

    public void processPacket(INetHandlerPlayClient p_148833_1_) {
        p_148833_1_.handleUpdateScore(this);
    }

    public String func_149324_c() {
        return this.field_149329_a;
    }

    public String func_149321_d() {
        return this.field_149327_b;
    }

    public int func_149323_e() {
        return this.field_149328_c;
    }

    public int func_149322_f() {
        return this.field_149326_d;
    }

    public void processPacket(INetHandler p_148833_1_) {
        this.processPacket((INetHandlerPlayClient) p_148833_1_);
    }
}
