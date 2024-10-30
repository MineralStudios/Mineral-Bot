package gg.mineral.bot.impl.controls;

import gg.mineral.bot.api.controls.Key.Type;
import gg.mineral.bot.impl.controls.KeyboardState.Log;
import lombok.Getter;
import lombok.val;

@Getter
public class Keyboard implements gg.mineral.bot.api.controls.Keyboard {
    private KeyboardState state = new KeyboardState();

    public void onGameLoop(long time) {
        val iter = state.getScheduledTasks().object2LongEntrySet().fastIterator();

        while (iter.hasNext()) {
            val entry = iter.next();
            if (time >= entry.getLongValue()) {
                entry.getKey().run();
                iter.remove();
            }
        }
    }

    public void schedule(Runnable runnable, long delay) {
        state.getScheduledTasks().put(runnable, (System.nanoTime() / 1000000) + delay);
    }

    @Override
    public Key getKey(Key.Type type) {
        return state.getKey(type);
    }

    @Override
    public void pressKey(int durationMillis, Key.Type... types) {
        for (val type : types) {
            val key = getKey(type);

            if (key == null || key.isPressed())
                return;

            key.setPressed(true);
            val currentLog = state.getCurrentLog();
            if (currentLog != null)
                state.getLogs().add(currentLog);
            state.setCurrentLog(new Log(type, true));
            if (durationMillis > 0 && durationMillis < Integer.MAX_VALUE)
                schedule(() -> unpressKey(type), durationMillis);
        }
    }

    @Override
    public void unpressKey(int durationMillis, Key.Type... types) {
        for (val type : types) {
            val key = getKey(type);

            if (key == null || !key.isPressed())
                return;

            key.setPressed(false);
            val currentLog = state.getCurrentLog();
            if (currentLog != null)
                state.getLogs().add(currentLog);
            state.setCurrentLog(new Log(type, false));

            if (durationMillis > 0 && durationMillis < Integer.MAX_VALUE)
                schedule(() -> pressKey(type), durationMillis);
        }
    }

    @Override
    public boolean next() {
        return state.next();
    }

    @Override
    public int getEventKey() {
        return state.getEventKey();
    }

    @Override
    public Type getEventKeyType() {
        return state.getEventKeyType();
    }

    @Override
    public boolean isKeyDown(Key.Type type) {
        val key = getKey(type);
        return key != null && key.isPressed();
    }

    @Override
    public boolean getEventKeyState() {
        val eventLog = state.getEventLog();
        return eventLog != null && eventLog.pressed();
    }

    @Override
    public void stopAll() {
        unpressKey(Key.Type.values());
        state.getScheduledTasks().clear();
    }

    @Override
    public void setState(gg.mineral.bot.api.controls.KeyboardState state) {
        if (state instanceof KeyboardState newState)
            this.state = newState;
    }

}
