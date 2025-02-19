package gg.mineral.bot.impl.thread

import java.util.concurrent.ThreadFactory

class MineralThreadFactory : ThreadFactory {
    override fun newThread(r: Runnable): Thread {
        val t = Thread(r)
        t.name = "MineralThread-" + t.id
        t.uncaughtExceptionHandler =
            Thread.UncaughtExceptionHandler { thread: Thread, throwable: Throwable ->
                System.err.println("Uncaught exception in thread " + thread.name)
                throwable.printStackTrace()
            }
        return t
    }
}
