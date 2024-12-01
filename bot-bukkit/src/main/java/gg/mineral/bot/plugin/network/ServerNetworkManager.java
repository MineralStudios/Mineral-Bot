package gg.mineral.bot.plugin.network;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import gg.mineral.bot.base.client.instance.ClientInstance;
import gg.mineral.bot.plugin.network.packet.Server2ClientTranslator;
import gg.mineral.server.combat.BacktrackSystem;
import io.netty.channel.ChannelHandlerContext;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import lombok.val;
import net.minecraft.client.Minecraft;
import net.minecraft.server.v1_8_R3.EnumProtocolDirection;
import net.minecraft.server.v1_8_R3.NetworkManager;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import net.minecraft.server.v1_8_R3.PacketPlayInKeepAlive;
import net.minecraft.server.v1_8_R3.PlayerConnection;

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

        if (mc instanceof ClientInstance instance && (!mc.isMainThread() || instance.getLatency() > 0))
            instance.scheduleTask(() -> translator.handlePacket(packet),
                    instance.getLatency());
        else
            translator.handlePacket(packet);

    }

    @SuppressWarnings("unchecked")
    @Override
    public void a(ChannelHandlerContext channelhandlercontext, @SuppressWarnings("rawtypes") Packet packet)
            throws Exception {
        val packetListener = this.getPacketListener();

        if (packetListener instanceof PlayerConnection playerConnection) {
            val player = playerConnection.player;
            val backtrackSystem = player.getBacktrackSystem();
            int currentDelay = backtrackSystem.isEnabled() && packet instanceof PacketPlayInFlying
                    ? backtrackSystem.getCurrentDelay()
                    : 0;

            // System.out.println("Receiving packet with delay: " + currentDelay + "ms");

            if ((currentDelay > 0 || !backtrackSystem.getPacketReadTasks().isEmpty())
                    && !(packet instanceof PacketPlayInKeepAlive))
                backtrackSystem.getPacketReadTasks()
                        .add(new BacktrackSystem.PacketRecieveTask(packet, playerConnection,
                                System.currentTimeMillis() + currentDelay));
            else
                packet.a(playerConnection);
        }
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

    @SuppressWarnings("unchecked")
    @Override
    public void a(@SuppressWarnings("rawtypes") Packet packet,
            GenericFutureListener<? extends Future<? super Void>> genericfuturelistener,
            GenericFutureListener<? extends Future<? super Void>>... agenericfuturelistener) {
        handle(packet);
    }

}
