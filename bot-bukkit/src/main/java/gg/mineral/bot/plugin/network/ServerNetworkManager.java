package gg.mineral.bot.plugin.network;

import gg.mineral.bot.base.client.player.FakePlayerInstance;
import gg.mineral.bot.plugin.network.packet.Server2ClientTranslator;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.client.Minecraft;
import net.minecraft.server.v1_8_R3.EnumProtocolDirection;
import net.minecraft.server.v1_8_R3.NetworkManager;
import net.minecraft.server.v1_8_R3.Packet;

public class ServerNetworkManager extends NetworkManager {

    private final Server2ClientTranslator translator;
    private final Minecraft mc;

    public ServerNetworkManager(Server2ClientTranslator translator, Minecraft mc) {
        super(EnumProtocolDirection.SERVERBOUND);
        this.translator = translator;
        this.mc = mc;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void handle(@SuppressWarnings("rawtypes") Packet packet) {
        if (mc instanceof FakePlayerInstance instance)
            instance.scheduleTask(() -> translator.handlePacket(packet), instance.getLatency());
        else
            translator.handlePacket(packet);

    }

    @Override
    public boolean isConnected() {
        return true;
    }

    @Override
    public boolean g() {
        return true;
    }

    @Override
    public void k() {
    }

    @Override
    public void a(@SuppressWarnings("rawtypes") Packet packet,
            GenericFutureListener<? extends Future<? super Void>> genericfuturelistener,
            @SuppressWarnings("unchecked") GenericFutureListener<? extends Future<? super Void>>... agenericfuturelistener) {
        handle(packet);
    }

}
