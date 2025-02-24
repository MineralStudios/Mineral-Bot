package gg.mineral.bot.impl.thread

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService

object ThreadManager {
    val asyncExecutor: ExecutorService = Executors
        .newWorkStealingPool()
    val gameLoopExecutor: ScheduledExecutorService = Executors
        .newScheduledThreadPool(1, MineralThreadFactory("GameLoop"))

    @JvmStatic
    fun shutdown() {
        asyncExecutor.shutdown()
        gameLoopExecutor.shutdown()
    }
}
