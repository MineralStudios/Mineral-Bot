package net.minecraft.network.status.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.ServerStatusResponse;
import net.minecraft.network.status.INetHandlerStatusClient;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumTypeAdapterFactory;
import net.minecraft.util.IChatComponent;

public class S00PacketServerInfo extends Packet {
    private static final Gson field_149297_a = (new GsonBuilder())
            .registerTypeAdapter(ServerStatusResponse.MinecraftProtocolVersionIdentifier.class,
                    new ServerStatusResponse.MinecraftProtocolVersionIdentifier.Serializer())
            .registerTypeAdapter(ServerStatusResponse.PlayerCountData.class,
                    new ServerStatusResponse.PlayerCountData.Serializer())
            .registerTypeAdapter(ServerStatusResponse.class, new ServerStatusResponse.Serializer())
            .registerTypeHierarchyAdapter(IChatComponent.class, new IChatComponent.Serializer())
            .registerTypeHierarchyAdapter(ChatStyle.class, new ChatStyle.Serializer())
            .registerTypeAdapterFactory(new EnumTypeAdapterFactory()).create();
    private ServerStatusResponse field_149296_b;

    public S00PacketServerInfo() {
    }

    public S00PacketServerInfo(ServerStatusResponse p_i45273_1_) {
        this.field_149296_b = p_i45273_1_;
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer p_148837_1_, Minecraft mc) throws IOException {
        this.field_149296_b = (ServerStatusResponse) field_149297_a.fromJson(p_148837_1_.readStringFromBuffer(32767),
                ServerStatusResponse.class);
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer p_148840_1_) throws IOException {
        p_148840_1_.writeStringToBuffer(field_149297_a.toJson(this.field_149296_b));
    }

    public void processPacket(INetHandlerStatusClient p_148833_1_) {
        p_148833_1_.handleServerInfo(this);
    }

    public ServerStatusResponse func_149294_c() {
        return this.field_149296_b;
    }

    /**
     * If true, the network manager will process the packet immediately when
     * received, otherwise it will queue it for
     * processing. Currently true for: Disconnect, LoginSuccess, KeepAlive,
     * ServerQuery/Info, Ping/Pong
     */
    public boolean hasPriority() {
        return true;
    }

    public void processPacket(INetHandler p_148833_1_) {
        this.processPacket((INetHandlerStatusClient) p_148833_1_);
    }
}
