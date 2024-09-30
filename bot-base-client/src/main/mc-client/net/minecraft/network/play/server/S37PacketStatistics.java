package net.minecraft.network.play.server;

import com.google.common.collect.Maps;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import net.minecraft.client.Minecraft;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;

@AllArgsConstructor
@NoArgsConstructor
public class S37PacketStatistics extends Packet {
    private Object2IntOpenHashMap<StatBase> field_148976_a;

    public void processPacket(INetHandlerPlayClient p_148833_1_) {
        p_148833_1_.handleStatistics(this);
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer p_148837_1_, Minecraft mc) throws IOException {
        int var2 = p_148837_1_.readVarIntFromBuffer();
        this.field_148976_a = new Object2IntOpenHashMap<>();

        for (int var3 = 0; var3 < var2; ++var3) {
            StatBase var4 = StatList.func_151177_a(p_148837_1_.readStringFromBuffer(32767));
            int var5 = p_148837_1_.readVarIntFromBuffer();

            if (var4 != null) {
                this.field_148976_a.put(var4, var5);
            }
        }
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
        p_148840_1_.writeVarIntToBuffer(this.field_148976_a.size());
        Iterator<it.unimi.dsi.fastutil.objects.Object2IntMap.Entry<StatBase>> var2 = this.field_148976_a
                .object2IntEntrySet().iterator();

        while (var2.hasNext()) {
            it.unimi.dsi.fastutil.objects.Object2IntMap.Entry<StatBase> var3 = var2.next();
            p_148840_1_.writeStringToBuffer(var3.getKey().statId);
            p_148840_1_.writeVarIntToBuffer(var3.getIntValue());
        }
    }

    /**
     * Returns a string formatted as comma separated [field]=[value] values. Used by
     * Minecraft for logging purposes.
     */
    public String serialize() {
        return String.format("count=%d", new Object[] { Integer.valueOf(this.field_148976_a.size()) });
    }

    public Object2IntOpenHashMap<StatBase> func_148974_c() {
        return this.field_148976_a;
    }

    public void processPacket(INetHandler p_148833_1_) {
        this.processPacket((INetHandlerPlayClient) p_148833_1_);
    }
}
