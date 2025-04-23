package gg.mineral.bot.engine.plugin.injector

import gg.mineral.bot.api.injector.BotChannelInjector
import io.netty.channel.DefaultEventLoopGroup
import io.netty.channel.EventLoopGroup
import io.netty.channel.local.LocalAddress
import java.net.SocketAddress

class MineralChannelInjector(
    override val address: SocketAddress = LocalAddress("Mineral-fake"),
    override val eventLoopGroup: EventLoopGroup = DefaultEventLoopGroup()
) :
    BotChannelInjector {
    override fun inject() {
    }

    override fun uninject(fullShutdown: Boolean) {
    }
}