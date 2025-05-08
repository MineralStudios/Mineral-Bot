package gg.mineral.bot.bukkit.plugin.impl.player

import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerPlayerAbilities
import gg.mineral.bot.api.entity.living.player.ServerPlayer
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerInitialSpawnEvent
import org.spigotmc.event.player.PlayerSpawnLocationEvent

abstract class BukkitServerPlayer<T> : ServerPlayer {
    abstract val abilities: WrapperPlayServerPlayerAbilities
    abstract val entityPlayer: T
    abstract var playerConnection: Any
    abstract val bukkitPlayer: Player

    override fun callSpawnEvents() {
        Bukkit.getPluginManager().callEvent(
            PlayerInitialSpawnEvent(
                bukkitPlayer, bukkitPlayer.location
            )
        )
        Bukkit.getPluginManager().callEvent(PlayerSpawnLocationEvent(bukkitPlayer, bukkitPlayer.location))
    }

    abstract fun syncInventory()
}