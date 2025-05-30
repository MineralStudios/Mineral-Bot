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
public class S03PacketTimeUpdate extends Packet {
    private long field_149369_a;
    private long field_149368_b;

    public S03PacketTimeUpdate(long p_i45230_1_, long p_i45230_3_, boolean p_i45230_5_) {
        this.field_149369_a = p_i45230_1_;
        this.field_149368_b = p_i45230_3_;

        if (!p_i45230_5_) {
            this.field_149368_b = -this.field_149368_b;

            if (this.field_149368_b == 0L) {
                this.field_149368_b = -1L;
            }
        }
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer p_148837_1_, Minecraft mc) throws IOException {
        this.field_149369_a = p_148837_1_.readLong();
        this.field_149368_b = p_148837_1_.readLong();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
        p_148840_1_.writeLong(this.field_149369_a);
        p_148840_1_.writeLong(this.field_149368_b);
    }

    public void processPacket(INetHandlerPlayClient p_148833_1_) {
        p_148833_1_.handleTimeUpdate(this);
    }

    /**
     * Returns a string formatted as comma separated [field]=[value] values. Used by
     * Minecraft for logging purposes.
     */
    public String serialize() {
        return String.format("time=%d,dtime=%d",
                new Object[] { Long.valueOf(this.field_149369_a), Long.valueOf(this.field_149368_b) });
    }

    public long func_149366_c() {
        return this.field_149369_a;
    }

    public long func_149365_d() {
        return this.field_149368_b;
    }

    public void processPacket(INetHandler p_148833_1_) {
        this.processPacket((INetHandlerPlayClient) p_148833_1_);
    }
}
