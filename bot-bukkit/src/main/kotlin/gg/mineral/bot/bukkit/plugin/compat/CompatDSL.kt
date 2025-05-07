package gg.mineral.bot.bukkit.plugin.compat

import com.github.retrooper.packetevents.PacketEvents
import com.github.retrooper.packetevents.manager.server.ServerVersion
import com.github.retrooper.packetevents.protocol.ConnectionState
import gg.mineral.bot.api.world.ServerWorld
import gg.mineral.bot.bukkit.plugin.impl.player.BukkitServerPlayer
import io.netty.channel.ChannelHandler
import java.util.*
import gg.mineral.bot.bukkit.plugin.compat.v1_8_R3.NMSServerPlayer as V1_8_8ServerPlayer

private val nmsVersion get() = PacketEvents.getAPI().serverManager.version

fun newBukkitServerPlayer(
    world: ServerWorld<*>,
    uuid: UUID,
    name: String,
    vararg skinData: String,
    disableEntityCollisions: Boolean = true
): BukkitServerPlayer<*> {
    return when (nmsVersion!!) {
        ServerVersion.V_1_8_8 -> V1_8_8ServerPlayer(
            world,
            uuid,
            name,
            *skinData,
            disableEntityCollisions = disableEntityCollisions
        )

        ServerVersion.V_1_7_10 -> TODO()
        ServerVersion.V_1_8 -> TODO()
        ServerVersion.V_1_8_3 -> TODO()
        ServerVersion.V_1_9 -> TODO()
        ServerVersion.V_1_9_1 -> TODO()
        ServerVersion.V_1_9_2 -> TODO()
        ServerVersion.V_1_9_4 -> TODO()
        ServerVersion.V_1_10 -> TODO()
        ServerVersion.V_1_10_1 -> TODO()
        ServerVersion.V_1_10_2 -> TODO()
        ServerVersion.V_1_11 -> TODO()
        ServerVersion.V_1_11_2 -> TODO()
        ServerVersion.V_1_12 -> TODO()
        ServerVersion.V_1_12_1 -> TODO()
        ServerVersion.V_1_12_2 -> TODO()
        ServerVersion.V_1_13 -> TODO()
        ServerVersion.V_1_13_1 -> TODO()
        ServerVersion.V_1_13_2 -> TODO()
        ServerVersion.V_1_14 -> TODO()
        ServerVersion.V_1_14_1 -> TODO()
        ServerVersion.V_1_14_2 -> TODO()
        ServerVersion.V_1_14_3 -> TODO()
        ServerVersion.V_1_14_4 -> TODO()
        ServerVersion.V_1_15 -> TODO()
        ServerVersion.V_1_15_1 -> TODO()
        ServerVersion.V_1_15_2 -> TODO()
        ServerVersion.V_1_16 -> TODO()
        ServerVersion.V_1_16_1 -> TODO()
        ServerVersion.V_1_16_2 -> TODO()
        ServerVersion.V_1_16_3 -> TODO()
        ServerVersion.V_1_16_4 -> TODO()
        ServerVersion.V_1_16_5 -> TODO()
        ServerVersion.V_1_17 -> TODO()
        ServerVersion.V_1_17_1 -> TODO()
        ServerVersion.V_1_18 -> TODO()
        ServerVersion.V_1_18_1 -> TODO()
        ServerVersion.V_1_18_2 -> TODO()
        ServerVersion.V_1_19 -> TODO()
        ServerVersion.V_1_19_1 -> TODO()
        ServerVersion.V_1_19_2 -> TODO()
        ServerVersion.V_1_19_3 -> TODO()
        ServerVersion.V_1_19_4 -> TODO()
        ServerVersion.V_1_20 -> TODO()
        ServerVersion.V_1_20_1 -> TODO()
        ServerVersion.V_1_20_2 -> TODO()
        ServerVersion.V_1_20_3 -> TODO()
        ServerVersion.V_1_20_4 -> TODO()
        ServerVersion.V_1_20_5 -> TODO()
        ServerVersion.V_1_20_6 -> TODO()
        ServerVersion.V_1_21 -> TODO()
        ServerVersion.V_1_21_1 -> TODO()
        ServerVersion.V_1_21_2 -> TODO()
        ServerVersion.V_1_21_3 -> TODO()
        ServerVersion.V_1_21_4 -> TODO()
        ServerVersion.ERROR -> TODO()
    }
}

fun newPlayerConnection(
    channelHandler: ChannelHandler,
    player: BukkitServerPlayer<*>,
): Any {
    return when (nmsVersion!!) {
        ServerVersion.V_1_8_8 -> net.minecraft.server.v1_8_R3.PlayerConnection(
            net.minecraft.server.v1_8_R3.MinecraftServer.getServer(),
            channelHandler as net.minecraft.server.v1_8_R3.NetworkManager,
            player.entityPlayer as net.minecraft.server.v1_8_R3.EntityPlayer
        )

        ServerVersion.V_1_7_10 -> TODO()
        ServerVersion.V_1_8 -> TODO()
        ServerVersion.V_1_8_3 -> TODO()
        ServerVersion.V_1_9 -> TODO()
        ServerVersion.V_1_9_1 -> TODO()
        ServerVersion.V_1_9_2 -> TODO()
        ServerVersion.V_1_9_4 -> TODO()
        ServerVersion.V_1_10 -> TODO()
        ServerVersion.V_1_10_1 -> TODO()
        ServerVersion.V_1_10_2 -> TODO()
        ServerVersion.V_1_11 -> TODO()
        ServerVersion.V_1_11_2 -> TODO()
        ServerVersion.V_1_12 -> TODO()
        ServerVersion.V_1_12_1 -> TODO()
        ServerVersion.V_1_12_2 -> TODO()
        ServerVersion.V_1_13 -> TODO()
        ServerVersion.V_1_13_1 -> TODO()
        ServerVersion.V_1_13_2 -> TODO()
        ServerVersion.V_1_14 -> TODO()
        ServerVersion.V_1_14_1 -> TODO()
        ServerVersion.V_1_14_2 -> TODO()
        ServerVersion.V_1_14_3 -> TODO()
        ServerVersion.V_1_14_4 -> TODO()
        ServerVersion.V_1_15 -> TODO()
        ServerVersion.V_1_15_1 -> TODO()
        ServerVersion.V_1_15_2 -> TODO()
        ServerVersion.V_1_16 -> TODO()
        ServerVersion.V_1_16_1 -> TODO()
        ServerVersion.V_1_16_2 -> TODO()
        ServerVersion.V_1_16_3 -> TODO()
        ServerVersion.V_1_16_4 -> TODO()
        ServerVersion.V_1_16_5 -> TODO()
        ServerVersion.V_1_17 -> TODO()
        ServerVersion.V_1_17_1 -> TODO()
        ServerVersion.V_1_18 -> TODO()
        ServerVersion.V_1_18_1 -> TODO()
        ServerVersion.V_1_18_2 -> TODO()
        ServerVersion.V_1_19 -> TODO()
        ServerVersion.V_1_19_1 -> TODO()
        ServerVersion.V_1_19_2 -> TODO()
        ServerVersion.V_1_19_3 -> TODO()
        ServerVersion.V_1_19_4 -> TODO()
        ServerVersion.V_1_20 -> TODO()
        ServerVersion.V_1_20_1 -> TODO()
        ServerVersion.V_1_20_2 -> TODO()
        ServerVersion.V_1_20_3 -> TODO()
        ServerVersion.V_1_20_4 -> TODO()
        ServerVersion.V_1_20_5 -> TODO()
        ServerVersion.V_1_20_6 -> TODO()
        ServerVersion.V_1_21 -> TODO()
        ServerVersion.V_1_21_1 -> TODO()
        ServerVersion.V_1_21_2 -> TODO()
        ServerVersion.V_1_21_3 -> TODO()
        ServerVersion.V_1_21_4 -> TODO()
        ServerVersion.ERROR -> TODO()
    }
}

fun ChannelHandler.setConnectionState(
    state: ConnectionState,
) {
    when (nmsVersion!!) {
        ServerVersion.V_1_8_8 -> (this as? net.minecraft.server.v1_8_R3.NetworkManager)?.a(
            net.minecraft.server.v1_8_R3.EnumProtocol.valueOf(
                state.name
            )
        )

        ServerVersion.V_1_7_10 -> TODO()
        ServerVersion.V_1_8 -> TODO()
        ServerVersion.V_1_8_3 -> TODO()
        ServerVersion.V_1_9 -> TODO()
        ServerVersion.V_1_9_1 -> TODO()
        ServerVersion.V_1_9_2 -> TODO()
        ServerVersion.V_1_9_4 -> TODO()
        ServerVersion.V_1_10 -> TODO()
        ServerVersion.V_1_10_1 -> TODO()
        ServerVersion.V_1_10_2 -> TODO()
        ServerVersion.V_1_11 -> TODO()
        ServerVersion.V_1_11_2 -> TODO()
        ServerVersion.V_1_12 -> TODO()
        ServerVersion.V_1_12_1 -> TODO()
        ServerVersion.V_1_12_2 -> TODO()
        ServerVersion.V_1_13 -> TODO()
        ServerVersion.V_1_13_1 -> TODO()
        ServerVersion.V_1_13_2 -> TODO()
        ServerVersion.V_1_14 -> TODO()
        ServerVersion.V_1_14_1 -> TODO()
        ServerVersion.V_1_14_2 -> TODO()
        ServerVersion.V_1_14_3 -> TODO()
        ServerVersion.V_1_14_4 -> TODO()
        ServerVersion.V_1_15 -> TODO()
        ServerVersion.V_1_15_1 -> TODO()
        ServerVersion.V_1_15_2 -> TODO()
        ServerVersion.V_1_16 -> TODO()
        ServerVersion.V_1_16_1 -> TODO()
        ServerVersion.V_1_16_2 -> TODO()
        ServerVersion.V_1_16_3 -> TODO()
        ServerVersion.V_1_16_4 -> TODO()
        ServerVersion.V_1_16_5 -> TODO()
        ServerVersion.V_1_17 -> TODO()
        ServerVersion.V_1_17_1 -> TODO()
        ServerVersion.V_1_18 -> TODO()
        ServerVersion.V_1_18_1 -> TODO()
        ServerVersion.V_1_18_2 -> TODO()
        ServerVersion.V_1_19 -> TODO()
        ServerVersion.V_1_19_1 -> TODO()
        ServerVersion.V_1_19_2 -> TODO()
        ServerVersion.V_1_19_3 -> TODO()
        ServerVersion.V_1_19_4 -> TODO()
        ServerVersion.V_1_20 -> TODO()
        ServerVersion.V_1_20_1 -> TODO()
        ServerVersion.V_1_20_2 -> TODO()
        ServerVersion.V_1_20_3 -> TODO()
        ServerVersion.V_1_20_4 -> TODO()
        ServerVersion.V_1_20_5 -> TODO()
        ServerVersion.V_1_20_6 -> TODO()
        ServerVersion.V_1_21 -> TODO()
        ServerVersion.V_1_21_1 -> TODO()
        ServerVersion.V_1_21_2 -> TODO()
        ServerVersion.V_1_21_3 -> TODO()
        ServerVersion.V_1_21_4 -> TODO()
        ServerVersion.ERROR -> TODO()
    }
}