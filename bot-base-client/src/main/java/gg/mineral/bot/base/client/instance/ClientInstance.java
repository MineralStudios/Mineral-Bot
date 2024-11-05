package gg.mineral.bot.base.client.instance;

import java.io.File;
import java.net.Proxy;
import java.util.Queue;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.google.common.collect.Multimap;

import gg.mineral.bot.api.configuration.BotConfiguration;
import gg.mineral.bot.api.controls.Keyboard;
import gg.mineral.bot.api.controls.Mouse;
import gg.mineral.bot.api.entity.living.player.FakePlayer;
import gg.mineral.bot.api.event.Event;
import gg.mineral.bot.api.goal.Goal;
import gg.mineral.bot.api.screen.Screen;

import gg.mineral.bot.base.client.gui.GuiConnecting.ConnectFunction;
import gg.mineral.bot.base.client.manager.InstanceManager;
import gg.mineral.bot.impl.config.BotGlobalConfig;
import gg.mineral.bot.impl.thread.ThreadManager;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.val;
import net.minecraft.client.Minecraft;

import net.minecraft.client.multiplayer.WorldClient;

import net.minecraft.util.Session;

public class ClientInstance extends Minecraft implements gg.mineral.bot.api.instance.ClientInstance {

    @Getter
    private final BotConfiguration configuration;

    private ObjectOpenHashSet<Goal> goals = new ObjectOpenHashSet<>();

    private Queue<DelayedTask> delayedTasks = new ConcurrentLinkedQueue<>();
    private Thread mainThread = null;
    @Getter
    private int latency = 0, currentTick;
    private CompletableFuture<FakePlayer> fakePlayerFuture = new CompletableFuture<>();

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

    public ClientInstance(BotConfiguration configuration, int width, int height,
            boolean fullscreen,
            boolean demo, File gameDir, File assetsDir, File resourcePackDir, Proxy proxy, String version,
            @SuppressWarnings("rawtypes") Multimap userProperties, String assetIndex, ConnectFunction connectFunction) {
        this(configuration, width, height, fullscreen, demo, gameDir, assetsDir, resourcePackDir, proxy, version,
                userProperties, assetIndex);

    }

    @Override
    public boolean isMainThread() {
        return Thread.currentThread() == this.mainThread;
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

        for (val goal : goals)
            if (goal.shouldExecute()) {
                goal.callGameLoop();
                break;
            }

        while (!delayedTasks.isEmpty()) {
            val task = delayedTasks.peek();
            if (task.canSend()) {
                task.runnable.run();
                delayedTasks.poll();
                continue;
            }

            break;
        }

        this.keyboard.onGameLoop(getSystemTime());
        this.mouse.onGameLoop(getSystemTime());

        super.runGameLoop();

        val fakePlayer = getFakePlayer();

        if (fakePlayer != null && !fakePlayerFuture.isDone())
            fakePlayerFuture.complete(fakePlayer);
    }

    public void ensureMainThread(Runnable runnable) {
        if (isMainThread())
            runnable.run();
        else
            this.scheduleTask(runnable, 0);
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
        for (val goal : goals)
            this.goals.add(goal);
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
    @SneakyThrows
    public FakePlayer getFakePlayer() {
        if (this.thePlayer instanceof FakePlayer fakePlayer)
            return fakePlayer;
        else if (!isMainThread())
            return fakePlayerFuture.get(10, TimeUnit.SECONDS);
        else
            return null;
    }

    @Override
    public Mouse newMouse() {
        return new gg.mineral.bot.base.lwjgl.input.Mouse(this);
    }

    @Override
    public Keyboard newKeyboard() {
        return new gg.mineral.bot.base.lwjgl.input.Keyboard(this);
    }
}
