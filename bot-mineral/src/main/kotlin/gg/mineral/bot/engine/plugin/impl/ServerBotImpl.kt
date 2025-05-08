package gg.mineral.bot.engine.plugin.impl

import com.google.common.collect.HashMultimap
import gg.mineral.api.network.packet.rw.ByteWriter
import gg.mineral.bot.api.concurrent.ListenableFuture
import gg.mineral.bot.api.configuration.BotConfiguration
import gg.mineral.bot.api.instance.ClientInstance
import gg.mineral.bot.api.math.ServerLocation
import gg.mineral.bot.api.util.dsl.onComplete
import gg.mineral.bot.base.client.BotImpl
import gg.mineral.bot.base.client.concurrent.ListenableFutureImpl
import gg.mineral.bot.base.client.instance.ConnectedClientInstance
import gg.mineral.bot.base.client.manager.InstanceManager
import gg.mineral.bot.base.client.network.ClientLoginHandler
import gg.mineral.bot.engine.plugin.injector.MineralChannelInjector
import gg.mineral.bot.impl.thread.ThreadManager
import io.netty.buffer.Unpooled
import net.minecraft.network.EnumConnectionState
import java.io.File
import java.net.Proxy


class ServerBotImpl : BotImpl(), ByteWriter {
    companion object {
        private val injector = MineralChannelInjector()

        fun init() {
            INSTANCE = ServerBotImpl()
        }
    }

    override fun spawn(configuration: BotConfiguration, location: ServerLocation): ListenableFuture<ClientInstance> {
        val startTime = System.nanoTime() / 1000000
        val file = configuration.runDirectory

        if (!file.exists()) file.mkdirs()

        val instance =
            ConnectedClientInstance(
                configuration, 1280, 720,
                fullscreen = false,
                demo = false,
                file,
                File(file, "assets"),
                File(file, "resourcepacks"),
                Proxy.NO_PROXY,
                "Mineral-Bot-Client", HashMultimap.create<Any, Any>(),
                "1.7.10",
                injector
            ) {}

        val serverPacketHandler = instance.networkManager

        InstanceManager.pendingInstances[configuration.uuid] = instance
        instance.setServer(injector.address.toString(), 25565)

        instance.channel.onComplete { result ->
            val channel = result.getOrThrow()
            val netHandlerLoginClient = ClientLoginHandler(
                serverPacketHandler,
                instance, null
            )

            fun EnumConnectionState.id() = func_150759_c()

            serverPacketHandler
                .setNetHandler(
                    netHandlerLoginClient
                )
            serverPacketHandler.setConnectionState(EnumConnectionState.LOGIN, channel)

            val protocolVersion = 5
            val stateId = EnumConnectionState.LOGIN.id()
            val length1 =
                1 + getVarIntSize(protocolVersion) + getVarIntSize(injector.address.toString().length) + injector.address.toString().length + 2 + getVarIntSize(
                    stateId
                )
            val length2 =
                1 + getVarIntSize(configuration.username.length) + configuration.username.length + 16 + 8 + 8 + 8 + 4 + 4
            val byteBuf = Unpooled.buffer(getVarIntSize(length1) + length1 + getVarIntSize(length2) + length2)
            byteBuf.writeVarInt(length1)
            byteBuf.writeVarInt(0x00)
            byteBuf.writeVarInt(5)
            byteBuf.writeString(injector.address.toString())
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

            ThreadManager.asyncExecutor.execute {
                try {
                    instance.run()
                    InstanceManager.pendingInstances.remove(configuration.uuid)
                    InstanceManager.instances[configuration.uuid] = instance
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            spawnRecords.add(SpawnRecord(configuration.username, (System.nanoTime() / 1000000) - startTime))
        }

        return ListenableFutureImpl(instance as ClientInstance).apply { complete() }
    }

    override fun cleanup() {

    }
}