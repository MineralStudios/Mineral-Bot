package gg.mineral.bot.base.client.tick

import gg.mineral.bot.api.BotAPI
import gg.mineral.bot.base.client.BotImpl
import gg.mineral.bot.base.client.manager.InstanceManager.instances
import gg.mineral.bot.base.client.manager.InstanceManager.pendingInstances
import gg.mineral.bot.impl.config.BotGlobalConfig
import gg.mineral.bot.impl.thread.ThreadManager.gameLoopExecutor
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiMemoryErrorScreen
import net.minecraft.crash.CrashReport
import net.minecraft.util.MinecraftError
import net.minecraft.util.ReportedException
import java.util.concurrent.TimeUnit

object GameLoop {
    init {
        gameLoopExecutor.scheduleAtFixedRate({
            for (instance in instances.values) {
                try {
                    if (instance.running) {
                        if (!instance.hasCrashed || instance.crashReporter == null) {
                            try {
                                instance.runGameLoop()
                            } catch (var10: OutOfMemoryError) {
                                instance.freeMemory()
                                instance.displayGuiScreen(GuiMemoryErrorScreen(instance))
                                if (BotGlobalConfig.manualGarbageCollection) System.gc()
                            }
                        } else {
                            instance.displayCrashReport(instance.crashReporter)
                            instance.running = false
                        }
                    }
                } catch (var12: MinecraftError) {
                    // Handle Minecraft-specific errors
                } catch (var13: ReportedException) {
                    instance.addGraphicsAndWorldToCrashReport(var13.crashReport)
                    instance.freeMemory()
                    Minecraft.logger.fatal("Reported exception thrown!", var13)
                    instance.displayCrashReport(var13.crashReport)
                } catch (var14: Throwable) {
                    val headlessCrashReport =
                        instance.addGraphicsAndWorldToCrashReport(
                            CrashReport("Unexpected error", var14)
                        )
                    instance.freeMemory()
                    Minecraft.logger.fatal("Unreported exception thrown!", var14)
                    instance.displayCrashReport(headlessCrashReport)
                } finally {
                    if (!instance.running) instance.shutdownMinecraftApplet()
                }
            }
            if (BotAPI.INSTANCE is BotImpl) (BotAPI.INSTANCE as BotImpl).printSpawnInfo()
        }, 0, BotGlobalConfig.gameLoopDelay.toLong(), TimeUnit.MILLISECONDS)

        gameLoopExecutor.scheduleAtFixedRate({
            instances.entries.removeIf { (_, instance) -> !instance.running }
            pendingInstances.entries.removeIf { (_, instance) -> !instance.running }
            BotAPI.INSTANCE.cleanup()
        }, 0, 10, TimeUnit.SECONDS)
    }

    @JvmStatic
    fun start() {
    }
}
