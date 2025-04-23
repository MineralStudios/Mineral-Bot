package gg.mineral.bot.bukkit.plugin.injector

import com.viaversion.viaversion.platform.WrappedChannelInitializer
import io.netty.channel.Channel
import io.netty.channel.ChannelInitializer
import java.lang.reflect.Method

class BukkitChannelInitializer(private val oldInit: ChannelInitializer<Channel>) :
    ChannelInitializer<Channel>(),
    WrappedChannelInitializer {
    companion object {
        val INIT_CHANNEL_METHOD: Method =
            ChannelInitializer::class.java.getDeclaredMethod("initChannel", Channel::class.java)
                .apply { isAccessible = true }
    }

    override fun initChannel(channel: Channel) {
        INIT_CHANNEL_METHOD.invoke(oldInit, channel)
    }

    override fun original() = oldInit
}