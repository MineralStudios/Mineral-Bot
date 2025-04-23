package gg.mineral.bot.bukkit.plugin.injector

import com.github.retrooper.packetevents.event.PacketListener
import com.github.retrooper.packetevents.event.UserConnectEvent
import com.github.retrooper.packetevents.event.UserDisconnectEvent
import com.github.retrooper.packetevents.netty.channel.ChannelHelper
import io.github.retrooper.packetevents.injector.handlers.PacketEventsDecoder
import io.netty.channel.Channel
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.ChannelInboundHandlerAdapter
import java.net.SocketAddress
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList


class BukkitInjectedListener : PacketListener {
    private val serverChannels = ConcurrentHashMap<SocketAddress, Channel>()
    private val oneShotListeners = ConcurrentHashMap<SocketAddress, MutableList<(Channel) -> Unit>>()
    private val lock = Any()

    fun onceChannelRegistered(addr: SocketAddress, callback: (Channel) -> Unit) {
        synchronized(lock) {
            serverChannels[addr]?.let {
                callback(it)
                return
            }

            oneShotListeners
                .computeIfAbsent(addr) { CopyOnWriteArrayList() }
                .add(callback)
        }
    }

    override fun onUserConnect(event: UserConnectEvent) {
        val channel = event.user.channel as? Channel ?: error("Unable to get channel from user.")
        val key = ChannelHelper.remoteAddress(channel)
        val decodeListenerHandler = object : ChannelInboundHandlerAdapter() {
            override fun handlerAdded(ctx: ChannelHandlerContext) {
                val pipeline = ctx.pipeline()

                ctx.executor().execute {
                    if (pipeline.get(PacketEventsDecoder::class.java) != null) {
                        synchronized(lock) {
                            serverChannels[key] = channel
                            oneShotListeners.remove(key)?.forEach {
                                it(channel)
                            }
                            pipeline.remove(this)
                        }
                    }
                }
            }
        }
        channel.pipeline().addLast(decodeListenerHandler)
    }

    override fun onUserDisconnect(event: UserDisconnectEvent) {
        val channel = event.user.channel as? Channel ?: error("Unable to get channel from user.")
        val key = ChannelHelper.remoteAddress(channel)
        synchronized(lock) {
            serverChannels.remove(key)
            oneShotListeners.remove(key)
        }
    }
}