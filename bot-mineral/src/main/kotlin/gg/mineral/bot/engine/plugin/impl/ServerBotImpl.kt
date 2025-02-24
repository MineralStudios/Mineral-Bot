package gg.mineral.bot.engine.plugin.impl

import com.google.common.collect.HashMultimap
import gg.mineral.api.network.channel.MineralChannelInitializer
import gg.mineral.api.network.packet.rw.ByteWriter
import gg.mineral.bot.api.configuration.BotConfiguration
import gg.mineral.bot.api.instance.ClientInstance
import gg.mineral.bot.api.math.ServerLocation
import gg.mineral.bot.base.client.BotImpl
import gg.mineral.bot.base.client.gui.GuiConnecting
import gg.mineral.bot.base.client.manager.InstanceManager
import gg.mineral.bot.base.client.netty.LatencySimulatorHandler
import gg.mineral.bot.base.client.network.ClientNetHandler
import gg.mineral.bot.impl.thread.ThreadManager
import io.netty.bootstrap.Bootstrap
import io.netty.buffer.Unpooled
import io.netty.channel.Channel
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.local.LocalAddress
import io.netty.channel.local.LocalChannel
import io.netty.channel.local.LocalEventLoopGroup
import io.netty.handler.timeout.ReadTimeoutHandler
import net.minecraft.client.Minecraft
import net.minecraft.client.Minecraft.logger
import net.minecraft.client.gui.GuiScreen
import net.minecraft.client.network.NetHandlerLoginClient
import net.minecraft.network.EnumConnectionState
import net.minecraft.network.NetworkManager
import net.minecraft.util.MessageDeserializer
import net.minecraft.util.MessageDeserializer2
import net.minecraft.util.MessageSerializer
import net.minecraft.util.MessageSerializer2
import java.io.File
import java.lang.ref.WeakReference
import java.net.Proxy


class ServerBotImpl : BotImpl(), ByteWriter {
    companion object {
        val fakeGroup = LocalEventLoopGroup()
        val fakeAddress = LocalAddress("Mineral-fake")

        fun init() {
            INSTANCE = ServerBotImpl()
        }
    }

    class ClientChannelInitializer(
        private val mc: Minecraft,
        private val packetHandler: NetworkManager = NetworkManager(mc, true)
    ) : MineralChannelInitializer() {
        override fun initChannel(channel: Channel) {
            channel.pipeline().addLast("timeout", ReadTimeoutHandler(20))
                .addLast("latency_simulator", LatencySimulatorHandler(mc))
                .addLast("splitter", MessageDeserializer2())
                .addLast("decoder", MessageDeserializer(NetworkManager.field_152462_h, mc))
                .addLast("prepender", MessageSerializer2())
                .addLast("encoder", MessageSerializer(NetworkManager.field_152462_h))
                .addLast("packet_handler", packetHandler)
        }
    }

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
            setConnectionState(EnumConnectionState.LOGIN)
        }
    }

    override fun spawn(configuration: BotConfiguration, location: ServerLocation): WeakReference<ClientInstance> {
        val startTime = System.nanoTime() / 1000000
        val file = configuration.runDirectory

        if (!file.exists()) file.mkdirs()

        lateinit var serverPacketHandler: NetworkManager

        val instance: gg.mineral.bot.base.client.instance.ClientInstance =
            object : gg.mineral.bot.base.client.instance.ClientInstance(
                configuration, 1280, 720,
                false,
                false,
                file,
                File(file, "assets"),
                File(file, "resourcepacks"),
                Proxy.NO_PROXY,
                "Mineral-Bot-Client", HashMultimap.create<Any, Any>(),
                "1.7.10"
            ) {
                override fun displayGuiScreen(guiScreen: GuiScreen?) {
                    if (guiScreen is GuiConnecting) guiScreen.connectFunction =
                        GuiConnecting.ConnectFunction { _: String?, _: Int ->
                            guiScreen.networkManager = serverPacketHandler
                        }

                    super.displayGuiScreen(guiScreen)
                }
            }

        serverPacketHandler = BotNetworkManager(instance)

        InstanceManager.pendingInstances[configuration.uuid] = instance
        instance.setServer(fakeAddress.id(), 25565)

        val channel = Bootstrap()
            .group(fakeGroup)
            .channel(LocalChannel::class.java)
            .handler(ClientChannelInitializer(instance, serverPacketHandler)).connect(fakeAddress).sync().channel()

        val netHandlerLoginClient = object : NetHandlerLoginClient(
            serverPacketHandler,
            instance, null
        ) {
            override fun onConnectionStateTransition(
                enumConnectionState: EnumConnectionState,
                connectionState: EnumConnectionState
            ) {
                if (connectionState === EnumConnectionState.PLAY) networkManager.netHandler =
                    ClientNetHandler(
                        this.mc, this.guiScreen,
                        serverPacketHandler
                    )
            }
        }

        fun EnumConnectionState.id() = func_150759_c()

        serverPacketHandler
            .setNetHandler(
                netHandlerLoginClient
            )
        serverPacketHandler.setConnectionState(EnumConnectionState.LOGIN, channel)

        val protcolVersion = 5
        val stateId = EnumConnectionState.LOGIN.id()
        val length1 =
            1 + getVarIntSize(protcolVersion) + getVarIntSize(fakeAddress.id().length) + fakeAddress.id().length + 2 + getVarIntSize(
                stateId
            )
        val length2 =
            1 + getVarIntSize(configuration.username.length) + configuration.username.length + 16 + 8 + 8 + 8 + 4 + 4
        val byteBuf = Unpooled.buffer(getVarIntSize(length1) + length1 + getVarIntSize(length2) + length2)
        byteBuf.writeVarInt(length1)
        byteBuf.writeVarInt(0x00)
        byteBuf.writeVarInt(5)
        byteBuf.writeString(fakeAddress.id())
        byteBuf.writeShort(25565)
        byteBuf.writeVarInt(EnumConnectionState.LOGIN.id())
        byteBuf.writeVarInt(length2)
        byteBuf.writeVarInt(0x02)
        byteBuf.writeString(configuration.username)
        byteBuf.writeUuid(configuration.uuid)
        byteBuf.writeDouble(location.x)
        byteBuf.writeDouble(location.y)
        byteBuf.writeDouble(location.z)
        byteBuf.writeFloat(location.yaw)
        byteBuf.writeFloat(location.pitch)

        channel.unsafe().write(byteBuf, channel.voidPromise())
        channel.unsafe().flush()

        ThreadManager.gameLoopExecutor.execute {
            try {
                instance.run()
                InstanceManager.pendingInstances.remove(configuration.uuid)
                InstanceManager.instances[configuration.uuid] = instance
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        spawnRecords.add(SpawnRecord(configuration.username, (System.nanoTime() / 1000000) - startTime))

        return WeakReference(instance)
    }
}