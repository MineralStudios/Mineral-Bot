package net.minecraft.network.play.client;

import java.io.IOException;

import lombok.Getter;
import lombok.NoArgsConstructor;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

@NoArgsConstructor
@Getter
public class C08PacketPlayerBlockPlacement extends Packet {
    private int x, y, z;
    private int direction;
    private ItemStack heldItem;
    private float cursorX, cursorY, cursorZ;

    public C08PacketPlayerBlockPlacement(int p_i45265_1_, int p_i45265_2_, int p_i45265_3_, int p_i45265_4_,
            ItemStack p_i45265_5_, float p_i45265_6_, float p_i45265_7_, float p_i45265_8_) {
        this.x = p_i45265_1_;
        this.y = p_i45265_2_;
        this.z = p_i45265_3_;
        this.direction = p_i45265_4_;
        this.heldItem = p_i45265_5_ != null ? p_i45265_5_.copy() : null;
        this.cursorX = p_i45265_6_;
        this.cursorY = p_i45265_7_;
        this.cursorZ = p_i45265_8_;
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer p_148837_1_, Minecraft mc) throws IOException {
        this.x = p_148837_1_.readInt();
        this.y = p_148837_1_.readUnsignedByte();
        this.z = p_148837_1_.readInt();
        this.direction = p_148837_1_.readUnsignedByte();
        this.heldItem = p_148837_1_.readItemStackFromBuffer();
        this.cursorX = (float) p_148837_1_.readUnsignedByte() / 16.0F;
        this.cursorY = (float) p_148837_1_.readUnsignedByte() / 16.0F;
        this.cursorZ = (float) p_148837_1_.readUnsignedByte() / 16.0F;
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
        p_148840_1_.writeInt(this.x);
        p_148840_1_.writeByte(this.y);
        p_148840_1_.writeInt(this.z);
        p_148840_1_.writeByte(this.direction);
        p_148840_1_.writeItemStackToBuffer(this.heldItem);
        p_148840_1_.writeByte((int) (this.cursorX * 16.0F));
        p_148840_1_.writeByte((int) (this.cursorY * 16.0F));
        p_148840_1_.writeByte((int) (this.cursorZ * 16.0F));
    }

    public void processPacket(INetHandlerPlayServer p_148833_1_) {
        p_148833_1_.processPlayerBlockPlacement(this);
    }

    public void processPacket(INetHandler p_148833_1_) {
        this.processPacket((INetHandlerPlayServer) p_148833_1_);
    }
}
