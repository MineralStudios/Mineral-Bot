package gg.mineral.bot.plugin.impl

import com.google.common.collect.HashMultimap
import gg.mineral.bot.api.configuration.BotConfiguration
import gg.mineral.bot.api.math.ServerLocation
import gg.mineral.bot.base.client.BotImpl
import gg.mineral.bot.base.client.gui.GuiConnecting
import gg.mineral.bot.base.client.instance.ClientInstance
import gg.mineral.bot.base.client.manager.InstanceManager
import gg.mineral.bot.base.client.network.ClientNetHandler
import gg.mineral.bot.impl.thread.ThreadManager
import gg.mineral.bot.plugin.impl.player.NMSServerPlayer
import gg.mineral.bot.plugin.network.ClientNetworkManager
import gg.mineral.bot.plugin.network.ServerNetworkManager
import gg.mineral.bot.plugin.network.packet.Client2ServerTranslator
import gg.mineral.bot.plugin.network.packet.Server2ClientTranslator
import lombok.Getter
import net.minecraft.client.gui.GuiScreen
import net.minecraft.server.v1_8_R3.*
import net.minecraft.server.v1_8_R3.WorldSettings.EnumGamemode
import org.bukkit.Bukkit
import org.bukkit.event.Listener
import java.io.File
import java.net.Proxy
import java.util.*
import kotlin.math.min

class ServerBotImpl : BotImpl(), Listener {
    override fun spawn(configuration: BotConfiguration, location: ServerLocation): ClientInstance {
        val startTime = System.nanoTime() / 1000000
        val file = configuration.runDirectory

        if (!file.exists()) file.mkdirs()

        val c2sTranslator = Client2ServerTranslator()
        val s2cTranslator = Server2ClientTranslator()

        val instance = getClientInstance(configuration, file)

        val name = configuration.fullUsername

        val serverSide = NMSServerPlayer(
            location.world,
            configuration.uuid, name, configuration.skin.value,
            configuration.skin.signature
        )

        serverSide.setPosition(location.x, location.y, location.z)
        serverSide.setYawPitch(location.yaw, location.pitch)
        serverSide.spawnInWorld(location.world)

        val cNetworkManager = ClientNetworkManager(
            c2sTranslator,
            instance
        )
        val sNetworkManager = ServerNetworkManager(s2cTranslator, instance)

        val netHandlerPlayClient = ClientNetHandler(
            instance, null,
            cNetworkManager
        )

        s2cTranslator.netHandlerPlayClient = netHandlerPlayClient

        val playerConnection: PlayerConnection = object : PlayerConnection(
            MinecraftServer.getServer(), sNetworkManager,
            serverSide
        ) {
            @Getter
            private var disconnected = false

            override fun disconnect(s: String) {
                if (disconnected) return
                disconnected = true
                despawn(configuration.uuid)
                // TODO: cancellable kick event
                super.disconnect(s)
            }

            // TODO: Temporary fix for the issue with the player's ping
            override fun a(packetplayinkeepalive: PacketPlayInKeepAlive) {
                player.ping = instance.latency
            }
        }

        c2sTranslator.playerConnection = playerConnection

        serverSide.callSpawnEvents()

        playerConnection.sendPacket(
            PacketPlayOutLogin(
                serverSide.id,
                EnumGamemode.getById(serverSide.gameModeId), serverSide.isWorldHardcore,
                serverSide.dimensionId, EnumDifficulty.getById(serverSide.difficultyId),
                min(serverSide.maxPlayers.toDouble(), 60.0).toInt(),
                WorldType.getType(serverSide.worldTypeName),
                serverSide.isReducedDebugInfo
            )
        )

        val spawn = serverSide.worldSpawn
        serverSide.initializeGameMode()

        serverSide.sendSupportedChannels()

        // playerConnection.sendPacket(new PacketPlayOutCustomPayload("MC|Brand",
        // new PacketDataSerializer(
        // Unpooled.wrappedBuffer(ByteUtil.stringToBytes(serverSide.getServerModName())))));
        playerConnection.sendPacket(
            PacketPlayOutServerDifficulty(
                EnumDifficulty.getById(serverSide.difficultyId), serverSide.isDifficultyLocked
            )
        )

        playerConnection.sendPacket(PacketPlayOutSpawnPosition(BlockPosition(spawn[0], spawn[1], spawn[2])))

        playerConnection.sendPacket(PacketPlayOutAbilities(serverSide.abilities))

        playerConnection.sendPacket(PacketPlayOutHeldItemSlot(serverSide.itemInHandIndex))

        serverSide.sendScoreboard()
        serverSide.resetPlayerSampleUpdateTimer()

        serverSide.onJoin()

        // Send location to client
        serverSide.sendLocationToClient()

        // World border, time, weather
        serverSide.initWorld()

        serverSide.initResourcePack()

        // serverSide.getEffectPackets().forEach(packet ->
        // getServerConnection().queuePacket(packet));
        serverSide.syncInventory()

        InstanceManager.pendingInstances[configuration.uuid] = instance

        ThreadManager.asyncExecutor.execute {
            try {
                instance.run()
                InstanceManager.pendingInstances.remove(configuration.uuid)
                InstanceManager.instances[configuration.uuid] = instance
                MinecraftServer.getServer()
                    .postToMainThread { sNetworkManager.releasePacketQueue() }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        spawnRecords.add(SpawnRecord(configuration.username, (System.nanoTime() / 1000000) - startTime))

        return instance
    }

    override fun despawn(uuid: UUID): Boolean {
        val isBot = super.despawn(uuid)

        if (isBot) {
            val player = Bukkit.getPlayer(uuid)
            player?.kickPlayer("Despawned")
        }

        return isBot
    }

    companion object {
        fun init() {
            INSTANCE = ServerBotImpl()
        }

        private fun getClientInstance(configuration: BotConfiguration, file: File): ClientInstance {
            val instance: ClientInstance = object : ClientInstance(
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
                override fun displayGuiScreen(guiScreen: GuiScreen) {
                    if (guiScreen is GuiConnecting) guiScreen.connectFunction =
                        GuiConnecting.ConnectFunction { _: String?, _: Int -> }

                    super.displayGuiScreen(guiScreen)
                }
            }
            instance.setServer("127.0.0.1", Bukkit.getServer().port)
            return instance
        }
    }
}
