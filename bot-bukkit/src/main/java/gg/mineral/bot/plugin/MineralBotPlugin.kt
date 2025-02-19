package gg.mineral.bot.plugin

import gg.mineral.bot.api.BotAPI
import gg.mineral.bot.base.client.tick.GameLoop
import gg.mineral.bot.impl.thread.ThreadManager.shutdown
import gg.mineral.bot.plugin.command.MineralBotCommand
import gg.mineral.bot.plugin.impl.ServerBotImpl
import net.minecraft.client.Minecraft
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class MineralBotPlugin : JavaPlugin() {
    override fun onLoad() {
        System.setProperty("java.net.preferIPv4Stack", "true")
        ServerBotImpl.init()
        Minecraft.init()
    }

    override fun onEnable() {
        getCommand("mineralbot").executor = MineralBotCommand()
        Bukkit.getPluginManager().registerEvents(
            BotAPI.INSTANCE as ServerBotImpl,
            this
        )
        GameLoop.start()
    }

    override fun onDisable() {
        BotAPI.INSTANCE.despawnAll()
        shutdown()
    }
}
