package gg.mineral.bot.impl.thread;

import java.util.concurrent.ThreadFactory;

import lombok.val;

public class MineralThreadFactory implements ThreadFactory {

    @Override
    public Thread newThread(Runnable r) {
        val t = new Thread(r);
        t.setName("MineralThread-" + t.getId());
        t.setUncaughtExceptionHandler((thread, throwable) -> {
            System.err.println("Uncaught exception in thread " + thread.getName());
            throwable.printStackTrace();
        });
        return t;
    }

}
