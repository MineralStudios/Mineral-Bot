package gg.mineral.bot.impl.thread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import lombok.Getter;

public class ThreadManager {
    @Getter
    private static final ExecutorService asyncExecutor = Executors
            .newWorkStealingPool();
    @Getter
    private static final ScheduledExecutorService gameLoopExecutor = Executors
            .newScheduledThreadPool(1, new MineralThreadFactory());

    public static void shutdown() {
        asyncExecutor.shutdown();
        gameLoopExecutor.shutdown();
    }
}
