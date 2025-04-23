package gg.mineral.bot.bukkit.plugin.netty

import com.viaversion.viaversion.api.protocol.packet.State
import gg.mineral.bot.bukkit.plugin.injector.BukkitChannelInjector
import io.netty.channel.ChannelDuplexHandler
import io.netty.channel.ChannelHandlerContext


class PostViaHandler : ChannelDuplexHandler() {

    override fun channelRead(ctx: ChannelHandlerContext, buf: Any) {
        val channel = ctx.channel()

        val connectionState = channel.attr(BukkitChannelInjector.CONNECTION_STATE).get()
            ?: error("Unable to get connection state from channel.")

        val isHandshake = connectionState == State.HANDSHAKE
        val isLogin = connectionState == State.LOGIN

        if (isHandshake || isLogin) return

        super.channelRead(ctx, buf)
    }
}