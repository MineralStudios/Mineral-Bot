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
public class C10PacketCreativeInventoryAction extends Packet {
    private int slot;
    private ItemStack clickedItem;

    public C10PacketCreativeInventoryAction(int p_i45263_1_, ItemStack p_i45263_2_) {
        this.slot = p_i45263_1_;
        this.clickedItem = p_i45263_2_ != null ? p_i45263_2_.copy() : null;
    }

    public void processPacket(INetHandlerPlayServer p_148833_1_) {
        p_148833_1_.processCreativeInventoryAction(this);
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer p_148837_1_, Minecraft mc) throws IOException {
        this.slot = p_148837_1_.readShort();
        this.clickedItem = p_148837_1_.readItemStackFromBuffer();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
        p_148840_1_.writeShort(this.slot);
        p_148840_1_.writeItemStackToBuffer(this.clickedItem);
    }

    public void processPacket(INetHandler p_148833_1_) {
        this.processPacket((INetHandlerPlayServer) p_148833_1_);
    }
}
