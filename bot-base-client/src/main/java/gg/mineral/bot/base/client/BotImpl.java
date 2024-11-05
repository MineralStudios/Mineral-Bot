package gg.mineral.bot.base.client;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.UUID;

import com.google.common.collect.HashMultimap;

import gg.mineral.bot.api.BotAPI;
import gg.mineral.bot.api.configuration.BotConfiguration;
import gg.mineral.bot.api.debug.Logger;
import gg.mineral.bot.api.entity.living.player.FakePlayer;
import gg.mineral.bot.api.instance.ClientInstance;
import gg.mineral.bot.api.math.ServerLocation;
import gg.mineral.bot.api.message.ChatColor;
import gg.mineral.bot.base.client.manager.InstanceManager;
import gg.mineral.bot.impl.thread.ThreadManager;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import lombok.val;

public abstract class BotImpl extends BotAPI implements Logger {

    private long lastSpawnInfo = 0;

    public void printSpawnInfo() {
        if (spawnRecords.isEmpty() || System.currentTimeMillis() - lastSpawnInfo < 3000)
            return;

        lastSpawnInfo = System.currentTimeMillis();

        val iterator = spawnRecords.iterator();

        var totalTime = 0;
        var amount = 0;

        val nameCount = new Object2IntOpenHashMap<String>();

        while (iterator.hasNext()) {
            SpawnRecord record = iterator.next();
            nameCount.put(record.name(), nameCount.getInt(record.name()) + 1);
            amount++;
            totalTime += record.time();
            iterator.remove();
        }

        val sb = new StringBuilder();

        val newLine = System.lineSeparator();

        for (Iterator<Entry<String>> it = nameCount.object2IntEntrySet().iterator(); it.hasNext();) {
            val e = it.next();
            val name = e.getKey();
            val count = e.getIntValue();

            if (it.hasNext())
                sb.append(newLine);

            sb.append("• " + name + " x" + count);
        }

        println(ChatColor.WHITE + ChatColor.UNDERLINE + "Recent Spawn Info:" + ChatColor.RESET);
        println(ChatColor.WHITE + "Amount: " + ChatColor.CYAN + amount + ChatColor.RESET);
        println(ChatColor.WHITE + "Total Spawn Time: " + ChatColor.CYAN + totalTime + "ms" + ChatColor.RESET);
        println(ChatColor.WHITE + "Average Spawn Time: " + ChatColor.CYAN + (totalTime / amount) + "ms"
                + ChatColor.RESET);
        println(ChatColor.WHITE + "Names: " + sb.toString() + ChatColor.RESET);
    }

    @Override
    public boolean isLoggingEnabled() {
        return true;
    }

    public static void init() {
        BotAPI.INSTANCE = new BotImpl() {
            @Override
            public ClientInstance spawn(BotConfiguration configuration, ServerLocation location) {
                return spawn(configuration, "localhost", 25565);
            }
        };
    }

    @Override
    public ClientInstance spawn(BotConfiguration configuration, String serverIp, int serverPort) {
        val startTime = System.nanoTime() / 1000000;
        val file = configuration.getRunDirectory();

        if (!file.exists())
            file.mkdirs();

        val instance = new gg.mineral.bot.base.client.instance.ClientInstance(configuration, 1280, 720,
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
            InstanceManager.getInstances().put(configuration.getUuid(), instance);
        });

        spawnRecords.add(new SpawnRecord(configuration.getUsername(), (System.nanoTime() / 1000000) - startTime));
        return instance;
    }

    @Override
    public boolean despawn(UUID uuid) {
        val instance = InstanceManager.getInstances().get(uuid);
        val running = instance != null && instance.running;
        if (instance != null)
            instance.shutdown();

        return running;
    }

    @Override
    public boolean[] despawn(UUID... uuids) {
        val results = new boolean[uuids.length];
        for (int i = 0; i < uuids.length; i++)
            results[i] = despawn(uuids[i]);
        return results;
    }

    @Override
    public boolean isFakePlayer(UUID uuid) {
        val instances = InstanceManager.getInstances();
        synchronized (instances) {
            return instances.containsKey(uuid);
        }
    }

    @Override
    public void despawnAll() {
        val instances = InstanceManager.getInstances();
        instances.values().forEach(ClientInstance::shutdown);
        instances.clear();
    }

    @Override
    public Collection<FakePlayer> getFakePlayers() {
        val fakePlayers = new ArrayList<FakePlayer>();

        for (val instance : InstanceManager.getInstances().values())
            fakePlayers.add(instance.getFakePlayer());
        return fakePlayers;
    }
}
