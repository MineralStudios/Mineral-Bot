package gg.mineral.bot.base.client;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import com.google.common.collect.HashMultimap;

import gg.mineral.bot.api.BotAPI;
import gg.mineral.bot.api.configuration.BotConfiguration;
import gg.mineral.bot.api.entity.living.player.FakePlayer;
import gg.mineral.bot.api.math.ServerLocation;
import gg.mineral.bot.api.message.ChatColor;
import gg.mineral.bot.base.client.manager.InstanceManager;
import gg.mineral.bot.base.client.player.FakePlayerInstance;
import gg.mineral.bot.impl.thread.ThreadManager;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;

import java.util.Iterator;

public abstract class BotImpl extends BotAPI {

    private long lastSpawnInfo = 0;

    public BotImpl() {
        super(new ObjectOpenHashSet<>());
    }

    public void printSpawnInfo() {
        if (spawnRecords.isEmpty() || System.currentTimeMillis() - lastSpawnInfo < 3000)
            return;

        lastSpawnInfo = System.currentTimeMillis();

        StringBuilder sb = new StringBuilder(ChatColor.GRAY + "[");

        Iterator<SpawnRecord> iterator = spawnRecords.iterator();

        long totalTime = 0;
        int amount = 0;

        while (iterator.hasNext()) {
            SpawnRecord record = iterator.next();
            sb.append(ChatColor.GREEN + record.name());
            amount++;
            totalTime += record.time();
            if (iterator.hasNext())
                sb.append(ChatColor.GRAY + ", ");

            iterator.remove();
        }

        sb.append(ChatColor.GRAY + "]");

        System.out.println(ChatColor.WHITE + ChatColor.UNDERLINE + "Recent Spawn Info:" + ChatColor.RESET);
        System.out.println(ChatColor.WHITE + "Amount: " + ChatColor.CYAN + amount + ChatColor.RESET);
        System.out
                .println(ChatColor.WHITE + "Total Spawn Time: " + ChatColor.CYAN + totalTime + "ms" + ChatColor.RESET);
        System.out.println(ChatColor.WHITE + "Average Spawn Time: " + ChatColor.CYAN + (totalTime / amount) + "ms"
                + ChatColor.RESET);
        System.out.println(ChatColor.WHITE + "Names: " + sb.toString() + ChatColor.RESET);
    }

    public static void init() {
        BotAPI.INSTANCE = new BotImpl() {
            @Override
            public FakePlayer spawn(BotConfiguration configuration, ServerLocation location) {
                return spawn(configuration, "localhost", 25565);
            }
        };
    }

    @Override
    public FakePlayer spawn(BotConfiguration configuration, String serverIp, int serverPort) {
        long startTime = System.nanoTime() / 1000000;
        File file = configuration.getRunDirectory();

        if (!file.exists())
            file.mkdirs();

        FakePlayerInstance instance = new FakePlayerInstance(configuration, 1280, 720,
                false,
                false,
                file,
                new File(file, "assets"),
                new File(file, "resourcepacks"),
                java.net.Proxy.NO_PROXY,
                "Mineral-Bot-Client", HashMultimap.create(),
                "1.7.10");

        instance.setServer(serverIp, serverPort);
        ThreadManager.getGameLoopExecutor().execute(() -> {
            instance.run();
            InstanceManager.getInstances().put(instance.getUuid(), instance);
        });

        spawnRecords.add(new SpawnRecord(configuration.getUsername(), (System.nanoTime() / 1000000) - startTime));
        return instance;
    }

    @Override
    public boolean despawn(FakePlayer player) {
        return despawn(player.getUuid());
    }

    @Override
    public boolean despawn(UUID uuid) {
        FakePlayerInstance instance = InstanceManager.getInstances().get(uuid);
        boolean running = instance != null && instance.running;
        if (instance != null)
            instance.shutdown();

        return running;
    }

    @Override
    public boolean isFakePlayer(UUID uuid) {
        synchronized (InstanceManager.getInstances()) {
            return InstanceManager.getInstances().containsKey(uuid);
        }
    }

    @Override
    public void despawnAll() {
        InstanceManager.getInstances().values().forEach(FakePlayerInstance::shutdown);
        InstanceManager.getInstances().clear();
    }

    @Override
    public Collection<FakePlayer> getFakePlayers() {
        List<FakePlayer> fakePlayers = new ArrayList<>();

        for (FakePlayerInstance instance : InstanceManager.getInstances().values())
            fakePlayers.add(instance);
        return fakePlayers;
    }
}
