package net.minecraft.network.play.client;

import java.io.IOException;

import lombok.Getter;
import lombok.NoArgsConstructor;
import net.minecraft.client.Minecraft;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

@NoArgsConstructor
@Getter
public class C0EPacketClickWindow extends Packet {
    private int windowId;
    private int slot;
    private int button;
    private short actionNumber;
    private ItemStack clickedItem;
    private int mode;

    public C0EPacketClickWindow(int p_i45246_1_, int p_i45246_2_, int p_i45246_3_, int p_i45246_4_,
            ItemStack p_i45246_5_, short p_i45246_6_) {
        this.windowId = p_i45246_1_;
        this.slot = p_i45246_2_;
        this.button = p_i45246_3_;
        this.clickedItem = p_i45246_5_ != null ? p_i45246_5_.copy() : null;
        this.actionNumber = p_i45246_6_;
        this.mode = p_i45246_4_;
    }

    public void processPacket(INetHandlerPlayServer p_148833_1_) {
        p_148833_1_.processClickWindow(this);
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer p_148837_1_, Minecraft mc) throws IOException {
        this.windowId = p_148837_1_.readByte();
        this.slot = p_148837_1_.readShort();
        this.button = p_148837_1_.readByte();
        this.actionNumber = p_148837_1_.readShort();
        this.mode = p_148837_1_.readByte();
        this.clickedItem = p_148837_1_.readItemStackFromBuffer();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
        p_148840_1_.writeByte(this.windowId);
        p_148840_1_.writeShort(this.slot);
        p_148840_1_.writeByte(this.button);
        p_148840_1_.writeShort(this.actionNumber);
        p_148840_1_.writeByte(this.mode);
        p_148840_1_.writeItemStackToBuffer(this.clickedItem);
    }

    /**
     * Returns a string formatted as comma separated [field]=[value] values. Used by
     * Minecraft for logging purposes.
     */
    public String serialize() {
        return this.clickedItem != null
                ? String.format("id=%d, slot=%d, button=%d, type=%d, itemid=%d, itemcount=%d, itemaux=%d",
                        new Object[] { Integer.valueOf(this.windowId), Integer.valueOf(this.slot),
                                Integer.valueOf(this.button), Integer.valueOf(this.mode),
                                Integer.valueOf(Item.getIdFromItem(this.clickedItem.getItem())),
                                Integer.valueOf(this.clickedItem.stackSize),
                                Integer.valueOf(this.clickedItem.getItemDamage()) })
                : String.format("id=%d, slot=%d, button=%d, type=%d, itemid=-1",
                        new Object[] { Integer.valueOf(this.windowId), Integer.valueOf(this.slot),
                                Integer.valueOf(this.button), Integer.valueOf(this.mode) });
    }

    public void processPacket(INetHandler p_148833_1_) {
        this.processPacket((INetHandlerPlayServer) p_148833_1_);
    }
}
