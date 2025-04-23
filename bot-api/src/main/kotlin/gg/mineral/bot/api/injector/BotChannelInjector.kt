package gg.mineral.bot.api.injector

import io.netty.channel.EventLoopGroup
import java.net.SocketAddress

interface BotChannelInjector {
    /**
     * The address to connect to.
     */
    val address: SocketAddress

    /**
     * The event loop group to use for the channel.
     */
    val eventLoopGroup: EventLoopGroup

    /**
     * Injects into the channel.
     */
    fun inject()

    /**
     * Uninjects from the channel.
     */
    fun uninject(fullShutdown: Boolean = true)
}