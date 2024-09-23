package gg.mineral.bot.base.client.manager;

import java.util.UUID;

import gg.mineral.bot.base.client.player.FakePlayerInstance;
import lombok.Getter;
import java.util.concurrent.ConcurrentHashMap;

public class InstanceManager {
    @Getter
    private static final ConcurrentHashMap<UUID, FakePlayerInstance> instances = new ConcurrentHashMap<>();

    public static boolean isRunning() {
        return !instances.isEmpty();
    }
}
