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

@NoArgsConstructor
@Getter
public class C0BPacketEntityAction extends Packet {
    private int entityId, actionId, jumpBoost;

    public C0BPacketEntityAction(Entity p_i45259_1_, int p_i45259_2_) {
        this(p_i45259_1_, p_i45259_2_, 0);
    }

    public C0BPacketEntityAction(Entity p_i45260_1_, int p_i45260_2_, int p_i45260_3_) {
        this.entityId = p_i45260_1_.getEntityId();
        this.actionId = p_i45260_2_;
        this.jumpBoost = p_i45260_3_;
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer p_148837_1_, Minecraft mc) throws IOException {
        this.entityId = p_148837_1_.readInt();
        this.actionId = p_148837_1_.readByte();
        this.jumpBoost = p_148837_1_.readInt();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
        p_148840_1_.writeInt(this.entityId);
        p_148840_1_.writeByte(this.actionId);
        p_148840_1_.writeInt(this.jumpBoost);
    }

    public void processPacket(INetHandlerPlayServer p_148833_1_) {
        p_148833_1_.processEntityAction(this);
    }

    public void processPacket(INetHandler p_148833_1_) {
        this.processPacket((INetHandlerPlayServer) p_148833_1_);
    }
}
