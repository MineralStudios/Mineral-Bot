package gg.mineral.bot.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import gg.mineral.bot.api.configuration.BotConfiguration;
import gg.mineral.bot.api.entity.living.player.FakePlayer;
import gg.mineral.bot.api.math.ServerLocation;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class BotAPI {

    public static BotAPI INSTANCE = null;

    public abstract FakePlayer spawn(BotConfiguration configuration, String serverIp, int serverPort);

    public abstract FakePlayer spawn(BotConfiguration configuration, ServerLocation location);

    public abstract boolean[] despawn(FakePlayer... players);

    public abstract boolean[] despawn(UUID... uuids);

    public abstract boolean despawn(FakePlayer player);

    public abstract boolean despawn(UUID uuid);

    public abstract void despawnAll();

    public abstract boolean isFakePlayer(UUID uuid);

    public abstract Collection<FakePlayer> getFakePlayers();

    protected final List<SpawnRecord> spawnRecords = new ArrayList<>();

    public record SpawnRecord(String name, long time) {
    }
}
