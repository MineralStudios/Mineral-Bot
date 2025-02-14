package gg.mineral.bot.base.client.instance;

import com.google.common.collect.Multimap;
import gg.mineral.bot.api.configuration.BotConfiguration;
import gg.mineral.bot.api.controls.Keyboard;
import gg.mineral.bot.api.controls.Mouse;
import gg.mineral.bot.api.entity.living.player.FakePlayer;
import gg.mineral.bot.api.event.Event;
import gg.mineral.bot.api.goal.Goal;
import gg.mineral.bot.api.inv.Inventory;
import gg.mineral.bot.api.inv.InventoryContainer;
import gg.mineral.bot.api.math.BoundingBox;
import gg.mineral.bot.api.math.simulation.PlayerMotionSimulator;
import gg.mineral.bot.api.screen.Screen;
import gg.mineral.bot.api.world.ClientWorld;
import gg.mineral.bot.base.client.gui.GuiConnecting.ConnectFunction;
import gg.mineral.bot.base.client.manager.InstanceManager;
import gg.mineral.bot.impl.thread.ThreadManager;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.val;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.util.Session;
import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.net.Proxy;
import java.util.Queue;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

public class ClientInstance extends Minecraft implements gg.mineral.bot.api.instance.ClientInstance {

    @Getter
    private final BotConfiguration configuration;

    private final ObjectLinkedOpenHashSet<Goal> goals = new ObjectLinkedOpenHashSet<>();

    private final Queue<DelayedTask> delayedTasks = new ConcurrentLinkedQueue<>();
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

    public ClientInstance(@NonNull BotConfiguration configuration, int width, int height,
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
        if (!this.running)
            return;
        if (this.mainThread == null)
            this.mainThread = Thread.currentThread();

        boolean executing = false;
        for (val goal : goals)
            if (goal.isExecuting()) {
                goal.callGameLoop();
                executing = true;
                break;
            }

        if (!executing)
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
    }

    public void ensureMainThread(Runnable runnable) {
        if (isMainThread())
            runnable.run();
        else
            this.scheduleTask(runnable, 0);
    }

    @Override
    public boolean schedule(@NotNull Runnable runnable, long delay) {
        return this.scheduleTask(runnable, delay);
    }

    @Override
    public <T extends Event> boolean callEvent(@NotNull T event) {
        var cancelled = false;

        for (val goal : goals)
            if (goal.isExecuting())
                return goal.onEvent(event);

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

        currentTick++;

        latency = (int) fakePlayer.getRandom().nextGaussian(getConfiguration().getLatency(),
                getConfiguration().getLatencyDeviation());

        for (val goal : goals)
            if (goal.isExecuting()) {
                goal.onTick();
                return;
            }

        for (val goal : goals)
            if (goal.shouldExecute()) {
                goal.onTick();
                break;
            }
    }

    @SafeVarargs
    @Override
    public final <T extends Goal> void startGoals(T... goals) {
        for (val goal : goals) {
            if (this.goals.add(goal))
                info(this, "Added goal: " + goal.getClass().getSimpleName());
            else
                info(this, "Failed to add goal: " + goal.getClass().getSimpleName());
        }
    }

    @Override
    public void shutdownMinecraftApplet() {

        InstanceManager.getInstances().remove(configuration.getUuid());

        goals.clear();

        this.running = false;

        info(this, "Stopping!");

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
        else
            return new FakePlayer() {

                @Override
                public double getLastReportedX() {
                    return 0;
                }

                @Override
                public double getLastReportedY() {
                    return 0;
                }

                @Override
                public double getLastReportedZ() {
                    return 0;
                }

                @Override
                public @Nullable Inventory getInventory() {
                    return null;
                }

                @Override
                public @Nullable InventoryContainer getInventoryContainer() {
                    return null;
                }

                @Override
                public float getEyeHeight() {
                    return 0;
                }

                @Override
                public String getUsername() {
                    return configuration.getFullUsername();
                }

                @Override
                public float getHunger() {
                    return 20;
                }

                @Override
                public double getHeadY() {
                    return 0;
                }

                @Override
                public int[] getActivePotionEffectIds() {
                    return new int[0];
                }

                @Override
                public boolean isPotionActive(int potionId) {
                    return false;
                }

                @Override
                public float getHealth() {
                    return 0;
                }

                @Override
                public UUID getUuid() {
                    return configuration.getUuid();
                }

                @Override
                public @Nullable BoundingBox getCollidingBoundingBox() {
                    return null;
                }

                @Override
                public int getEntityId() {
                    return 0;
                }

                @Override
                public double getX() {
                    return 0;
                }

                @Override
                public double getY() {
                    return 0;
                }

                @Override
                public double getZ() {
                    return 0;
                }

                @Override
                public float getYaw() {
                    return 0;
                }

                @Override
                public float getPitch() {
                    return 0;
                }

                @Override
                public boolean isOnGround() {
                    return false;
                }

                @Override
                public double getLastX() {
                    return 0;
                }

                @Override
                public double getLastY() {
                    return 0;
                }

                @Override
                public double getLastZ() {
                    return 0;
                }

                @Override
                public double getMotionX() {
                    return 0;
                }

                @Override
                public double getMotionY() {
                    return 0;
                }

                @Override
                public double getMotionZ() {
                    return 0;
                }

                @Override
                public @Nullable ClientWorld getWorld() {
                    return null;
                }

                @Override
                public Random getRandom() {
                    return new Random();
                }

                @Override
                public BoundingBox getBoundingBox() {
                    return new BoundingBox() {
                        @Override
                        public void setMinX(double minX) {

                        }

                        @Override
                        public void setMinY(double minY) {

                        }

                        @Override
                        public void setMinZ(double minZ) {

                        }

                        @Override
                        public void setMaxX(double maxX) {

                        }

                        @Override
                        public void setMaxY(double maxY) {

                        }

                        @Override
                        public void setMaxZ(double maxZ) {

                        }

                        @Override
                        public double getMinX() {
                            return 0;
                        }

                        @Override
                        public double getMinY() {
                            return 0;
                        }

                        @Override
                        public double getMinZ() {
                            return 0;
                        }

                        @Override
                        public double getMaxX() {
                            return 0;
                        }

                        @Override
                        public double getMaxY() {
                            return 0;
                        }

                        @Override
                        public double getMaxZ() {
                            return 0;
                        }
                    };
                }

                @Override
                public boolean isSprinting() {
                    return false;
                }

                @Override
                public gg.mineral.bot.api.instance.ClientInstance getClientInstance() {
                    return ClientInstance.this;
                }

                @Override
                public PlayerMotionSimulator getMotionSimulator() {
                    if (getClientInstance() instanceof Minecraft mc)
                        return new gg.mineral.bot.base.client.math.simulation.PlayerMotionSimulator(mc, this);
                    else
                        throw new IllegalStateException("Client instance is not an instance of Minecraft!");
                }

            };
    }

    @Override
    public Mouse newMouse() {
        return new gg.mineral.bot.base.lwjgl.input.Mouse(this);
    }

    @Override
    public Keyboard newKeyboard() {
        return new gg.mineral.bot.base.lwjgl.input.Keyboard(this);
    }

    @Override
    public int getDisplayHeight() {
        return this.displayHeight;
    }

    @Override
    public int getDisplayWidth() {
        return this.displayWidth;
    }
}
