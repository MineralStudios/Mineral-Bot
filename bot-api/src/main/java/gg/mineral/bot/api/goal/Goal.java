package gg.mineral.bot.api.goal;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.eclipse.jdt.annotation.NonNull;

import gg.mineral.bot.api.entity.living.player.FakePlayer;
import gg.mineral.bot.api.event.Event;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public abstract class Goal {
    @NonNull
    protected final FakePlayer fakePlayer;
    private Queue<DelayedTask> delayedTasks = new ConcurrentLinkedQueue<>();

    protected static long timeMillis() {
        return System.nanoTime() / 1000000;
    }

    public record DelayedTask(Runnable runnable, long sendTime) {
        public boolean canSend() {
            return timeMillis() >= sendTime;
        }
    }

    public abstract boolean shouldExecute();

    public abstract void onTick();

    public abstract boolean onEvent(Event event);

    public abstract void onGameLoop();

    public boolean schedule(Runnable runnable, int delay) {
        if (delay <= 0 && delayedTasks.isEmpty()) {
            runnable.run();
            return true;
        }
        delayedTasks.add(new DelayedTask(runnable, timeMillis() + delay));
        return false;
    }
}
