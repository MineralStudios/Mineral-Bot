package gg.mineral.bot.bukkit.plugin.injector

import com.github.retrooper.packetevents.util.reflection.ReflectionObject
import com.viaversion.viaversion.api.protocol.packet.State
import com.viaversion.viaversion.util.ReflectionUtil
import gg.mineral.bot.api.injector.BotChannelInjector
import io.github.retrooper.packetevents.util.SpigotReflectionUtil
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.*
import io.netty.channel.local.LocalAddress
import io.netty.channel.local.LocalServerChannel
import io.netty.channel.socket.ServerSocketChannel
import io.netty.util.AttributeKey
import java.net.SocketAddress


class BukkitChannelInjector(
    override val address: SocketAddress = LocalAddress("Mineral-fake"),
    override val eventLoopGroup: EventLoopGroup = DefaultEventLoopGroup()
) :
    BotChannelInjector {
    private val lock = Object()

    private val connectionChannelsListIndex by lazy {
        val serverConnection = SpigotReflectionUtil.getMinecraftServerConnectionInstance()
        if (serverConnection != null) {
            val reflectServerConnection = ReflectionObject(serverConnection)
            //There should only be 2 lists.
            for (i in 0..1) {
                val list = reflectServerConnection.readList<Any>(i)
                for (value in list)
                    if (value is ChannelFuture)
                        return@lazy i
            }
            error("Unable to find connection channels list index.")
        } else error("Unable to find Minecraft server connection instance.")
    }

    override fun inject() {
        val serverConnection = SpigotReflectionUtil.getMinecraftServerConnectionInstance()
            ?: error("Unable to find Minecraft server connection instance.")

        val reflectServerConnection = ReflectionObject(serverConnection)
        val connectionChannelFutures = reflectServerConnection.readList<ChannelFuture>(connectionChannelsListIndex)

        val injected =
            connectionChannelFutures.any {
                it.channel().localAddress() == address
            }

        if (injected) uninject(false)

        // Find main server socket channel to clone
        val mainServerChannelFuture =
            connectionChannelFutures.firstOrNull { it.channel() is ServerSocketChannel }
                ?: error("Unable to find main server channel future.")

        // Pipeline from the main server channel
        val channelPipeline = mainServerChannelFuture.channel().pipeline()

        val bootstrapAcceptor: ChannelHandler = channelPipeline.firstOrNull {
            try {
                ReflectionUtil.get(it.value, "childHandler", ChannelInitializer::class.java)
                true
            } catch (ignored: ReflectiveOperationException) {
                false
            }
        }?.value ?: channelPipeline.first() ?: error("Unable to find bootstrap acceptor.")

        val oldInitializer: ChannelInitializer<Channel> = getFieldSafe(
            bootstrapAcceptor, "childHandler"
        ) ?: error("Unable to find child handler in bootstrap acceptor.")

        // Derive channel initializer from the pipeline
        val channelInitializer = BukkitChannelInitializer(oldInitializer)

        // Add new bot local channel future to the list
        connectionChannelFutures.add(
            ServerBootstrap()
                .group(eventLoopGroup)
                .channel(LocalServerChannel::class.java)
                .localAddress(address)
                .childHandler(channelInitializer).bind().syncUninterruptibly()
        )

        // Write the list
        reflectServerConnection.writeList(connectionChannelsListIndex, connectionChannelFutures)
    }

    private inline fun <reified T> getFieldSafe(instance: Any, fieldName: String): T? {
        return try {
            val field = instance.javaClass.getDeclaredField(fieldName)
            field.isAccessible = true
            val value = field.get(instance)
            if (value is T) value else null
        } catch (e: NoSuchFieldException) {
            null
        } catch (e: SecurityException) {
            null
        } catch (e: IllegalAccessException) {
            null
        }
    }

    override fun uninject(fullShutdown: Boolean) {
        // Grab the underlying Minecraft ServerConnection instance
        val serverConnection = SpigotReflectionUtil.getMinecraftServerConnectionInstance()
            ?: error("Unable to find Minecraft server connection instance.")

        val reflectServerConnection = ReflectionObject(serverConnection)
        // Read out the current list of ChannelFutures
        val connectionChannelFutures = reflectServerConnection.readList<ChannelFuture>(connectionChannelsListIndex)

        // Iterate and remove any futures matching our fake local address
        val iterator = connectionChannelFutures.iterator()
        var removed = false
        while (iterator.hasNext()) {
            val future = iterator.next()
            if (future.channel().localAddress() == address) {
                // Close the channel
                try {
                    future.channel().close().syncUninterruptibly()
                } finally {
                    // Remove it from the list
                    iterator.remove()
                    removed = true
                }
            }
        }

        if (removed) {
            // Write the pruned list back into the server connection
            reflectServerConnection.writeList(connectionChannelsListIndex, connectionChannelFutures)
            // Shutdown the event loop group we created for bots
            if (fullShutdown)
                eventLoopGroup.shutdownGracefully()
        }
    }

    companion object {
        val CONNECTION_STATE: AttributeKey<State> = AttributeKey.valueOf("mineral_bot_connection_state")
    }

}