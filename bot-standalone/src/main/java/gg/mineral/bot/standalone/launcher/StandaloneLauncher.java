package gg.mineral.bot.standalone.launcher;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.reader.impl.DefaultParser;
import org.jline.terminal.TerminalBuilder;

import com.google.common.collect.HashMultimap;

import gg.mineral.bot.api.configuration.BotConfiguration;
import gg.mineral.bot.api.controls.Key;
import gg.mineral.bot.base.client.BotImpl;
import gg.mineral.bot.base.client.instance.ClientInstance;
import gg.mineral.bot.base.client.manager.InstanceManager;
import gg.mineral.bot.base.client.tick.GameLoop;
import gg.mineral.bot.impl.config.BotGlobalConfig;
import gg.mineral.bot.impl.thread.ThreadManager;
import lombok.val;
import net.minecraft.client.Minecraft;
import net.minecraft.client.main.Main;

public class StandaloneLauncher {

    public static void main(String[] args) throws IOException {
        val file = new File("run");

        if (!file.exists())
            file.mkdirs();

        if (!BotGlobalConfig.isHeadless()) {
            Main.main(
                    concat(
                            new String[] {
                                    "--version",
                                    "Mineral-Bot-Client",
                                    "--accessToken",
                                    "0",
                                    "--assetIndex",
                                    "1.7.10",
                                    "--userProperties",
                                    "{}",
                                    "--gameDir",
                                    file.getAbsolutePath(),
                                    "--uuid",
                                    UUID.randomUUID().toString(),
                                    "--assetsDir",
                                    new File(file, "assets").getAbsolutePath()
                            },
                            args));
            return;
        }

        BotImpl.init();
        Minecraft.init();

        Configurator.setRootLevel(Level.DEBUG);
        System.setProperty("java.net.preferIPv4Stack", "true");
        GameLoop.start();

        Runtime.getRuntime().addShutdownHook(new Thread("Client Shutdown Thread") {

            @Override
            public void run() {
                InstanceManager.getInstances().values().removeIf(mc -> {
                    mc.stopIntegratedServer();
                    return true;
                });
            }
        });

        val terminal = TerminalBuilder.builder().build();
        val parser = new DefaultParser();
        val reader = LineReaderBuilder.builder()
                .terminal(terminal)
                .parser(parser)
                .build();
        consoleLoop: while (true) {
            try {
                val line = reader.readLine("~> ");

                val split = line.split(" ");
                val commandString = split[0];

                switch (commandString) {
                    case "stop":
                        break consoleLoop;
                    case "instances":
                        System.out.println("Instances: " + InstanceManager.getInstances().size());
                        break;
                    case "players":
                        System.out.println("Players: " + InstanceManager.getInstances().values().stream()
                                .map(mc -> mc.getSession().getUsername())
                                .reduce((a, b) -> a + ", " + b)
                                .orElse("None"));
                        break;
                    case "connect":
                        if (split.length < 4) {
                            System.out.println("Usage: connect <username> <ip> <port>");
                            break;
                        }

                        val username = split[1];
                        val ipAddr = split[2];
                        val port = Integer.parseInt(split[3]);
                        InstanceManager.getInstances().values()
                                .stream()
                                .filter(mc -> mc.getSession().getUsername().equals(username)).findFirst()
                                .ifPresentOrElse(mc -> System.out.println("Player already connected"), () -> {
                                    val configuration = BotConfiguration.builder().username(username).build();
                                    val minecraftInstance = new ClientInstance(
                                            configuration, 1280, 720,
                                            false,
                                            false,
                                            file,
                                            new File(file, "assets"),
                                            new File(file, "resourcepacks"),
                                            java.net.Proxy.NO_PROXY,
                                            "Mineral-Bot-Client", HashMultimap.create(),
                                            "1.7.10");

                                    minecraftInstance.setServer(ipAddr, port);
                                    minecraftInstance.run();
                                    InstanceManager.getInstances().put(configuration.getUuid(), minecraftInstance);
                                });

                        break;
                    case "gc":
                        System.gc();
                        break;

                    case "presskey":
                        if (split.length < 4) {
                            System.out.println("Usage: presskey <username> <key> <duration>");
                            break;
                        }

                        val username2 = split[1];
                        val key = split[2];
                        val type = Key.Type.valueOf(key);
                        val duration = Integer.parseInt(split[3]);
                        InstanceManager.getInstances().values()
                                .stream()
                                .filter(mc -> mc.getSession().getUsername().equals(username2)).findFirst()
                                .ifPresent(mc -> mc.getKeyboard().pressKey(duration, type));
                        break;
                    default:
                        System.out.println("Unknown command: " + line);
                        break;
                }

            } catch (UserInterruptException e) {
                // Ignore
            } catch (EndOfFileException e) {
                return;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        System.out.println("Shutting down...");
        ThreadManager.shutdown();
        System.exit(0);
    }

    public static <T> T[] concat(T[] first, T[] second) {
        val result = java.util.Arrays.copyOf(first, first.length + second.length);
        System.arraycopy(second, 0, result, first.length, second.length);
        return result;
    }
}
