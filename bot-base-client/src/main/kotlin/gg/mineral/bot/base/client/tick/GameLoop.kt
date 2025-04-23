package gg.mineral.bot.base.client.tick

import gg.mineral.bot.api.BotAPI
import gg.mineral.bot.base.client.BotImpl
import gg.mineral.bot.base.client.instance.ClientInstance
import gg.mineral.bot.base.client.manager.InstanceManager.instances
import gg.mineral.bot.base.client.manager.InstanceManager.pendingInstances
import gg.mineral.bot.impl.config.BotGlobalConfig
import kotlinx.coroutines.*
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiMemoryErrorScreen
import net.minecraft.crash.CrashReport
import net.minecraft.util.MinecraftError
import net.minecraft.util.ReportedException
import java.util.concurrent.TimeUnit

object GameLoop {
    // A SupervisorJob so one instance failing doesn’t cancel the whole scope
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    init {
        // 1) Main tick loop
        scope.launch {
            while (isActive) {
                if (BotGlobalConfig.tickConcurrently) {
                    // old behavior: launch one child coroutine per instance
                    coroutineScope {
                        instances.values.map { inst ->
                            launch { safeTick(inst) }
                        }.joinAll()
                    }
                } else {
                    // new behavior: tick each instance one after another
                    for (inst in instances.values) {
                        safeTick(inst)
                    }
                }

                // After all ticks, print spawn info if needed
                if (BotAPI.INSTANCE is BotImpl) {
                    (BotAPI.INSTANCE as BotImpl).printSpawnInfo()
                }

                // Non‑blocking pause
                delay(BotGlobalConfig.gameLoopDelay.toLong())
            }
        }

        // 2) Periodic cleanup loop
        scope.launch {
            while (isActive) {
                delay(TimeUnit.SECONDS.toMillis(10))

                instances.entries.removeIf { (_, inst) -> !inst.running }
                pendingInstances.entries.removeIf { (_, inst) -> !inst.running }
                BotAPI.INSTANCE.cleanup()
            }
        }
    }

    @JvmStatic
    fun start() {
        // No‑op: all work kicked off in init
    }

    // Extracted error‑handling + tick logic
    private fun safeTick(instance: ClientInstance) {
        try {
            if (!instance.running) return

            if (!instance.hasCrashed || instance.crashReporter == null) {
                try {
                    instance.runGameLoop()
                } catch (oom: OutOfMemoryError) {
                    instance.freeMemory()
                    instance.displayGuiScreen(GuiMemoryErrorScreen(instance))
                    if (BotGlobalConfig.manualGarbageCollection) System.gc()
                }
            } else {
                instance.displayCrashReport(instance.crashReporter)
                instance.running = false
            }
        } catch (mcErr: MinecraftError) {
            // silent Minecraft errors
        } catch (reported: ReportedException) {
            instance.addGraphicsAndWorldToCrashReport(reported.crashReport)
            instance.freeMemory()
            Minecraft.logger.fatal("Reported exception thrown!", reported)
            instance.displayCrashReport(reported.crashReport)
        } catch (t: Throwable) {
            val crashReport = instance
                .addGraphicsAndWorldToCrashReport(CrashReport("Unexpected error", t))
            instance.freeMemory()
            Minecraft.logger.fatal("Unreported exception thrown!", t)
            instance.displayCrashReport(crashReport)
        } finally {
            if (!instance.running) {
                instance.shutdownMinecraftApplet()
            }
        }
    }
}

