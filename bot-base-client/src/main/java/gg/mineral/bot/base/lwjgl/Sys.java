package gg.mineral.bot.base.lwjgl;

import gg.mineral.bot.impl.config.BotGlobalConfig;

public class Sys {

    public static void openURL(String string) {
        if (BotGlobalConfig.isHeadless())
            return;

        org.lwjgl.Sys.openURL(string);
    }

    public static String getVersion() {
        if (BotGlobalConfig.isHeadless())
            return "Mineral";

        return org.lwjgl.Sys.getVersion();
    }

    public static long getTime() {
        if (BotGlobalConfig.isHeadless())
            return System.nanoTime() / 1000000;

        return org.lwjgl.Sys.getTime();
    }

    public static long getTimerResolution() {
        if (BotGlobalConfig.isHeadless())
            return 1000;

        return org.lwjgl.Sys.getTimerResolution();
    }

}
