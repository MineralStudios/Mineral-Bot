package net.minecraft.network.play.client;

import java.io.IOException;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.minecraft.client.Minecraft;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class C16PacketClientStatus extends Packet {
    private C16PacketClientStatus.EnumState state;

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer p_148837_1_, Minecraft mc) throws IOException {
        this.state = C16PacketClientStatus.EnumState.field_151404_e[p_148837_1_.readByte()
                % C16PacketClientStatus.EnumState.field_151404_e.length];
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
        p_148840_1_.writeByte(this.state.field_151403_d);
    }

    public void processPacket(INetHandlerPlayServer p_148833_1_) {
        p_148833_1_.processClientStatus(this);
    }

    public void processPacket(INetHandler p_148833_1_) {
        this.processPacket((INetHandlerPlayServer) p_148833_1_);
    }

    public static enum EnumState {
        PERFORM_RESPAWN("PERFORM_RESPAWN", 0, 0), REQUEST_STATS("REQUEST_STATS", 1,
                1), OPEN_INVENTORY_ACHIEVEMENT("OPEN_INVENTORY_ACHIEVEMENT", 2, 2);

        private final int field_151403_d;
        private static final C16PacketClientStatus.EnumState[] field_151404_e = new C16PacketClientStatus.EnumState[values().length];

        private static final C16PacketClientStatus.EnumState[] $VALUES = new C16PacketClientStatus.EnumState[] {
                PERFORM_RESPAWN, REQUEST_STATS, OPEN_INVENTORY_ACHIEVEMENT };

        private EnumState(String p_i45241_1_, int p_i45241_2_, int p_i45241_3_) {
            this.field_151403_d = p_i45241_3_;
        }

        static {
            C16PacketClientStatus.EnumState[] var0 = values();
            int var1 = var0.length;

            for (int var2 = 0; var2 < var1; ++var2) {
                C16PacketClientStatus.EnumState var3 = var0[var2];
                field_151404_e[var3.field_151403_d] = var3;
            }
        }
    }
}
