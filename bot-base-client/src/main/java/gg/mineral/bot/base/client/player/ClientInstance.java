package gg.mineral.bot.base.client.player;

import java.io.File;
import java.net.Proxy;
import java.util.Queue;

import java.util.Set;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

import com.google.common.collect.Multimap;

import gg.mineral.bot.api.configuration.BotConfiguration;
import gg.mineral.bot.api.controls.Keyboard;
import gg.mineral.bot.api.controls.Mouse;
import gg.mineral.bot.api.entity.living.player.FakePlayer;
import gg.mineral.bot.api.event.Event;
import gg.mineral.bot.api.goal.Goal;

import gg.mineral.bot.api.screen.Screen;

import gg.mineral.bot.base.client.gui.GuiConnecting;
import gg.mineral.bot.base.client.manager.InstanceManager;
import gg.mineral.bot.impl.config.BotGlobalConfig;
import gg.mineral.bot.impl.thread.ThreadManager;
import io.netty.util.concurrent.GenericFutureListener;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;

import lombok.Getter;
import lombok.val;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerLoginClient;

import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.login.client.C00PacketLoginStart;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.Session;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class ClientInstance extends Minecraft implements gg.mineral.bot.api.instance.ClientInstance {

    @Getter
    private final BotConfiguration configuration;

    private Set<Goal> goals = new ObjectLinkedOpenHashSet<>();

    private Queue<DelayedTask> delayedTasks = new ConcurrentLinkedQueue<>();
    private Thread mainThread = null;
    @Getter
    private int latency = 0, currentTick;

    record DelayedTask(Runnable runnable, long sendTime) {
        public boolean canSend() {
            return getSystemTime() >= sendTime;
        }
    }

    public boolean scheduleTask(Runnable runnable, long delay) {
        if (isMainThread() && delay <= 0 && delayedTasks.isEmpty()) {
            runnable.run();
            return true;
        }
        delayedTasks.add(new DelayedTask(runnable, getSystemTime() + delay));
        return false;
    }

    public ClientInstance(BotConfiguration configuration, int width, int height,
            boolean fullscreen,
            boolean demo, File gameDir, File assetsDir, File resourcePackDir, Proxy proxy, String version,
            @SuppressWarnings("rawtypes") Multimap userProperties, String assetIndex) {
        super(new Session(configuration.getFullUsername(), configuration.getUuid().toString(),
                "0",
                "legacy"), width, height, fullscreen, demo, gameDir, assetsDir, resourcePackDir,
                proxy,
                version, userProperties, assetIndex);

        this.configuration = configuration;
    }

    @Override
    public boolean isMainThread() {
        return Thread.currentThread() == this.mainThread;
    }

    @Override
    public void displayGuiScreen(GuiScreen guiScreen) {
        if (guiScreen instanceof GuiConnecting connecting)
            connecting.setConnectFunction((ip, port) -> {
                logger.info("Connecting to " + ip + ", " + port);
                ThreadManager.getAsyncExecutor().execute(() -> {
                    InetAddress iNetAddress = null;

                    try {
                        if (connecting.isCancelled())
                            return;

                        iNetAddress = InetAddress.getByName(ip);
                        connecting.networkManager = NetworkManager.provideLanClient(ClientInstance.this, iNetAddress,
                                port);
                        connecting.networkManager
                                .setNetHandler(new NetHandlerLoginClient(connecting.networkManager,
                                        ClientInstance.this, connecting.previousScreen));
                        connecting.networkManager.scheduleOutboundPacket(
                                new C00Handshake(5, ip, port, EnumConnectionState.LOGIN),
                                new GenericFutureListener[0]);
                        connecting.networkManager.scheduleOutboundPacket(
                                new C00PacketLoginStart(ClientInstance.this.getSession().getGameProfile()),
                                new GenericFutureListener[0]);
                    } catch (UnknownHostException var5) {
                        if (connecting.isCancelled())
                            return;

                        GuiConnecting.getLogger().error("Couldn\'t connect to server", var5);
                        ClientInstance.this.displayGuiScreen(new GuiDisconnected(ClientInstance.this,
                                connecting.previousScreen,
                                "connect.failed",
                                new ChatComponentTranslation("disconnect.genericReason",
                                        new Object[] { "Unknown host" })));
                    } catch (Exception e) {
                        if (connecting.isCancelled())
                            return;

                        GuiConnecting.getLogger().error("Couldn\'t connect to server", e);
                        var errorMessage = e.toString();

                        if (iNetAddress != null)
                            errorMessage = errorMessage.replaceAll(iNetAddress.toString() + ":" + port, "");

                        ClientInstance.this
                                .displayGuiScreen(new GuiDisconnected(ClientInstance.this,
                                        connecting.previousScreen, "connect.failed",
                                        new ChatComponentTranslation("disconnect.genericReason",
                                                new Object[] { errorMessage })));
                    }
                });
            });

        super.displayGuiScreen(guiScreen);
    }

    @Override
    public ScheduledExecutorService getGameLoopExecutor() {
        return ThreadManager.getGameLoopExecutor();
    }

    @Override
    public ExecutorService getAsyncExecutor() {
        return ThreadManager.getAsyncExecutor();
    }

    @Override
    public void runGameLoop() {
        if (this.mainThread == null)
            this.mainThread = Thread.currentThread();

        while (!delayedTasks.isEmpty()) {
            val task = delayedTasks.peek();
            if (task.canSend()) {
                task.runnable.run();
                delayedTasks.poll();
                continue;
            }

            break;
        }

        for (val goal : goals)
            if (goal.shouldExecute()) {
                goal.callGameLoop();
                break;
            }

        this.getKeyboard().onGameLoop(getSystemTime());
        this.getMouse().onGameLoop(getSystemTime());

        super.runGameLoop();
    }

    @Override
    public boolean schedule(Runnable runnable, long delay) {
        return this.scheduleTask(runnable, delay);
    }

    @Override
    public <T extends Event> boolean callEvent(T event) {
        var cancelled = false;

        for (val goal : goals)
            if (goal.shouldExecute()) {
                cancelled = goal.onEvent(event);
                break;
            }

        return cancelled;
    }

    @Override
    public void runTick() {
        super.runTick();

        val fakePlayer = getFakePlayer();

        if (fakePlayer == null)
            return;

        currentTick++;

        latency = (int) fakePlayer.getRandom().nextGaussian(getConfiguration().getLatency(),
                getConfiguration().getLatencyDeviation());

        for (val goal : goals)
            if (goal.shouldExecute()) {
                goal.onTick();
                break;
            }
    }

    @Override
    public void startGoals(Goal... goals) {
        for (val goal : goals) {
            for (val g : this.goals)
                if (g.getClass() == goal.getClass())
                    continue;
            this.goals.add(goal);
        }
    }

    @Override
    public void shutdownMinecraftApplet() {

        InstanceManager.getInstances().remove(configuration.getUuid());

        goals.clear();

        this.running = false;

        if (BotGlobalConfig.isDebug())
            logger.info("Stopping!");

        try {
            this.loadWorld((WorldClient) null);
        } catch (Throwable var7) {
            ;
        }

        if (this.mcSoundHandler != null)
            this.mcSoundHandler.func_147685_d();
    }

    @Override
    public boolean isRunning() {
        return this.running;
    }

    @Override
    public long timeMillis() {
        return getSystemTime();
    }

    @Override
    public Screen getCurrentScreen() {
        return this.currentScreen;
    }

    @Override
    public FakePlayer getFakePlayer() {
        return this.thePlayer instanceof FakePlayer fakePlayer ? fakePlayer : null;
    }

    @Override
    public Mouse newMouse() {
        return new gg.mineral.bot.impl.controls.Mouse();
    }

    @Override
    public Keyboard newKeyboard() {
        return new gg.mineral.bot.impl.controls.Keyboard();
    }
}
