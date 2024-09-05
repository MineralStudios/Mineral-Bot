package gg.mineral.bot.api.entity.living.player;

import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import org.eclipse.jdt.annotation.NonNull;
import org.eclipse.jdt.annotation.Nullable;

import gg.mineral.bot.api.configuration.BotConfiguration;
import gg.mineral.bot.api.controls.Keyboard;
import gg.mineral.bot.api.controls.Mouse;
import gg.mineral.bot.api.event.Event;
import gg.mineral.bot.api.goal.Goal;
import gg.mineral.bot.api.screen.Screen;
import gg.mineral.bot.api.world.ClientWorld;

public interface FakePlayer extends ClientPlayer {
    /**
     * Gets the world.
     * 
     * @return the world
     */
    @Nullable
    ClientWorld getWorld();

    /**
     * Gets the keyboard.
     * 
     * @return the keyboard
     */
    Keyboard getKeyboard();

    /**
     * Gets the mouse.
     * 
     * @return the mouse
     */
    Mouse getMouse();

    /**
     * Gets the bot configuration.
     * 
     * @return the bot configuration
     */
    BotConfiguration getConfiguration();

    /**
     * Gets the game loop executor.
     * 
     * @return the game loop executor
     */
    ScheduledExecutorService getGameLoopExecutor();

    /**
     * Gets the async executor.
     * 
     * @return the async executor
     */
    ExecutorService getAsyncExecutor();

    /**
     * Schedules a task to run after a delay.
     * 
     * @param runnable
     *            the task to run
     * @param delay
     *            the delay in milliseconds
     * 
     * 
     * @return the scheduled future
     */
    ScheduledFuture<?> schedule(@NonNull Runnable runnable, long delay);

    /**
     * Schedules a task to run when an event occurs.
     * 
     * @param event
     *            the event
     * 
     * @return true if the event was cancelled
     * 
     */
    <T extends Event> boolean callEvent(@NonNull T event);

    /**
     * Gets the player's username.
     * 
     * @return the player's username
     */
    String getUsername();

    /**
     * Gets the player's access token.
     * 
     * @return the player's access token
     */
    String getAccessToken();

    /**
     * Gets the player's session ID.
     * 
     * @return the player's session ID
     */
    String getSessionId();

    /**
     * Gets the player's eye height.
     * 
     * @return the player's eye height
     */
    float getEyeHeight();

    /**
     * Gets the player's friendly entity UUIDs.
     * 
     * @return the player's friendly entity UUIDs
     */
    Set<UUID> getFriendlyEntityUUIDs();

    /**
     * Gets the player's random.
     * 
     * @return the player's random
     */
    Random getRandom();

    /**
     * Sets the player's goals.
     * 
     * @param goals
     *            the goals
     */
    void startGoals(Goal... goals);

    /**
     * Checks if the client is running.
     * 
     * @return true if the client is running
     */
    boolean isRunning();

    /**
     * Gets the time in milliseconds.
     * 
     * @return the time in milliseconds
     */
    long timeMillis();

    /**
     * Gets the current latency.
     * 
     * @return the current latency
     */
    int getLatency();

    /**
     * Returns the current screen that the fake player is viewing.
     *
     * @return the current screen of the fake player
     */
    Screen getCurrentScreen();
}
