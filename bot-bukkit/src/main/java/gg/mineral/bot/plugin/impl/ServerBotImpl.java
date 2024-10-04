package gg.mineral.bot.plugin.impl;

import java.io.File;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import com.google.common.collect.HashMultimap;

import gg.mineral.bot.api.BotAPI;
import gg.mineral.bot.api.configuration.BotConfiguration;
import gg.mineral.bot.api.entity.living.player.FakePlayer;
import gg.mineral.bot.api.math.ServerLocation;
import gg.mineral.bot.base.client.BotImpl;
import gg.mineral.bot.base.client.gui.GuiConnecting;
import gg.mineral.bot.base.client.manager.InstanceManager;
import gg.mineral.bot.base.client.player.FakePlayerInstance;
import gg.mineral.bot.impl.thread.ThreadManager;
import gg.mineral.bot.plugin.impl.player.NMSServerPlayer;
import gg.mineral.bot.plugin.network.ClientNetworkManager;
import gg.mineral.bot.plugin.network.ServerNetworkManager;
import gg.mineral.bot.plugin.network.packet.Client2ServerTranslator;
import gg.mineral.bot.plugin.network.packet.Server2ClientTranslator;
import lombok.Getter;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.EnumDifficulty;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import net.minecraft.server.v1_8_R3.PacketPlayInKeepAlive;
import net.minecraft.server.v1_8_R3.PacketPlayOutAbilities;
import net.minecraft.server.v1_8_R3.PacketPlayOutHeldItemSlot;
import net.minecraft.server.v1_8_R3.PacketPlayOutLogin;
import net.minecraft.server.v1_8_R3.PacketPlayOutServerDifficulty;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnPosition;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import net.minecraft.server.v1_8_R3.WorldType;
import net.minecraft.server.v1_8_R3.WorldSettings.EnumGamemode;

public class ServerBotImpl extends BotImpl implements Listener {

    public static void init() {
        BotAPI.INSTANCE = new ServerBotImpl();
    }

    @Override
    public FakePlayer spawn(BotConfiguration configuration, ServerLocation location) {
        long startTime = System.nanoTime() / 1000000;
        File file = configuration.getRunDirectory();

        if (!file.exists())
            file.mkdirs();

        Client2ServerTranslator c2sTranslator = new Client2ServerTranslator();
        Server2ClientTranslator s2cTranslator = new Server2ClientTranslator();

        FakePlayerInstance instance = new FakePlayerInstance(configuration, 1280, 720,
                false,
                false,
                file,
                new File(file, "assets"),
                new File(file, "resourcepacks"),
                java.net.Proxy.NO_PROXY,
                "Mineral-Bot-Client", HashMultimap.create(),
                "1.7.10") {
            @Override
            public void displayGuiScreen(GuiScreen guiScreen) {
                if (guiScreen instanceof GuiConnecting connecting)
                    connecting.setConnectFunction((ip, port) -> {
                    });

                super.displayGuiScreen(guiScreen);
            }
        };
        instance.setServer("127.0.0.1", Bukkit.getServer().getPort());
        ThreadManager.getGameLoopExecutor().execute(() -> {
            instance.run();
            InstanceManager.getInstances().put(instance.getUuid(), instance);
        });

        String name = configuration.getUsername();

        NMSServerPlayer serverSide = new NMSServerPlayer(location.getWorld(),
                configuration.getUuid(), name, configuration.getSkin().getValue(),
                configuration.getSkin().getSignature());

        serverSide.setPosition(location.getX(), location.getY(), location.getZ());
        serverSide.setYawPitch(location.getYaw(), location.getPitch());
        serverSide.spawnInWorld(location.getWorld());

        ClientNetworkManager cNetworkManager = new ClientNetworkManager(c2sTranslator,
                instance);
        ServerNetworkManager sNetworkManager = new ServerNetworkManager(s2cTranslator, instance);

        NetHandlerPlayClient netHandlerPlayClient = new NetHandlerPlayClient(instance, null,
                cNetworkManager);

        s2cTranslator.setNetHandlerPlayClient(netHandlerPlayClient);

        PlayerConnection playerConnection = new PlayerConnection(MinecraftServer.getServer(), sNetworkManager,
                serverSide) {
            @Getter
            private boolean disconnected = false;

            @Override
            public void disconnect(String s) {
                despawn(configuration.getUuid());
                // TODO: cancellable kick event
                super.disconnect(s);
                disconnected = true;
            }

            @Override
            // TODO: Temporary fix for the issue with the player's ping
            public void a(PacketPlayInKeepAlive packetplayinkeepalive) {
                this.player.ping = instance.getLatency();
            }

        };

        c2sTranslator.setPlayerConnection(playerConnection);

        serverSide.callSpawnEvents();

        playerConnection.sendPacket(new PacketPlayOutLogin(serverSide.getId(),
                EnumGamemode.getById(serverSide.getGameModeId()), serverSide.isWorldHardcore(),
                serverSide.getDimensionId(), EnumDifficulty.getById(serverSide.getDifficultyId()),
                Math.min(serverSide.getMaxPlayers(), 60),
                WorldType.getType(serverSide.getWorldTypeName()),
                serverSide.isReducedDebugInfo()));

        int[] spawn = serverSide.getWorldSpawn();
        serverSide.initializeGameMode();

        serverSide.sendSupportedChannels();

        // playerConnection.sendPacket(new PacketPlayOutCustomPayload("MC|Brand",
        // new PacketDataSerializer(
        // Unpooled.wrappedBuffer(ByteUtil.stringToBytes(serverSide.getServerModName())))));

        playerConnection.sendPacket(new PacketPlayOutServerDifficulty(
                EnumDifficulty.getById(serverSide.getDifficultyId()), serverSide.isDifficultyLocked()));

        playerConnection.sendPacket(new PacketPlayOutSpawnPosition(new BlockPosition(spawn[0], spawn[1], spawn[2])));

        playerConnection.sendPacket(new PacketPlayOutAbilities(serverSide.abilities));

        playerConnection.sendPacket(new PacketPlayOutHeldItemSlot(serverSide.getItemInHandIndex()));

        serverSide.sendScoreboard();
        serverSide.resetPlayerSampleUpdateTimer();

        serverSide.onJoin();

        // Send location to client
        serverSide.sendLocationToClient();

        // World border, time, weather
        serverSide.initWorld();

        serverSide.initResourcePack();

        // serverSide.getEffectPackets().forEach(packet ->
        // getServerConnection().queuePacket(packet));
        serverSide.syncInventory();

        spawnRecords.add(new SpawnRecord(configuration.getUsername(), (System.nanoTime() / 1000000) - startTime));

        return instance;
    }

    @Override
    public boolean despawn(UUID uuid) {
        boolean isBot = super.despawn(uuid);

        if (isBot) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null)
                player.kickPlayer("Despawned");
        }

        return isBot;
    }

}
