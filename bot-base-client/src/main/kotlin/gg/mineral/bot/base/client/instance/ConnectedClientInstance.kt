package gg.mineral.bot.base.client.instance

import com.google.common.collect.Multimap
import gg.mineral.bot.api.BotAPI
import gg.mineral.bot.api.configuration.BotConfiguration
import gg.mineral.bot.api.injector.BotChannelInjector
import gg.mineral.bot.base.client.gui.GuiConnecting
import gg.mineral.bot.base.client.netty.ClientChannelInitializer
import gg.mineral.bot.base.client.network.ClientNetworkManager
import io.netty.bootstrap.Bootstrap
import io.netty.channel.local.LocalChannel
import kotlinx.coroutines.*
import net.minecraft.client.gui.GuiScreen
import net.minecraft.network.EnumConnectionState
import java.io.File
import java.net.Proxy

class ConnectedClientInstance(
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
    assetIndex: String,
    private val injector: BotChannelInjector,
    val connectFunction: (ConnectedClientInstance) -> Unit
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
    val networkManager = ClientNetworkManager(this, EnumConnectionState.HANDSHAKING)
    var disconnected = false
    val channel: Deferred<LocalChannel> = scope.async {
        val channel = Bootstrap()
            .group(injector.eventLoopGroup)
            .channel(LocalChannel::class.java)
            .handler(ClientChannelInitializer(this@ConnectedClientInstance, networkManager))
            .connect(injector.address).sync()
            .channel() as LocalChannel

        channel.config().setAutoRead(true)

        channel.closeFuture().addListener {
            if (disconnected) return@addListener
            disconnected = true
            BotAPI.INSTANCE.despawn(configuration.uuid)
        }
        return@async channel
    }

    override fun displayGuiScreen(guiScreen: GuiScreen?) {
        if (guiScreen is GuiConnecting) guiScreen.connectFunction =
            GuiConnecting.ConnectFunction { _: String?, _: Int ->
                guiScreen.networkManager = networkManager
                connectFunction(this)
            }

        super.displayGuiScreen(guiScreen)
    }

    companion object {
        val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    }
}