package gg.mineral.bot.base.client.manager;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import gg.mineral.bot.base.client.instance.ClientInstance;
import lombok.Getter;

public class InstanceManager {
    @Getter
    private static final ConcurrentHashMap<UUID, ClientInstance> instances = new ConcurrentHashMap<>();

    public static boolean isRunning() {
        return !instances.isEmpty();
    }
}
