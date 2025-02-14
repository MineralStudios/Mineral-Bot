package gg.mineral.bot.impl.thread;

import lombok.val;
import org.eclipse.jdt.annotation.NonNull;

import java.util.concurrent.ThreadFactory;

public class MineralThreadFactory implements ThreadFactory {

    @Override
    public Thread newThread(@NonNull Runnable r) {
        val t = new Thread(r);
        t.setName("MineralThread-" + t.getId());
        t.setUncaughtExceptionHandler((thread, throwable) -> {
            System.err.println("Uncaught exception in thread " + thread.getName());
            throwable.printStackTrace();
        });
        return t;
    }

}
