package gg.mineral.bot.bukkit.plugin

import gg.mineral.bot.api.BotAPI
import gg.mineral.bot.base.client.manager.InstanceManager
import gg.mineral.bot.base.client.tick.GameLoop
import gg.mineral.bot.bukkit.plugin.command.MineralBotCommand
import gg.mineral.bot.bukkit.plugin.impl.ServerBotImpl
import gg.mineral.bot.impl.thread.ThreadManager.shutdown
import net.minecraft.client.Minecraft
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin


class MineralBotPlugin : JavaPlugin() {
    companion object {
        lateinit var instance: MineralBotPlugin
    }

    override fun onLoad() {
        System.setProperty("java.net.preferIPv4Stack", "true")
        ServerBotImpl.init()
        Minecraft.init()
    }

    override fun onEnable() {
        instance = this
        getCommand("mineralbot").executor = MineralBotCommand()
        GameLoop.start()
    }

    override fun onDisable() {
        BotAPI.INSTANCE.despawnAll()
        logger.info("Disabling MineralBotPlugin...")

        // Cancel all tasks to prevent memory leaks
        Bukkit.getScheduler().cancelTasks(this)

        // Ensure all instances are removed
        InstanceManager.instances.clear()
        InstanceManager.pendingInstances.clear()

        shutdown()

        ServerBotImpl.destroy()
    }
}
