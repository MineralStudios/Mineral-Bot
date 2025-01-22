package gg.mineral.bot.api.debug;

import java.util.Date;
import java.util.UUID;

public interface Logger {

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
        // System.out.println("[" + obj.getClass().getSimpleName() + "] " + message);
    }

    default void warn(Object obj, String message) {
        //System.out.println("[" + obj.getClass().getSimpleName() + "] " + message);
    }

    default void error(Object obj, String message) {
        //System.out.println("[" + obj.getClass().getSimpleName() + "] " + message);
    }

    default void success(Object obj, String message) {
        //System.out.println("[" + obj.getClass().getSimpleName() + "] " + message);
    }

    default void println(String message) {
        System.out.println(message);
    }
}
