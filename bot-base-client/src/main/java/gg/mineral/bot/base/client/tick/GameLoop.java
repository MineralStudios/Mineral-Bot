package gg.mineral.bot.base.client.tick;

import gg.mineral.bot.api.BotAPI;
import gg.mineral.bot.base.client.BotImpl;
import gg.mineral.bot.base.client.manager.InstanceManager;
import gg.mineral.bot.impl.config.BotGlobalConfig;
import gg.mineral.bot.impl.thread.ThreadManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMemoryErrorScreen;
import net.minecraft.crash.CrashReport;
import net.minecraft.util.MinecraftError;
import net.minecraft.util.ReportedException;

public class GameLoop {
    static {
        ThreadManager.getGameLoopExecutor().scheduleAtFixedRate(() -> {

            for (Minecraft instance : InstanceManager.getInstances().values()) {
                try {
                    if (instance.running) {
                        if (!instance.hasCrashed || instance.crashReporter == null) {
                            try {
                                instance.runGameLoop();
                            } catch (OutOfMemoryError var10) {
                                instance.freeMemory();
                                instance.displayGuiScreen(new GuiMemoryErrorScreen(instance));
                                System.gc();
                            }
                        } else {
                            instance.displayCrashReport(instance.crashReporter);
                            instance.running = false;
                        }
                    }
                } catch (MinecraftError var12) {
                    // Handle Minecraft-specific errors
                } catch (ReportedException var13) {
                    instance.addGraphicsAndWorldToCrashReport(var13.getCrashReport());
                    instance.freeMemory();
                    Minecraft.logger.fatal("Reported exception thrown!", var13);
                    instance.displayCrashReport(var13.getCrashReport());
                } catch (Throwable var14) {
                    CrashReport headlessCrashReport = instance.addGraphicsAndWorldToCrashReport(
                            new CrashReport("Unexpected error", var14));
                    instance.freeMemory();
                    Minecraft.logger.fatal("Unreported exception thrown!", var14);
                    instance.displayCrashReport(headlessCrashReport);
                } finally {
                    if (!instance.running)
                        instance.shutdownMinecraftApplet();
                }
            }

            if (BotAPI.INSTANCE != null && BotAPI.INSTANCE instanceof BotImpl botImpl)
                botImpl.printSpawnInfo();
        }, 0, BotGlobalConfig.getGameLoopDelay(), java.util.concurrent.TimeUnit.MILLISECONDS);
    }

    public static void start() {

    }
}
