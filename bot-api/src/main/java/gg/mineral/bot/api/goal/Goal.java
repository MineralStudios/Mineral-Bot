package gg.mineral.bot.api.goal;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.eclipse.jdt.annotation.NonNull;

import gg.mineral.bot.api.controls.Key;
import gg.mineral.bot.api.controls.Keyboard;
import gg.mineral.bot.api.controls.Mouse;
import gg.mineral.bot.api.controls.MouseButton;
import gg.mineral.bot.api.entity.living.player.FakePlayer;
import gg.mineral.bot.api.event.Event;
import gg.mineral.bot.api.instance.ClientInstance;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;

@Getter
@RequiredArgsConstructor
public abstract class Goal {
    @NonNull
    protected final ClientInstance clientInstance;
    private Queue<DelayedTask> delayedTasks = new ConcurrentLinkedQueue<>();
    private Mouse mouse;
    private Keyboard keyboard;

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

    protected abstract void onGameLoop();

    public boolean schedule(Runnable runnable, int delay) {
        if (delay <= 0 && delayedTasks.isEmpty()) {
            runnable.run();
            return true;
        }
        delayedTasks.add(new DelayedTask(runnable, timeMillis() + delay));
        return false;
    }

    public int getMouseX() {
        return getMouse().getX();
    }

    public int getMouseY() {
        return getMouse().getY();
    }

    public void setMouseX(int x) {
        getMouse().setX(x);
    }

    public void setMouseY(int y) {
        getMouse().setY(y);
    }

    public void setMouseYaw(float yaw) {
        val fakePlayer = clientInstance.getFakePlayer();

        if (fakePlayer == null)
            return;
        val rotYaw = fakePlayer.getYaw();
        getMouse().changeYaw(yaw - rotYaw);
    }

    public void setMousePitch(float pitch) {
        val fakePlayer = clientInstance.getFakePlayer();

        if (fakePlayer == null)
            return;
        val rotPitch = fakePlayer.getPitch();
        getMouse().changePitch(pitch - rotPitch);
    }

    public MouseButton getButton(MouseButton.Type type) {
        return getMouse().getButton(type);
    }

    public void pressKey(int durationMillis, Key.Type... types) {
        getKeyboard().pressKey(durationMillis, types);
    }

    public void pressKey(Key.Type... types) {
        getKeyboard().pressKey(types);
    }

    public void unpressKey(int durationMillis, Key.Type... types) {
        getKeyboard().unpressKey(durationMillis, types);
    }

    public void unpressKey(Key.Type... types) {
        getKeyboard().unpressKey(types);
    }

    public void pressButton(int durationMillis, MouseButton.Type... types) {
        getMouse().pressButton(durationMillis, types);
    }

    public void pressButton(MouseButton.Type... types) {
        getMouse().pressButton(types);
    }

    public void unpressButton(int durationMillis, MouseButton.Type... types) {
        getMouse().unpressButton(durationMillis, types);
    }

    public void unpressButton(MouseButton.Type... types) {
        getMouse().unpressButton(types);
    }

    public void callGameLoop() {
        this.onGameLoop();
        while (!delayedTasks.isEmpty()) {
            val task = delayedTasks.peek();
            if (task.canSend()) {
                task.runnable().run();
                delayedTasks.poll();
                continue;
            }

            break;
        }

        val mouseState = getMouse().getState();
        val keyboardState = getKeyboard().getState();

        clientInstance.getMouse().setState(mouseState);
        clientInstance.getKeyboard().setState(keyboardState);
    }
}
