package gg.mineral.bot.api.debug;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;

import lombok.val;

public interface Logger {

    public static Cache<UUID, ConcurrentLinkedQueue<Log>> logCache = Caffeine.newBuilder()
            .maximumSize(10000)
            .expireAfterWrite(15, TimeUnit.MINUTES)
            .build();

    UUID getIdentifier();

    public record Log(LogLevel level, long timeNanos, String message, Object obj, String threadName) {
        public Date getTime() {
            return new Date(timeNanos / 1000000);
        }
    }

    public enum LogLevel {
        INFO, WARN, ERROR, SUCCESS
    }

    default void info(Object obj, String message) {
        val thread = Thread.currentThread();
        val identifier = getIdentifier();
        val log = new Log(LogLevel.INFO, System.nanoTime(), message, obj, thread.getName());
        logCache.get(identifier, k -> new ConcurrentLinkedQueue<>()).add(log);
    }

    default void warn(Object obj, String message) {
        val thread = Thread.currentThread();
        val identifier = getIdentifier();
        val log = new Log(LogLevel.WARN, System.nanoTime(), message, obj, thread.getName());
        logCache.get(identifier, k -> new ConcurrentLinkedQueue<>()).add(log);
    }

    default void error(Object obj, String message) {
        val thread = Thread.currentThread();
        val identifier = getIdentifier();
        val log = new Log(LogLevel.ERROR, System.nanoTime(), message, obj, thread.getName());
        logCache.get(identifier, k -> new ConcurrentLinkedQueue<>()).add(log);
    }

    default void success(Object obj, String message) {
        val thread = Thread.currentThread();
        val identifier = getIdentifier();
        val log = new Log(LogLevel.SUCCESS, System.nanoTime(), message, obj, thread.getName());
        logCache.get(identifier, k -> new ConcurrentLinkedQueue<>()).add(log);
    }

    default void println(String message) {
        System.out.println(message);
    }
}
