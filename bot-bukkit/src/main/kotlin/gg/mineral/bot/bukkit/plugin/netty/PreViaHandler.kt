package gg.mineral.bot.bukkit.plugin.netty

import com.viaversion.viaversion.api.connection.UserConnection
import com.viaversion.viaversion.api.protocol.packet.State
import com.viaversion.viaversion.bukkit.handlers.BukkitEncodeHandler
import gg.mineral.bot.bukkit.plugin.injector.BukkitChannelInjector
import io.netty.channel.Channel
import io.netty.channel.ChannelDuplexHandler
import io.netty.channel.ChannelHandlerContext


class PreViaHandler(private val onLogin: (Channel) -> Unit) : ChannelDuplexHandler() {

    private fun getUserConnection(channel: Channel): UserConnection? {
        val encoder = channel.pipeline().get(BukkitEncodeHandler::class.java)
        return encoder?.connection()
    }

    override fun channelRead(ctx: ChannelHandlerContext, buf: Any) {
        val channel = ctx.channel()
        val userConnection =
            getUserConnection(channel) ?: error("Unable to get UserConnection from channel pipeline.")

        val connectionState = userConnection.protocolInfo.serverState

        channel.attr(BukkitChannelInjector.CONNECTION_STATE).set(connectionState)

        if (connectionState == State.LOGIN) onLogin(ctx.channel())

        super.channelRead(ctx, buf)
    }
}