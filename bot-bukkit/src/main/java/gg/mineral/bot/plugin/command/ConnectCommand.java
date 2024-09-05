package gg.mineral.bot.plugin.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.google.common.collect.HashMultimap;
import java.io.File;

import gg.mineral.bot.api.configuration.BotConfiguration;
import gg.mineral.bot.base.client.manager.InstanceManager;
import gg.mineral.bot.base.client.player.FakePlayerInstance;

public class ConnectCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        File file = new File("run");

        if (!file.exists())
            file.mkdirs();
        if (args.length < 1) {
            System.out.println("Usage: connect <username>");
            return false;
        }

        String username = args[0];
        InstanceManager.getInstances().values()
                .stream()
                .filter(mc -> mc.getSession().getUsername().equals(username)).findFirst()
                .ifPresentOrElse(mc -> System.out.println("Player already connected"), () -> {
                    FakePlayerInstance minecraftInstance = new FakePlayerInstance(
                            BotConfiguration.builder().username(username).build(), 1280, 720,
                            false,
                            false,
                            file,
                            new File(file, "assets"),
                            new File(file, "resourcepacks"),
                            java.net.Proxy.NO_PROXY,
                            "Mineral-Bot-Client", HashMultimap.create(),
                            "1.7.10");

                    minecraftInstance.setServer("127.0.0.1", Bukkit.getServer().getPort());
                    minecraftInstance.run();
                    InstanceManager.getInstances().put(minecraftInstance.getUuid(), minecraftInstance);
                });

        return true;
    }

}
