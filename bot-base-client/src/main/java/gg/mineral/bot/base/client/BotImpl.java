package gg.mineral.bot.base.client;

import com.google.common.collect.HashMultimap;

import gg.mineral.bot.api.BotAPI;
import gg.mineral.bot.api.configuration.BotConfiguration;
import gg.mineral.bot.api.entity.living.player.FakePlayer;
import gg.mineral.bot.api.math.ServerLocation;
import gg.mineral.bot.base.client.manager.InstanceManager;
import gg.mineral.bot.base.client.player.FakePlayerInstance;

import java.io.File;

import java.util.UUID;

public abstract class BotImpl extends BotAPI {

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
        instance.run();
        InstanceManager.getInstances().put(instance.getUuid(), instance);
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
        return InstanceManager.getInstances().containsKey(uuid);
    }

    @Override
    public void despawnAll() {
        InstanceManager.getInstances().values().forEach(FakePlayerInstance::shutdown);
        InstanceManager.getInstances().clear();
    }
}
