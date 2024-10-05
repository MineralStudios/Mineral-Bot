package gg.mineral.bot.impl.config;

import lombok.Getter;

public class BotGlobalConfig {
    @Getter
    private static boolean headless = true, control = true, optimizedGameLoop = true, debug = false,
            disableStepSounds = true, disableEntityCollisions = true, disableConnection = true,
            manualGarbageCollection = false;
    @Getter
    private static int gameLoopDelay = 1;

    public static void load() {

    }
}
