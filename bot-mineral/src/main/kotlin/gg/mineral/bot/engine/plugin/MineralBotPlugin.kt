package gg.mineral.bot.engine.plugin

import gg.mineral.api.plugin.MineralPlugin
import gg.mineral.bot.api.BotAPI
import gg.mineral.bot.base.client.tick.GameLoop
import gg.mineral.bot.engine.plugin.command.MineralBotCommand
import gg.mineral.bot.engine.plugin.impl.ServerBotImpl
import gg.mineral.bot.impl.thread.ThreadManager
import net.minecraft.client.Minecraft

class MineralBotPlugin : MineralPlugin() {
    override fun onDisable() {
        BotAPI.INSTANCE.despawnAll()
        ThreadManager.shutdown()
    }

    override fun onEnable() {
        System.setProperty("java.net.preferIPv4Stack", "true")
        ServerBotImpl.init()
        Minecraft.init()
        GameLoop.start()

        registerCommand(MineralBotCommand())
    }
}