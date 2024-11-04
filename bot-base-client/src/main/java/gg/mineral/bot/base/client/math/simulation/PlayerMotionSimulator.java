package gg.mineral.bot.base.client.math.simulation;

import org.eclipse.jdt.annotation.NonNull;

import gg.mineral.bot.api.controls.Key;
import gg.mineral.bot.api.entity.living.player.ClientPlayer;
import gg.mineral.bot.api.event.Event;
import gg.mineral.bot.api.event.EventHandler;
import gg.mineral.bot.base.lwjgl.input.Keyboard;
import gg.mineral.bot.base.lwjgl.input.Mouse;
import lombok.Getter;
import lombok.val;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.MovementInput;
import net.minecraft.util.Session;
import net.minecraft.world.World;

public class PlayerMotionSimulator extends EntityPlayerSP
        implements gg.mineral.bot.api.math.simulation.PlayerMotionSimulator {

    private final Timer timer = new Timer(20.0f);

    private int millis;

    private static EventHandler EMPTY_EVENT_HANDLER = new EventHandler() {

        @Override
        public <T extends Event> boolean callEvent(@NonNull T event) {
            return false;
        }

    };

    @Getter
    private final Keyboard keyboard = new Keyboard(EMPTY_EVENT_HANDLER);
    @Getter
    private final Mouse mouse = new Mouse(EMPTY_EVENT_HANDLER);

    public PlayerMotionSimulator(Minecraft mc, ClientPlayer player) {
        super(mc, (World) player.getWorld(), new Session(player.getUsername(), player.getUuid().toString(),
                "0",
                "legacy"), 0);

        this.movementInput = new MovementInput() {
            @Override
            public void updatePlayerMoveState() {
                this.moveStrafe = 0.0F;
                this.moveForward = 0.0F;

                if (keyboard.isKeyDown(Key.Type.KEY_W))
                    ++this.moveForward;

                if (keyboard.isKeyDown(Key.Type.KEY_S))
                    --this.moveForward;

                if (keyboard.isKeyDown(Key.Type.KEY_A))
                    ++this.moveStrafe;

                if (keyboard.isKeyDown(Key.Type.KEY_D))
                    --this.moveStrafe;

                this.jump = keyboard.isKeyDown(Key.Type.KEY_SPACE);
                this.sneak = keyboard.isKeyDown(Key.Type.KEY_LSHIFT);

                if (this.sneak) {
                    this.moveStrafe = (float) ((double) this.moveStrafe * 0.3D);
                    this.moveForward = (float) ((double) this.moveForward * 0.3D);
                }
            }
        };

    }

    @Override
    public void execute(long time) {
        for (millis = 0; millis < time; millis++)
            this.runGameLoop();
    }

    public void runGameLoop() {
        this.timer.updateTimer();

        for (int i = 0; i < this.timer.elapsedTicks; ++i)
            this.onUpdate();

        this.mc.mouseHelper.mouseXYChange();
        float var132 = 0.5f * 0.6F + 0.2F;
        float var141 = var132 * var132 * var132 * 8.0F;
        float var15 = (float) this.mouse.getDX() * var141;
        float var16 = (float) this.mouse.getDY() * var141;
        byte var17 = 1;

        this.setAngles(var15, var16 * (float) var17);
    }

    public class Timer {
        /** The number of timer ticks per second of real time */
        float ticksPerSecond;

        /**
         * The time reported by the high-resolution clock at the last call of
         * updateTimer(), in seconds
         */
        private double lastHighResTime;

        /**
         * How many full ticks have turned over since the last call to updateTimer(),
         * capped at 10.
         */
        public int elapsedTicks;

        /**
         * How much time has elapsed since the last tick, in ticks, for use by display
         * rendering routines (range: 0.0 - 1.0). This field is frozen if the display
         * is paused to eliminate jitter.
         */
        public float renderPartialTicks;

        /**
         * A multiplier to make the timer (and therefore the game) go faster or slower.
         * 0.5 makes the game run at half-speed.
         */
        public float timerSpeed = 1.0F;

        /**
         * How much time has elapsed since the last tick, in ticks (range: 0.0 - 1.0).
         */
        public float elapsedPartialTicks;

        /**
         * The time reported by the system clock at the last sync, in milliseconds
         */
        private long lastSyncSystemClock;

        /**
         * The time reported by the high-resolution clock at the last sync, in
         * milliseconds
         */
        private long lastSyncHighResClock;

        /** Accumulated time between syncs, used to adjust timeSyncRatio */
        private long accumulatedTimeBetweenSyncs;

        /**
         * A ratio used to sync the high-resolution clock to the system clock, updated
         * once per second
         */
        private double timeSyncRatio = 1.0D;

        public Timer(float ticksPerSecond) {
            this.ticksPerSecond = ticksPerSecond;
            this.lastSyncSystemClock = millis;
            this.lastSyncHighResClock = millis;
        }

        /**
         * Updates all fields of the Timer using the current time
         */
        public void updateTimer() {
            val currentSystemTime = millis;
            val timeSinceLastSync = currentSystemTime - this.lastSyncSystemClock;
            val currentHighResTime = millis;
            val currentHighResSeconds = currentHighResTime / 1000.0D;

            if (timeSinceLastSync <= 1000L && timeSinceLastSync >= 0L) {
                this.accumulatedTimeBetweenSyncs += timeSinceLastSync;

                if (this.accumulatedTimeBetweenSyncs > 1000L) {
                    val highResTimeSinceLastSync = currentHighResTime - this.lastSyncHighResClock;
                    val newTimeSyncRatio = (double) this.accumulatedTimeBetweenSyncs
                            / (double) highResTimeSinceLastSync;
                    this.timeSyncRatio += (newTimeSyncRatio - this.timeSyncRatio) * 0.2D;
                    this.lastSyncHighResClock = currentHighResTime;
                    this.accumulatedTimeBetweenSyncs = 0L;
                }

                if (this.accumulatedTimeBetweenSyncs < 0L)
                    this.lastSyncHighResClock = currentHighResTime;

            } else
                this.lastHighResTime = currentHighResSeconds;

            this.lastSyncSystemClock = currentSystemTime;
            var timeDelta = (currentHighResSeconds - this.lastHighResTime) * this.timeSyncRatio;
            this.lastHighResTime = currentHighResSeconds;

            if (timeDelta < 0.0D)
                timeDelta = 0.0D;

            if (timeDelta > 1.0D)
                timeDelta = 1.0D;

            this.elapsedPartialTicks += timeDelta * this.timerSpeed * this.ticksPerSecond;
            this.elapsedTicks = (int) this.elapsedPartialTicks;
            this.elapsedPartialTicks -= this.elapsedTicks;

            if (this.elapsedTicks > 10)
                this.elapsedTicks = 10;

            this.renderPartialTicks = this.elapsedPartialTicks;
        }
    }

}
