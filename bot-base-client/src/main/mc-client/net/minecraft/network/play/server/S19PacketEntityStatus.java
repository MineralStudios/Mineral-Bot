package net.minecraft.network.play.server;

import java.io.IOException;

import gg.mineral.bot.api.entity.ClientEntity;
import gg.mineral.bot.api.packet.play.clientbound.EntityStatusPacket;
import gg.mineral.bot.api.world.ClientWorld;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.world.World;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class S19PacketEntityStatus extends Packet implements EntityStatusPacket {
    private int entityId;
    private byte status;

    public S19PacketEntityStatus(Entity p_i46335_1_, byte p_i46335_2_) {
        this.entityId = p_i46335_1_.getEntityId();
        this.status = p_i46335_2_;
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer p_148837_1_, Minecraft mc) throws IOException {
        this.entityId = p_148837_1_.readInt();
        this.status = p_148837_1_.readByte();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
        p_148840_1_.writeInt(this.entityId);
        p_148840_1_.writeByte(this.status);
    }

    public void processPacket(INetHandlerPlayClient p_148833_1_) {
        p_148833_1_.handleEntityStatus(this);
    }

    public Entity getEntity(World p_149161_1_) {
        return p_149161_1_.getEntityByID(this.entityId);
    }

    public void processPacket(INetHandler p_148833_1_) {
        this.processPacket((INetHandlerPlayClient) p_148833_1_);
    }

    @Override
    public ClientEntity getEntity(ClientWorld world) {
        return world.getEntityByID(this.entityId);
    }

    @Override
    public void setEntity(ClientEntity entity) {
        this.entityId = entity.getEntityId();
    }
}
