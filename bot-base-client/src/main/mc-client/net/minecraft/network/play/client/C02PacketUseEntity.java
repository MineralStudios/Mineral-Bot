package net.minecraft.network.play.client;

import java.io.IOException;

import lombok.Getter;
import lombok.NoArgsConstructor;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.world.World;

@Getter
@NoArgsConstructor
public class C02PacketUseEntity extends Packet {
    private int entityId;
    private C02PacketUseEntity.Action action;

    public C02PacketUseEntity(Entity p_i45251_1_, C02PacketUseEntity.Action p_i45251_2_) {
        this.entityId = p_i45251_1_.getEntityId();
        this.action = p_i45251_2_;
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer p_148837_1_, Minecraft mc) throws IOException {
        this.entityId = p_148837_1_.readInt();
        this.action = C02PacketUseEntity.Action.field_151421_c[p_148837_1_.readByte()
                % C02PacketUseEntity.Action.field_151421_c.length];
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
        p_148840_1_.writeInt(this.entityId);
        p_148840_1_.writeByte(this.action.field_151418_d);
    }

    public void processPacket(INetHandlerPlayServer p_148833_1_) {
        p_148833_1_.processUseEntity(this);
    }

    public Entity func_149564_a(World p_149564_1_) {
        return p_149564_1_.getEntityByID(this.entityId);
    }

    public void processPacket(INetHandler p_148833_1_) {
        this.processPacket((INetHandlerPlayServer) p_148833_1_);
    }

    public static enum Action {
        INTERACT("INTERACT", 0, 0), ATTACK("ATTACK", 1, 1);

        private static final C02PacketUseEntity.Action[] field_151421_c = new C02PacketUseEntity.Action[values().length];
        private final int field_151418_d;

        private static final C02PacketUseEntity.Action[] $VALUES = new C02PacketUseEntity.Action[] { INTERACT, ATTACK };

        private Action(String p_i45250_1_, int p_i45250_2_, int p_i45250_3_) {
            this.field_151418_d = p_i45250_3_;
        }

        static {
            C02PacketUseEntity.Action[] var0 = values();
            int var1 = var0.length;

            for (int var2 = 0; var2 < var1; ++var2) {
                C02PacketUseEntity.Action var3 = var0[var2];
                field_151421_c[var3.field_151418_d] = var3;
            }
        }
    }
}
