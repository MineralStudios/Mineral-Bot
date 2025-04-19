package gg.mineral.bot.plugin.impl

import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.protocol.nbt.NBTCompound
import com.github.retrooper.packetevents.protocol.player.ClientVersion
import com.github.retrooper.packetevents.protocol.player.GameMode
import com.github.retrooper.packetevents.protocol.world.Difficulty
import com.github.retrooper.packetevents.protocol.world.WorldBlockPosition
import com.github.retrooper.packetevents.protocol.world.dimension.DimensionTypeRef
import com.github.retrooper.packetevents.resources.ResourceLocation
import com.github.retrooper.packetevents.util.Vector3i
import com.github.retrooper.packetevents.wrapper.PacketWrapper
import com.github.retrooper.packetevents.wrapper.play.server.*
import com.google.common.collect.HashMultimap
import gg.mineral.bot.api.configuration.BotConfiguration
import gg.mineral.bot.api.math.ServerLocation
import gg.mineral.bot.base.client.BotImpl
import gg.mineral.bot.base.client.manager.InstanceManager
import gg.mineral.bot.base.client.netty.LatencySimulatorHandler
import gg.mineral.bot.base.client.network.ClientNetHandler
import gg.mineral.bot.impl.thread.ThreadManager
import gg.mineral.bot.plugin.impl.player.NMSServerPlayer
import gg.mineral.bot.plugin.instance.BukkitClientInstance
import io.netty.bootstrap.Bootstrap
import io.netty.channel.Channel
import io.netty.channel.ChannelInitializer
import io.netty.channel.DefaultEventLoopGroup
import io.netty.channel.local.LocalAddress
import io.netty.channel.local.LocalChannel
import io.netty.channel.local.LocalChannelUtil
import io.netty.handler.timeout.ReadTimeoutHandler
import net.minecraft.client.Minecraft
import net.minecraft.network.EnumConnectionState
import net.minecraft.network.NetworkManager
import net.minecraft.server.v1_8_R3.EnumProtocol
import net.minecraft.server.v1_8_R3.MinecraftServer
import net.minecraft.server.v1_8_R3.PacketPlayInKeepAlive
import net.minecraft.server.v1_8_R3.PlayerConnection
import net.minecraft.util.MessageDeserializer
import net.minecraft.util.MessageDeserializer2
import net.minecraft.util.MessageSerializer
import net.minecraft.util.MessageSerializer2
import org.bukkit.Bukkit
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import java.io.File
import java.lang.ref.WeakReference
import java.net.Proxy
import java.util.*

class ServerBotImpl : BotImpl(), Listener {
    class ClientChannelInitializer(
        private val mc: Minecraft,
        private val packetHandler: NetworkManager
    ) : ChannelInitializer<Channel>() {
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

    private fun Player.sendPacket(wrapper: PacketWrapper<*>) =
        PacketEvents.getAPI().playerManager.sendPacket(this, wrapper)

    override fun spawn(
        configuration: BotConfiguration,
        location: ServerLocation
    ): WeakReference<gg.mineral.bot.api.instance.ClientInstance> {
        val startTime = System.nanoTime() / 1000000
        val file = configuration.runDirectory

        if (!file.exists()) file.mkdirs()

        val name = configuration.fullUsername

        val serverSide = NMSServerPlayer(
            location.world,
            configuration.uuid, name, configuration.skin.value,
            configuration.skin.signature,
            disableEntityCollisions = configuration.disableEntityCollisions
        )

        val instance = BukkitClientInstance(
            configuration, 1280, 720,
            fullscreen = false,
            demo = false,
            file,
            File(file, "assets"),
            File(file, "resourcepacks"),
            Proxy.NO_PROXY,
            "Mineral-Bot-Client", HashMultimap.create<Any, Any>(),
            "1.7.10"
        )

        instance.setServer("127.0.0.1", Bukkit.getServer().port)

        val clientNetworkManager = instance.networkManager

        val channel = Bootstrap()
            .group(fakeGroup)
            .channel(LocalChannel::class.java)
            .handler(ClientChannelInitializer(instance, clientNetworkManager)).connect(fakeAddress).sync()
            .channel() as LocalChannel

        val remoteAddress = channel.remoteAddress()

        val serverChannel = LocalChannelUtil.get(remoteAddress)

        val serverNetworkManager =
            serverChannel!!.pipeline().get("packet_handler") as net.minecraft.server.v1_8_R3.NetworkManager

        serverNetworkManager.protocol = EnumProtocol.PLAY

        val netHandlerPlayClient = ClientNetHandler(
            instance, null,
            clientNetworkManager
        )

        clientNetworkManager.netHandler = netHandlerPlayClient

        clientNetworkManager.setConnectionState(EnumConnectionState.PLAY, channel)

        val instanceRef = WeakReference(instance)

        val playerConnection: PlayerConnection = object : PlayerConnection(
            MinecraftServer.getServer(), serverNetworkManager,
            serverSide
        ) {
            private var disconnected = false

            override fun disconnect(s: String) {
                if (disconnected) return
                disconnected = true
                despawn(configuration.uuid)
                // TODO: cancellable kick event
                super.disconnect(s)
            }

            override fun isDisconnected(): Boolean {
                return disconnected
            }

            // TODO: Temporary fix for the issue with the player's ping
            override fun a(packetplayinkeepalive: PacketPlayInKeepAlive) {
                player.ping = instanceRef.get()?.latency ?: 0
            }
        }

        serverSide.playerConnection = playerConnection


        val player = serverSide.bukkitEntity

        PacketEvents.getAPI().playerManager.getUser(player)?.clientVersion = ClientVersion.V_1_7_10


        serverSide.setPosition(location.x, location.y, location.z)
        serverSide.setYawPitch(location.yaw, location.pitch)
        serverSide.spawnInWorld(location.world)
        serverSide.callSpawnEvents()

        player.sendPacket(
            WrapperPlayServerJoinGame(
                serverSide.id,
                serverSide.isWorldHardcore,
                GameMode.getById(serverSide.gameModeId),
                GameMode.defaultGameMode(),
                Bukkit.getServer().worlds.map { it.name },
                NBTCompound(),
                DimensionTypeRef.IdRef(serverSide.dimensionId),
                Difficulty.getById(serverSide.difficultyId),
                player.world.name,
                player.world.seed,
                serverSide.maxPlayers,
                Bukkit.getServer().viewDistance,
                Bukkit.getServer().viewDistance, /* simulation distance */
                serverSide.isReducedDebugInfo,
                false, /*Respawn screen enabled*/
                false, /*is debug world*/
                false, /*is flat world*/
                WorldBlockPosition(
                    ResourceLocation(player.world.worldFolder.toString()),
                    serverSide.worldSpawn[0],
                    serverSide.worldSpawn[1],
                    serverSide.worldSpawn[2]
                ),
                0, /* portal cooldown */
            )
        )

        val spawn = serverSide.worldSpawn
        serverSide.initializeGameMode()

        serverSide.sendSupportedChannels()

        player.sendPacket(
            WrapperPlayServerDifficulty(
                Difficulty.getById(serverSide.difficultyId),
                serverSide.isDifficultyLocked
            )
        )

        player.sendPacket(
            WrapperPlayServerSpawnPosition(
                Vector3i(
                    spawn[0],
                    spawn[1],
                    spawn[2]
                )
            )
        )

        val abilities = serverSide.abilities

        player.sendPacket(
            WrapperPlayServerPlayerAbilities(
                abilities.isInvulnerable,
                abilities.isFlying,
                abilities.canFly,
                player.gameMode == org.bukkit.GameMode.CREATIVE,
                abilities.flySpeed,
                abilities.walkSpeed
            )
        )

        player.sendPacket(
            WrapperPlayServerHeldItemChange(
                serverSide.itemInHandIndex
            )
        )

        serverSide.sendScoreboard()
        serverSide.resetPlayerSampleUpdateTimer()

        serverSide.onJoin()

        // Send location to client
        serverSide.sendLocationToClient()

        // World border, time, weather
        serverSide.initWorld()

        serverSide.initResourcePack()

        serverSide.syncInventory()

        InstanceManager.pendingInstances[configuration.uuid] = instanceRef.get()!!

        ThreadManager.asyncExecutor.execute {
            try {
                instance.apply {
                    this.run()
                    InstanceManager.pendingInstances.remove(configuration.uuid)
                    InstanceManager.instances[configuration.uuid] = this
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        spawnRecords.add(SpawnRecord(configuration.username, (System.nanoTime() / 1000000) - startTime))

        return WeakReference(instance)
    }

    override fun despawn(uuid: UUID): Boolean {
        val isBot = super.despawn(uuid)

        if (isBot) {
            val player = Bukkit.getPlayer(uuid)
            player?.kickPlayer("Despawned")
        }

        return isBot
    }

    override fun cleanup() {
        for (player in Bukkit.getOnlinePlayers()) {
            if ((player as CraftPlayer).handle.playerConnection.networkManager.channel.let { it is LocalChannel || it == null } && !isFakePlayer(
                    player.uniqueId
                )) player.kickPlayer(
                "Despawned"
            )
        }
    }

    companion object {
        val fakeGroup = DefaultEventLoopGroup()
        val fakeAddress = LocalAddress("Mineral-fake")

        fun init() {
            INSTANCE = ServerBotImpl()
        }
    }
}
