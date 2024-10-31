package gg.mineral.bot.plugin.network;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import gg.mineral.bot.base.client.instance.ClientInstance;
import gg.mineral.bot.plugin.network.packet.Server2ClientTranslator;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import net.minecraft.client.Minecraft;

import net.minecraft.server.v1_8_R3.EnumProtocol;
import net.minecraft.server.v1_8_R3.EnumProtocolDirection;
import net.minecraft.server.v1_8_R3.NetworkManager;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayOutLogin;

public class ServerNetworkManager extends NetworkManager {

    private boolean started = false;

    private final Server2ClientTranslator translator;
    private final Minecraft mc;
    private final Queue<Packet<?>> packetQueue = new ConcurrentLinkedQueue<>();

    public ServerNetworkManager(Server2ClientTranslator translator, Minecraft mc) {
        super(EnumProtocolDirection.SERVERBOUND);
        this.translator = translator;
        this.mc = mc;
    }

    public void releasePacketQueue() {
        started = true;
        Packet<?> packet;
        while ((packet = packetQueue.poll()) != null)
            handle(packet);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void handle(@SuppressWarnings("rawtypes") Packet packet) {
        if (!started) {
            packetQueue.add(packet);
            return;
        }

        if (packet instanceof PacketPlayOutLogin || protocol != EnumProtocol.PLAY) {
            translator.handlePacket(packet);
            return;
        }

        if (mc instanceof ClientInstance instance && (!mc.isMainThread() || instance.getLatency() > 0))
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
