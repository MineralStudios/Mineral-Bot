package gg.mineral.bot.api.goal;

import gg.mineral.bot.api.controls.Key;
import gg.mineral.bot.api.controls.Keyboard;
import gg.mineral.bot.api.controls.Mouse;
import gg.mineral.bot.api.controls.MouseButton;
import gg.mineral.bot.api.debug.Logger;
import gg.mineral.bot.api.event.Event;
import gg.mineral.bot.api.instance.ClientInstance;
import gg.mineral.bot.api.util.MathUtil;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.eclipse.jdt.annotation.NonNull;

import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

@Getter
@RequiredArgsConstructor
public abstract class Goal implements MathUtil, Logger {
    @NonNull
    protected final ClientInstance clientInstance;
    protected Queue<DelayedTask> delayedTasks = new ConcurrentLinkedQueue<>();

    protected static long timeMillis() {
        return System.nanoTime() / 1000000;
    }

    public record DelayedTask(Runnable runnable, long sendTime) {
        public boolean canSend() {
            return timeMillis() >= sendTime;
        }
    }

    public abstract boolean shouldExecute();

    public abstract boolean isExecuting();

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

    private Mouse getMouse() {
        return clientInstance.getMouse();
    }

    private Keyboard getKeyboard() {
        return clientInstance.getKeyboard();
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
        val rotYaw = fakePlayer.getYaw();
        getMouse().changeYaw(angleDifference(rotYaw, yaw));
    }

    public void setMousePitch(float pitch) {
        val fakePlayer = clientInstance.getFakePlayer();
        val rotPitch = fakePlayer.getPitch();
        getMouse().changePitch(angleDifference(rotPitch, pitch));
    }

    public MouseButton getButton(MouseButton.Type type) {
        return getMouse().getButton(type);
    }

    public Key getKey(Key.Type type) {
        return getKeyboard().getKey(type);
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

    public void stopAll() {
        getMouse().stopAll();
        getKeyboard().stopAll();
    }

    public void mouseStopAll() {
        getMouse().stopAll();
    }

    public void keyboardStopAll() {
        getKeyboard().stopAll();
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
    }

    @Override
    public UUID getIdentifier() {
        return clientInstance.getConfiguration().getUuid();
    }
}
