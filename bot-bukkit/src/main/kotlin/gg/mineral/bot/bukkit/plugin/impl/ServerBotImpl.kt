package gg.mineral.bot.bukkit.plugin.impl

import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.event.PacketListenerPriority
import com.github.retrooper.packetevents.protocol.nbt.NBTCompound
import com.github.retrooper.packetevents.protocol.player.GameMode
import com.github.retrooper.packetevents.protocol.world.Difficulty
import com.github.retrooper.packetevents.protocol.world.WorldBlockPosition
import com.github.retrooper.packetevents.protocol.world.dimension.DimensionTypeRef
import com.github.retrooper.packetevents.resources.ResourceLocation
import com.github.retrooper.packetevents.util.Vector3i
import com.github.retrooper.packetevents.wrapper.PacketWrapper
import com.github.retrooper.packetevents.wrapper.login.server.WrapperLoginServerLoginSuccess
import com.github.retrooper.packetevents.wrapper.play.server.*
import com.google.common.collect.HashMultimap
import com.mojang.authlib.GameProfile
import gg.mineral.bot.api.configuration.BotConfiguration
import gg.mineral.bot.api.math.ServerLocation
import gg.mineral.bot.api.util.dsl.onComplete
import gg.mineral.bot.base.client.BotImpl
import gg.mineral.bot.base.client.instance.ConnectedClientInstance
import gg.mineral.bot.base.client.manager.InstanceManager.instances
import gg.mineral.bot.base.client.manager.InstanceManager.pendingInstances
import gg.mineral.bot.base.client.network.ClientLoginHandler
import gg.mineral.bot.bukkit.plugin.MineralBotPlugin
import gg.mineral.bot.bukkit.plugin.impl.player.NMSServerPlayer
import gg.mineral.bot.bukkit.plugin.injector.BukkitChannelInjector
import gg.mineral.bot.bukkit.plugin.injector.BukkitInjectedListener
import gg.mineral.bot.bukkit.plugin.netty.PostViaHandler
import gg.mineral.bot.bukkit.plugin.netty.PreViaHandler
import gg.mineral.bot.impl.thread.ThreadManager
import io.netty.channel.Channel
import io.netty.channel.local.LocalChannel
import net.minecraft.network.EnumConnectionState
import net.minecraft.network.handshake.client.C00Handshake
import net.minecraft.network.login.client.C00PacketLoginStart
import net.minecraft.server.v1_8_R3.EnumProtocol
import net.minecraft.server.v1_8_R3.MinecraftServer
import net.minecraft.server.v1_8_R3.NetworkManager
import net.minecraft.server.v1_8_R3.PlayerConnection
import org.bukkit.Bukkit
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer
import java.io.File
import java.lang.ref.WeakReference
import java.net.Proxy
import java.util.*


class ServerBotImpl : BotImpl() {

    private fun Channel.sendPacket(wrapper: PacketWrapper<*>) =
        PacketEvents.getAPI().protocolManager.sendPacket(this, wrapper)

    override fun spawn(
        configuration: BotConfiguration,
        location: ServerLocation
    ): WeakReference<gg.mineral.bot.api.instance.ClientInstance> {
        val startTime = System.nanoTime() / 1000000

        val uuid = configuration.uuid

        if (instances.containsKey(uuid) || pendingInstances.containsKey(uuid) || Bukkit.getPlayer(uuid) != null) {
            val newUUID = UUID.randomUUID()
            Bukkit.getLogger().warning("Instance already spawned for $uuid, reassigning uuid to $newUUID")
            configuration.uuid = newUUID
        }

        val file = configuration.runDirectory

        if (!file.exists()) file.mkdirs()

        val name = configuration.fullUsername

        val instance = ConnectedClientInstance(
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
        ) {
            it.networkManager.apply {
                netHandler = ClientLoginHandler(
                    this,
                    it
                )

                scheduleOutboundPacket(
                    C00Handshake(5, injector.address.toString(), Bukkit.getPort(), EnumConnectionState.LOGIN)
                )

                scheduleOutboundPacket(
                    C00PacketLoginStart(GameProfile(configuration.uuid, name))
                )
            }
        }

        instance.setServer(injector.address.toString(), Bukkit.getServer().port)

        val serverSide = NMSServerPlayer(
            location.world,
            configuration.uuid, name, configuration.skin.value,
            configuration.skin.signature,
            disableEntityCollisions = configuration.disableEntityCollisions
        ).apply {
            setPosition(location.x, location.y, location.z)
            setYawPitch(location.yaw, location.pitch)
            spawnInWorld(location.world)
        }

        pendingInstances[configuration.uuid] = instance

        instance.channel.onComplete {
            onConnect(it.getOrThrow(), configuration, serverSide, instance)
        }

        spawnRecords.add(SpawnRecord(configuration.username, (System.nanoTime() / 1000000) - startTime))

        return WeakReference(instance)
    }

    private fun onConnect(
        channel: Channel,
        configuration: BotConfiguration,
        serverSide: NMSServerPlayer,
        instance: ConnectedClientInstance
    ) {
        val localAddress = channel.localAddress()

        listener.onceChannelRegistered(localAddress) { serverChannel ->
            val pipeline = serverChannel.pipeline()
            val serverNetworkManager =
                pipeline.get("packet_handler") as NetworkManager?
                    ?: error("Unable to find server network manager for address $localAddress")

            pipeline.addAfter("splitter", "mineral_bot_pre_via", PreViaHandler {
                it.sendPacket(
                    WrapperLoginServerLoginSuccess(
                        configuration.uuid,
                        configuration.fullUsername
                    )
                )

                serverNetworkManager.a(EnumProtocol.PLAY)
                serverSide.playerConnection = PlayerConnection(
                    MinecraftServer.getServer(), serverNetworkManager,
                    serverSide
                )

                onLoginStart(it, serverSide)
            })

            pipeline.addBefore("decoder", "mineral_bot_post_via", PostViaHandler())

            ThreadManager.asyncExecutor.execute {
                try {
                    instance.apply {
                        this.run()
                        pendingInstances.remove(configuration.uuid)
                        instances[configuration.uuid] = this
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun onLoginStart(serverChannel: Channel, serverSide: NMSServerPlayer) {

        val player = serverSide.bukkitEntity

        serverChannel.sendPacket(
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
                    ResourceLocation(player.world.name),
                    serverSide.worldSpawn[0],
                    serverSide.worldSpawn[1],
                    serverSide.worldSpawn[2]
                ),
                0, /* portal cooldown */
            )
        )

        Bukkit.getScheduler().runTask(
            MineralBotPlugin.instance
        ) { onJoin(serverChannel, serverSide) }
    }

    private fun onJoin(serverChannel: Channel, serverSide: NMSServerPlayer) {
        val spawn = serverSide.worldSpawn
        val player = serverSide.bukkitEntity
        serverSide.initializeGameMode()

        serverSide.sendSupportedChannels()

        serverChannel.sendPacket(
            WrapperPlayServerDifficulty(
                Difficulty.getById(serverSide.difficultyId),
                serverSide.isDifficultyLocked
            )
        )

        serverChannel.sendPacket(
            WrapperPlayServerSpawnPosition(
                Vector3i(
                    spawn[0],
                    spawn[1],
                    spawn[2]
                )
            )
        )

        val abilities = serverSide.abilities

        serverChannel.sendPacket(
            WrapperPlayServerPlayerAbilities(
                abilities.isInvulnerable,
                abilities.isFlying,
                abilities.canFly,
                player.gameMode == org.bukkit.GameMode.CREATIVE,
                abilities.flySpeed,
                abilities.walkSpeed
            )
        )

        serverChannel.sendPacket(
            WrapperPlayServerHeldItemChange(
                serverSide.itemInHandIndex
            )
        )

        serverSide.sendScoreboard()
        serverSide.resetPlayerSampleUpdateTimer()

        serverSide.onJoin()

        serverSide.callSpawnEvents()

        // Send location to client
        serverSide.sendLocationToClient()

        // World border, time, weather
        serverSide.initWorld()

        serverSide.initResourcePack()

        serverSide.syncInventory()
    }

    override fun cleanup() {
        Bukkit.getScheduler().runTask(
            MineralBotPlugin.instance
        ) {
            for (player in Bukkit.getOnlinePlayers()) {
                if ((player as CraftPlayer).handle.playerConnection.networkManager.channel.let { it is LocalChannel || it == null } && !isFakePlayer(
                        player.uniqueId
                    )) player.kickPlayer(
                    "Despawned"
                )
            }
        }
    }

    companion object {
        private val injector = BukkitChannelInjector()
        private val listener = BukkitInjectedListener()

        fun init() {
            val apiInstance = ServerBotImpl()
            INSTANCE = apiInstance
            PacketEvents.getAPI().eventManager.registerListener(listener, PacketListenerPriority.HIGHEST)
            injector.inject()
        }

        fun destroy() {
            injector.uninject()
        }
    }
}
