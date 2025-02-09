package gg.mineral.bot.api.instance;

import gg.mineral.bot.api.configuration.BotConfiguration;
import gg.mineral.bot.api.controls.Keyboard;
import gg.mineral.bot.api.controls.Mouse;
import gg.mineral.bot.api.entity.living.player.FakePlayer;
import gg.mineral.bot.api.event.EventHandler;
import gg.mineral.bot.api.goal.Goal;
import gg.mineral.bot.api.math.optimization.Optimizer;
import gg.mineral.bot.api.math.optimization.RecursiveCalculation;
import gg.mineral.bot.api.screen.Screen;
import org.eclipse.jdt.annotation.NonNull;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Function;

public interface ClientInstance extends EventHandler {

    /**
     * Sets the player's goals.
     *
     * @param goals the goals
     */
    <T extends Goal> void startGoals(@SuppressWarnings("unchecked") T... goals);

    /**
     * Gets the current latency.
     *
     * @return the current latency
     */
    int getLatency();

    /**
     * Gets the current tick.
     *
     * @return the current tick
     */
    int getCurrentTick();

    /**
     * Returns the current screen that the fake player is viewing.
     *
     * @return the current screen of the fake player
     */
    Screen getCurrentScreen();

    /**
     * Gets the bot configuration.
     *
     * @return the bot configuration
     */
    BotConfiguration getConfiguration();

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
     * Gets the fake player.
     *
     * @return the fake player
     */
    FakePlayer getFakePlayer();

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
     * @param runnable the task to run
     * @param delay    the delay in milliseconds
     * @return true if the task was executed immediately
     */
    boolean schedule(@NonNull Runnable runnable, long delay);

    /**
     * Gets the player's session.
     *
     * @return the player's session
     */
    Session getSession();

    /**
     * Shuts down the instance.
     */
    void shutdown();

    /**
     * Create a new mouse.
     *
     * @return the new mouse
     */
    Mouse newMouse();

    /**
     * Create a new keyboard.
     *
     * @return the new keyboard
     */
    Keyboard newKeyboard();

    /**
     * Gets the display height.
     *
     * @return the display height
     */
    int getDisplayHeight();

    /**
     * Gets the display width.
     *
     * @return the display width
     */
    int getDisplayWidth();


    /**
     * Creates a new univariate optimizer.
     *
     * @param callable      the callable (obtains the input to optimize)
     * @param valueFunction the value function (obtains the value to optimize)
     * @param maxEval       the maximum number of evaluations
     * @return the optimizer data
     */
    <C extends RecursiveCalculation> Optimizer.Data<C, Number> univariateOptimizer(Callable<C> callable,
                                                                                   Function<C, Number> valueFunction,
                                                                                   int maxEval);

    /**
     * Creates a new bivariate optimizer.
     *
     * @param callable      the callable (obtains the input to optimize)
     * @param valueFunction the value function (obtains the value to optimize)
     * @param maxEval       the maximum number of evaluations
     * @return the optimizer data
     */
    <C extends RecursiveCalculation> Optimizer.Data<C, Number[]> bivariateOptimizer(Callable<C> callable,
                                                                                    Function<C, Number> valueFunction,
                                                                                    int maxEval);
}
