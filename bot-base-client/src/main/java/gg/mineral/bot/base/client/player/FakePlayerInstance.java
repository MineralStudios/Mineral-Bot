package gg.mineral.bot.base.client.player;

import java.io.File;
import java.net.Proxy;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

import org.eclipse.jdt.annotation.Nullable;

import com.google.common.collect.Multimap;

import gg.mineral.bot.api.configuration.BotConfiguration;
import gg.mineral.bot.api.entity.living.player.FakePlayer;
import gg.mineral.bot.api.event.Event;
import gg.mineral.bot.api.goal.Goal;
import gg.mineral.bot.api.inv.Inventory;
import gg.mineral.bot.api.inv.InventoryContainer;
import gg.mineral.bot.api.math.BoundingBox;
import gg.mineral.bot.api.screen.Screen;
import gg.mineral.bot.api.world.ClientWorld;
import gg.mineral.bot.base.client.manager.InstanceManager;
import gg.mineral.bot.impl.config.BotGlobalConfig;
import gg.mineral.bot.impl.thread.ThreadManager;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import lombok.Getter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.Session;

public class FakePlayerInstance extends Minecraft implements FakePlayer {

    @Getter
    private final BotConfiguration configuration;

    private Set<UUID> friendlyEntityUUIDS = new ObjectOpenHashSet<>();

    private Set<Goal> goals = new ObjectLinkedOpenHashSet<>();

    private Queue<DelayedTask> delayedTasks = new ConcurrentLinkedQueue<>();

    @Getter
    private int latency = 0;

    record DelayedTask(Runnable runnable, long sendTime) {
        public boolean canSend() {
            return getSystemTime() >= sendTime;
        }
    }

    public boolean scheduleTask(Runnable runnable, long delay) {
        if (delay <= 0 && delayedTasks.isEmpty()) {
            runnable.run();
            return true;
        }
        delayedTasks.add(new DelayedTask(runnable, getSystemTime() + delay));
        return false;
    }

    public FakePlayerInstance(BotConfiguration configuration, int width, int height,
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
    public ScheduledExecutorService getGameLoopExecutor() {
        return ThreadManager.getGameLoopExecutor();
    }

    @Override
    public ExecutorService getAsyncExecutor() {
        return ThreadManager.getAsyncExecutor();
    }

    @Override
    public String getUsername() {
        return this.getSession().getUsername();
    }

    @Override
    public UUID getUuid() {
        return this.thePlayer == null ? EntityPlayer.getUUIDFromGameProfile(this.getSession().getGameProfile())
                : this.thePlayer.getUniqueID();
    }

    @Override
    public String getAccessToken() {
        return this.getSession().getToken();
    }

    @Override
    public String getSessionId() {
        return this.getSession().getSessionID();
    }

    @Override
    public double getX() {
        return this.thePlayer == null ? 0.0 : this.thePlayer.posX;
    }

    @Override
    public double getY() {
        return this.thePlayer == null ? 0.0 : this.thePlayer.posY;
    }

    @Override
    public double getZ() {
        return this.thePlayer == null ? 0.0 : this.thePlayer.posZ;
    }

    @Override
    public float getYaw() {
        return this.thePlayer == null ? 0.0f : this.thePlayer.rotationYaw;
    }

    @Override
    public float getPitch() {
        return this.thePlayer == null ? 0.0f : this.thePlayer.rotationPitch;
    }

    @Override
    public float getEyeHeight() {
        return this.thePlayer == null ? 0.0f : this.thePlayer.getEyeHeight();
    }

    @Override
    public void runGameLoop() {
        super.runGameLoop();

        this.getKeyboard().onGameLoop(getSystemTime());
        this.getMouse().onGameLoop(getSystemTime());

        while (!delayedTasks.isEmpty()) {
            DelayedTask task = delayedTasks.peek();
            if (task.canSend()) {
                task.runnable.run();
                delayedTasks.poll();
                continue;
            }

            break;
        }

        for (Goal goal : goals)
            if (goal.shouldExecute()) {

                goal.onGameLoop();

                while (!goal.getDelayedTasks().isEmpty()) {
                    Goal.DelayedTask task = goal.getDelayedTasks().peek();
                    if (task.canSend()) {
                        task.runnable().run();
                        goal.getDelayedTasks().poll();
                        continue;
                    }

                    break;
                }

                break;
            }
    }

    @Override
    public boolean schedule(Runnable runnable, long delay) {
        return this.scheduleTask(runnable, delay);
    }

    @Override
    @Nullable
    public Inventory getInventory() {
        return this.thePlayer == null ? null : this.thePlayer.getInventory();
    }

    @Override
    public <T extends Event> boolean callEvent(T event) {
        boolean cancelled = false;

        for (Goal goal : goals)
            if (goal.shouldExecute()) {
                cancelled = goal.onEvent(event);
                break;
            }

        return cancelled;
    }

    @Override
    @Nullable
    public ClientWorld getWorld() {
        return this.theWorld;
    }

    @Override
    @Nullable
    public BoundingBox getBoundingBox() {
        return this.thePlayer == null ? null : this.thePlayer.boundingBox;
    }

    @Override
    public Set<UUID> getFriendlyEntityUUIDs() {
        return friendlyEntityUUIDS;
    }

    @Override
    public double getHeadY() {
        return this.thePlayer == null ? 0.0f : this.thePlayer.getHeadY();
    }

    @Override
    public Random getRandom() {
        return this.thePlayer == null ? new Random() : this.thePlayer.getRNG();
    }

    @Override
    public void runTick() {
        super.runTick();

        latency = (int) getRandom().nextGaussian(getConfiguration().getLatency(),
                getConfiguration().getLatencyDeviation());

        for (Goal goal : goals)
            if (goal.shouldExecute()) {
                goal.onTick();
                break;
            }
    }

    @Override
    public void startGoals(Goal... goals) {
        for (Goal goal : goals) {
            for (Goal g : this.goals)
                if (g.getClass() == goal.getClass())
                    continue;
            this.goals.add(goal);
        }
    }

    @Override
    public void shutdownMinecraftApplet() {

        InstanceManager.getInstances().remove(this.getUuid());

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
    public int getEntityId() {
        return this.thePlayer == null ? -1 : this.thePlayer.getEntityId();
    }

    @Override
    public @Nullable InventoryContainer getInventoryContainer() {
        return this.thePlayer == null ? null : this.thePlayer.getInventoryContainer();
    }

    @Override
    public Screen getCurrentScreen() {
        return this.currentScreen;
    }

    @Override
    public boolean isOnGround() {
        return this.thePlayer == null ? false : this.thePlayer.onGround;
    }

    @Override
    public double getLastX() {
        return this.thePlayer == null ? 0.0 : this.thePlayer.lastTickPosX;
    }

    @Override
    public double getLastY() {
        return this.thePlayer == null ? 0.0 : this.thePlayer.lastTickPosY;
    }

    @Override
    public double getLastZ() {
        return this.thePlayer == null ? 0.0 : this.thePlayer.lastTickPosZ;
    }

    @Override
    public int[] getActivePotionEffectIds() {
        return this.thePlayer == null ? new int[0] : this.thePlayer.getActivePotionEffectIds();
    }
}
