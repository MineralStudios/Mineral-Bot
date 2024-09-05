package gg.mineral.bot.base.client.manager;

import java.util.UUID;

import gg.mineral.bot.base.client.player.FakePlayerInstance;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.Getter;

public class InstanceManager {
    @Getter
    private static final Object2ObjectOpenHashMap<UUID, FakePlayerInstance> instances = new Object2ObjectOpenHashMap<>();

    public static boolean isRunning() {
        return !instances.isEmpty();
    }
}
