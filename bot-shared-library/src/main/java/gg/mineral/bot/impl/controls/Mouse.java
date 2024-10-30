package gg.mineral.bot.impl.controls;

import gg.mineral.bot.api.controls.MouseButton.Type;
import gg.mineral.bot.impl.controls.MouseState.Log;
import lombok.Getter;

import lombok.val;

@Getter
public class Mouse implements gg.mineral.bot.api.controls.Mouse {
    private MouseState state = new MouseState();

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
    public MouseButton getButton(MouseButton.Type type) {
        return state.getButton(type);
    }

    @Override
    public void pressButton(int durationMillis, MouseButton.Type... types) {
        for (val type : types) {
            val button = getButton(type);

            if (button == null || button.isPressed())
                return;

            button.setPressed(true);
            val currentLog = state.getCurrentLog();
            if (currentLog != null)
                state.getLogs().add(currentLog);
            state.setCurrentLog(new Log(type, true, state.x, state.y, state.dX, state.dY,
                    state.dWheel));
            if (durationMillis > 0 && durationMillis < Integer.MAX_VALUE)
                schedule(() -> unpressButton(type), durationMillis);
        }
    }

    @Override
    public void unpressButton(int durationMillis, MouseButton.Type... types) {
        for (val type : types) {
            val button = getButton(type);

            if (button == null || !button.isPressed())
                return;

            button.setPressed(false);
            val currentLog = state.getCurrentLog();
            if (currentLog != null)
                state.getLogs().add(currentLog);
            state.setCurrentLog(new Log(type, false, state.x, state.y, state.dX, state.dY, state.dWheel));
        }
    }

    @Override
    public boolean next() {
        return state.next();
    }

    @Override
    public MouseButton.Type getEventButtonType() {
        return state.getEventButtonType();
    }

    public boolean isButtonDown(int i) {
        val type = MouseButton.Type.fromKeyCode(i);
        if (type == null)
            return false;
        return isButtonDown(type);
    }

    public boolean isButtonDown(Type type) {
        return getButton(type).isPressed();
    }

    @Override
    public int getEventButton() {
        return state.getEventButton();
    }

    @Override
    public void setX(int x) {
        state.dX = x - state.x;
        state.x = x;
        val currentLog = state.getCurrentLog();
        if (currentLog != null)
            state.setCurrentLog(new Log(currentLog.type(), currentLog.pressed(), state.x, state.y, state.dX,
                    state.dY, state.dWheel));
        else
            state.setCurrentLog(new Log(Type.UNKNOWN, false, state.x, state.y, state.dX, state.dY, state.dWheel));
    }

    @Override
    public void setY(int y) {
        state.dY = y - state.y;
        state.y = y;
        val currentLog = state.getCurrentLog();
        if (currentLog != null)
            state.setCurrentLog(new Log(currentLog.type(), currentLog.pressed(), state.x, state.y, state.dX,
                    state.dY, state.dWheel));
        else
            state.setCurrentLog(new Log(Type.UNKNOWN, false, state.x, state.y, state.dX, state.dY, state.dWheel));
    }

    @Override
    public int getDX() {
        try {
            return state.dX;
        } finally {
            state.dX = 0;
        }
    }

    @Override
    public int getDY() {
        try {
            return state.dY;
        } finally {
            state.dY = 0;
        }
    }

    public int getEventDWheel() {
        val eventLog = state.getEventLog();
        return eventLog != null ? eventLog.dWheel() : 0;
    }

    public int getEventX() {
        val eventLog = state.getEventLog();
        return eventLog != null ? eventLog.x() : state.x;
    }

    public int getEventY() {
        val eventLog = state.getEventLog();
        return eventLog != null ? eventLog.y() : state.y;
    }

    public boolean getEventButtonState() {
        val eventLog = state.getEventLog();
        return eventLog != null && eventLog.pressed();
    }

    @Override
    public void stopAll() {
        unpressButton(MouseButton.Type.values());
        state.getScheduledTasks().clear();
    }

    @Override
    public int getDWheel() {
        return state.dWheel;
    }

    @Override
    public void setDWheel(int dWheel) {
        state.dWheel = dWheel;
    }

    @Override
    public int getX() {
        return state.x;
    }

    @Override
    public int getY() {
        return state.y;
    }

    @Override
    public void setDX(int dx) {
        state.dX = dx;
    }

    @Override
    public void setDY(int dy) {
        state.dY = dy;
    }

    @Override
    public void changeYaw(float dYaw) {
        val defaultMouseSense = 0.5f;
        val sensitivity = defaultMouseSense * 0.6F + 0.2F;
        val deltaX = dYaw / (sensitivity * sensitivity * sensitivity * 8.0F);
        this.setDX((int) (deltaX / 0.15));
    }

    @Override
    public void changePitch(float dPitch) {
        val defaultMouseSense = 0.5f;
        val sensitivity = defaultMouseSense * 0.6F + 0.2F;
        // TODO: inverted
        // val inverted = this.mc.gameSettings.invertMouse ? -1 : 1;
        val deltaY = -dPitch / (sensitivity * sensitivity * sensitivity * 8.0F /* * inverted */
        );
        this.setDY((int) (deltaY / 0.15));
    }

    @Override
    public void setState(gg.mineral.bot.api.controls.MouseState state) {
        if (state instanceof MouseState newState)
            this.state = newState;
    }

}
