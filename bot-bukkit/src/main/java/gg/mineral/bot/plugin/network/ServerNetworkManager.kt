package gg.mineral.bot.plugin.network

import gg.mineral.bot.base.client.instance.ClientInstance
import gg.mineral.bot.plugin.network.packet.Server2ClientTranslator
import gg.mineral.server.combat.BacktrackSystem.PacketRecieveTask
import io.netty.channel.ChannelHandlerContext
import io.netty.util.concurrent.Future
import io.netty.util.concurrent.GenericFutureListener
import net.minecraft.server.v1_8_R3.*
import java.lang.ref.WeakReference
import java.util.*
import java.util.concurrent.ConcurrentLinkedQueue

class ServerNetworkManager(
    private val translator: Server2ClientTranslator,
    private val clientInstance: WeakReference<ClientInstance>
) :
    NetworkManager(EnumProtocolDirection.SERVERBOUND) {
    private var started = false

    private val packetQueue: Queue<Packet<*>> = ConcurrentLinkedQueue()

    fun releasePacketQueue() {
        started = true
        var packet: Packet<*>
        while ((packetQueue.poll().also { packet = it }) != null) handle(packet)
    }

    @Suppress("UNCHECKED_CAST")
    override fun handle(packet: Packet<*>) {
        if (!started) {
            packetQueue.add(packet)
            return
        }

        clientInstance.get()?.let {
            if (!it.isMainThread() || it.latency > 0) it.scheduleTask(
                { translator.handlePacket(packet as Packet<PacketListenerPlayOut>) },
                it.latency.toLong()
            )
        }
    }

    @Throws(Exception::class)
    @Suppress("UNCHECKED_CAST")
    override fun a(channelhandlercontext: ChannelHandlerContext?, packet: Packet<*>) {
        val packetListener = this.packetListener

        if (packetListener is PlayerConnection) {
            val player = packetListener.player
            val backtrackSystem = player.backtrackSystem
            val currentDelay = if (backtrackSystem.isEnabled && packet is PacketPlayInFlying)
                backtrackSystem.currentDelay
            else
                0

            // System.out.println("Receiving packet with delay: " + currentDelay + "ms");
            if ((currentDelay > 0 || !backtrackSystem.packetReadTasks.isEmpty())
                && packet !is PacketPlayInKeepAlive
            ) backtrackSystem.packetReadTasks
                .add(
                    PacketRecieveTask(
                        packet as Packet<PacketListenerPlayIn>, packetListener,
                        System.currentTimeMillis() + currentDelay
                    )
                )
            else (packet as Packet<PacketListenerPlayIn>).a(packetListener)
        }
    }

    override fun isConnected(): Boolean {
        return true
    }

    override fun g(): Boolean {
        return true
    }

    override fun k() {
    }

    override fun a(
        packet: Packet<*>,
        genericfuturelistener: GenericFutureListener<out Future<in Void>>,
        vararg agenericfuturelistener: GenericFutureListener<out Future<in Void>>
    ) {
        handle(packet)
    }
}
