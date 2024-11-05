package gg.mineral.bot.api.debug;

import gg.mineral.bot.api.message.ChatColor;

public interface Logger {
    boolean isLoggingEnabled();

    default void info(String message) {
        if (!isLoggingEnabled())
            return;
        System.out.println("[Mineral-Bot ~ INFO] " + message);
    }

    default void warn(String message) {
        if (!isLoggingEnabled())
            return;
        System.out.println(ChatColor.YELLOW + "[Mineral-Bot ~ WARN] " + message);
    }

    default void error(String message) {
        if (!isLoggingEnabled())
            return;
        System.out.println(ChatColor.RED + "[Mineral-Bot ~ ERROR] " + message);
    }

    default void success(String message) {
        if (!isLoggingEnabled())
            return;
        System.out.println(ChatColor.GREEN + "[Mineral-Bot ~ SUCCESS] " + message);
    }

    default void println(String message) {
        System.out.println(message);
    }
}
