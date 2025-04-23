package gg.mineral.bot.base.client.network

import io.netty.channel.Channel
import io.netty.channel.ChannelHandlerContext
import net.minecraft.client.Minecraft
import net.minecraft.network.EnumConnectionState
import net.minecraft.network.NetworkManager

class ClientNetworkManager(mc: Minecraft, private val defaultConnectionState: EnumConnectionState) :
    NetworkManager(mc, true) {
    fun setConnectionState(state: EnumConnectionState, channel: Channel) {
        this.connectionState = channel.attr(attrKeyConnectionState).getAndSet(state)
        channel.attr(attrKeyReceivable).set(state.func_150757_a(true))
        channel.attr(attrKeySendable).set(state.func_150754_b(true))
        channel.config().setAutoRead(true)
        Minecraft.logger.debug("Enabled auto read")
    }

    override fun channelActive(ctx: ChannelHandlerContext?) {
        super.channelActive(ctx)
        setConnectionState(defaultConnectionState, ctx!!.channel())
    }
}