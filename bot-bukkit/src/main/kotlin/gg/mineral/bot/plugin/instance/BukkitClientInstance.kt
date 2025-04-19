package gg.mineral.bot.plugin.instance

import com.google.common.collect.Multimap
import gg.mineral.bot.api.configuration.BotConfiguration
import gg.mineral.bot.base.client.gui.GuiConnecting
import gg.mineral.bot.base.client.instance.ClientInstance
import io.netty.channel.Channel
import io.netty.channel.ChannelHandlerContext
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen
import net.minecraft.network.EnumConnectionState
import net.minecraft.network.NetworkManager
import java.io.File
import java.net.Proxy

class BukkitClientInstance(
    configuration: BotConfiguration,
    width: Int,
    height: Int,
    fullscreen: Boolean,
    demo: Boolean,
    gameDir: File,
    assetsDir: File,
    resourcePackDir: File,
    proxy: Proxy,
    version: String,
    userProperties: Multimap<*, *>,
    assetIndex: String
) : ClientInstance(
    configuration,
    width,
    height,
    fullscreen,
    demo,
    gameDir,
    assetsDir,
    resourcePackDir,
    proxy,
    version,
    userProperties,
    assetIndex
) {
    class BotNetworkManager(mc: Minecraft) : NetworkManager(mc, true) {
        fun setConnectionState(state: EnumConnectionState, channel: Channel) {
            this.connectionState = channel.attr(attrKeyConnectionState).getAndSet(state)
            channel.attr(attrKeyReceivable).set(state.func_150757_a(true))
            channel.attr(attrKeySendable).set(state.func_150754_b(true))
            channel.config().setAutoRead(true)
            logger.debug("Enabled auto read")
        }

        override fun channelActive(ctx: ChannelHandlerContext?) {
            super.channelActive(ctx)
            setConnectionState(EnumConnectionState.PLAY, ctx!!.channel())
        }
    }

    val networkManager = BotNetworkManager(this)

    override fun displayGuiScreen(guiScreen: GuiScreen?) {
        if (guiScreen is GuiConnecting) guiScreen.connectFunction =
            GuiConnecting.ConnectFunction { _: String?, _: Int ->
                guiScreen.networkManager = networkManager
            }

        super.displayGuiScreen(guiScreen)
    }
}