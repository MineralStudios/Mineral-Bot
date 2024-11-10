package gg.mineral.bot.plugin;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import gg.mineral.bot.api.BotAPI;
import gg.mineral.bot.base.client.tick.GameLoop;
import gg.mineral.bot.impl.thread.ThreadManager;
import gg.mineral.bot.plugin.command.MineralBotCommand;
import gg.mineral.bot.plugin.impl.ServerBotImpl;
import net.minecraft.client.Minecraft;

public class MineralBotPlugin extends JavaPlugin {

    @Override
    public void onLoad() {
        System.setProperty("java.net.preferIPv4Stack", "true");
        ServerBotImpl.init();
        Minecraft.init();
    }

    @Override
    public void onEnable() {
        this.getCommand("mineralbot").setExecutor(new MineralBotCommand());
        Bukkit.getPluginManager().registerEvents((ServerBotImpl) BotAPI.INSTANCE, this);
        GameLoop.start();
    }

    @Override
    public void onDisable() {
        BotAPI.INSTANCE.despawnAll();
        ThreadManager.shutdown();
    }
}
