package gg.mineral.bot.plugin.network

import gg.mineral.bot.base.client.instance.ClientInstance
import gg.mineral.bot.plugin.network.packet.Client2ServerTranslator
import io.netty.channel.ChannelHandlerContext
import io.netty.util.concurrent.GenericFutureListener
import net.minecraft.client.Minecraft
import net.minecraft.network.EnumConnectionState
import net.minecraft.network.NetworkManager
import net.minecraft.network.Packet
import net.minecraft.util.IChatComponent
import javax.crypto.SecretKey

class ClientNetworkManager(private val translator: Client2ServerTranslator, mc: Minecraft) :
    NetworkManager(mc, true) {
    private var open = true

    @Throws(Exception::class)
    override fun channelActive(p_channelActive_1_: ChannelHandlerContext) {
        this.setConnectionState(EnumConnectionState.HANDSHAKING)
    }

    override fun setConnectionState(state: EnumConnectionState) {
        this.connectionState = state
    }

    override fun channelRead0(p_channelRead0_1_: ChannelHandlerContext, p_channelRead0_2_: Packet) {
        if (p_channelRead0_2_.hasPriority()) p_channelRead0_2_.processPacket(this.getNetHandler())
        else receivedPacketsQueue.add(p_channelRead0_2_)
    }

    override fun scheduleOutboundPacket(
        p_150725_1_: Packet,
        vararg p_150725_2_: GenericFutureListener<*>
    ) {
        this.flushOutboundQueue()
        this.dispatchPacket(p_150725_1_, p_150725_2_)
    }

    override fun dispatchPacket(
        packet: Packet,
        p_150732_2_: Array<out GenericFutureListener<*>>
    ) {
        val newState = EnumConnectionState.func_150752_a(packet)
        val currState = this.connectionState

        if (newState !== currState) this.setConnectionState(newState)

        if (mc is ClientInstance && (!mc.isMainThread || (mc as ClientInstance).latency > 0)) (mc as ClientInstance).scheduleTask(
            { packet.processPacket(translator) }, (mc as ClientInstance).latency.toLong()
        )
        else packet.processPacket(translator)
    }

    override fun flushOutboundQueue() {
        while (!outboundPacketsQueue.isEmpty()) {
            val packet = outboundPacketsQueue
                .poll()
            this.dispatchPacket(packet.field_150774_a, packet.field_150773_b)
        }
    }

    override fun processReceivedPackets() {
        this.flushOutboundQueue()
        val currConnectionState = this.connectionState

        if (this.connectionState !== currConnectionState) {
            if (this.connectionState != null) netHandler.onConnectionStateTransition(
                this.connectionState,
                currConnectionState
            )

            this.connectionState = currConnectionState
        }

        if (this.netHandler != null) {
            var iterations = 1000
            while (!receivedPacketsQueue.isEmpty() && iterations >= 0) {
                val packet = receivedPacketsQueue.poll()
                packet.processPacket(this.netHandler)
                --iterations
            }

            netHandler.onNetworkTick()
        }

        // this.channel.flush();
    }

    override fun closeChannel(p_150718_1_: IChatComponent) {
        if (mc is ClientInstance) mc.shutdown()
        this.open = false
        this.terminationReason = p_150718_1_
    }

    override fun isLocalChannel(): Boolean {
        return false
    }

    override fun enableEncryption(p_150727_1_: SecretKey) {
        this.encryptionEnabled = true
    }

    override fun isChannelOpen(): Boolean {
        return open
    }

    override fun disableAutoRead() {
        // TODO: simulate auto read
    }
}
