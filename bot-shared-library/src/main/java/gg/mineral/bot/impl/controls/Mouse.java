package gg.mineral.bot.impl.controls;

import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import gg.mineral.bot.api.controls.MouseButton.Type;
import gg.mineral.bot.api.entity.living.player.FakePlayer;
import gg.mineral.bot.api.event.mouse.MouseButtonEvent;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import lombok.Getter;
import lombok.Setter;

public abstract class Mouse implements gg.mineral.bot.api.controls.Mouse {

    private final FakePlayer fakePlayer;
    private final MouseButton[] mouseButtons;
    @Getter
    @Setter
    private int dWheel;
    @Getter
    private int x, y;
    @Setter
    private int dX, dY;
    private final Queue<Log> logs = new ConcurrentLinkedQueue<>();
    private Log eventLog = null, currentLog = null;
    private Iterator<Log> iterator = null;

    private final Object2LongOpenHashMap<Runnable> scheduledTasks = new Object2LongOpenHashMap<>();

    public Mouse(FakePlayer fakePlayer) {
        this.fakePlayer = fakePlayer;
        mouseButtons = new MouseButton[MouseButton.Type.values().length];

        for (MouseButton.Type type : MouseButton.Type.values())
            mouseButtons[type.ordinal()] = new MouseButton(type);
    }

    public void onGameLoop(long time) {
        scheduledTasks.keySet().removeIf(runnable -> {
            if (time >= scheduledTasks.getLong(runnable)) {
                runnable.run();
                return true;
            }
            return false;
        });
    }

    public void schedule(Runnable runnable, long delay) {
        scheduledTasks.put(runnable, (System.nanoTime() / 1000000) + delay);
    }

    @Override
    public MouseButton getButton(MouseButton.Type type) {
        return mouseButtons[type.ordinal()];
    }

    @Override
    public void pressButton(int durationMillis, MouseButton.Type... types) {
        for (MouseButton.Type type : types) {
            MouseButton button = getButton(type);

            if (button == null || button.isPressed())
                return;

            MouseButtonEvent event = new MouseButtonEvent(type, true, durationMillis);

            if (fakePlayer != null && fakePlayer.callEvent(event))
                return;

            button.setPressed(true);
            if (currentLog != null)
                logs.add(currentLog);
            currentLog = new Log(type, true, x, y, dX, dY,
                    dWheel);
            if (durationMillis > 0 && durationMillis < Integer.MAX_VALUE)
                schedule(() -> unpressButton(type), durationMillis);
        }
    }

    @Override
    public void unpressButton(int durationMillis, MouseButton.Type... types) {
        for (MouseButton.Type type : types) {
            MouseButton button = getButton(type);

            if (button == null || !button.isPressed())
                return;

            MouseButtonEvent event = new MouseButtonEvent(type, true, durationMillis);

            if (fakePlayer != null && fakePlayer.callEvent(event))
                return;

            button.setPressed(false);
            if (currentLog != null)
                logs.add(currentLog);
            currentLog = new Log(type, false, x, y, dX, dY, dWheel);
        }
    }

    @Override
    public boolean next() {
        if (currentLog != null) {
            this.logs.add(currentLog);
            currentLog = null;
        }

        if (iterator == null) {
            buttonsLoop: for (MouseButton button : mouseButtons) {
                if (button.isPressed()) {
                    for (Log log : logs)
                        if (log.type == button.getType())
                            continue buttonsLoop;

                    logs.add(new Log(button.getType(), true, x, y, dX, dY, dWheel));
                }
            }

            iterator = logs.iterator();
        }

        if (iterator.hasNext()) {
            eventLog = iterator.next();
            iterator.remove();
            return true;
        }

        iterator = null;

        return false;
    }

    @Override
    public MouseButton.Type getEventButtonType() {
        return eventLog != null ? eventLog.type : null;
    }

    public boolean isButtonDown(int i) {
        MouseButton.Type type = MouseButton.Type.fromKeyCode(i);
        if (type == null)
            return false;
        return isButtonDown(type);
    }

    public boolean isButtonDown(Type type) {
        return getButton(type).isPressed();
    }

    @Override
    public int getEventButton() {
        return eventLog != null ? eventLog.type.getKeyCode() : -1;
    }

    public record Log(MouseButton.Type type, boolean pressed, int x, int y, int dX, int dY,
            int dWheel) {
    }

    @Override
    public void setX(int x) {
        this.dX = x - this.x;
        this.x = x;
        if (currentLog != null)
            currentLog = new Log(currentLog.type, currentLog.pressed, x, y, dX, dY, dWheel);
        else
            currentLog = new Log(Type.UNKNOWN, false, x, y, dX, dY, dWheel);
    }

    @Override
    public void setY(int y) {
        this.dY = y - this.y;
        this.y = y;
        if (currentLog != null)
            currentLog = new Log(currentLog.type, currentLog.pressed, x, y, dX, dY, dWheel);
        else
            currentLog = new Log(Type.UNKNOWN, false, x, y, dX, dY, dWheel);
    }

    @Override
    public int getDX() {
        try {
            return dX;
        } finally {
            dX = 0;
        }
    }

    @Override
    public int getDY() {
        try {
            return dY;
        } finally {
            dY = 0;
        }
    }

    public int getEventDWheel() {
        return eventLog != null ? eventLog.dWheel : 0;
    }

    public int getEventX() {
        return eventLog != null ? eventLog.x : x;
    }

    public int getEventY() {
        return eventLog != null ? eventLog.y : y;
    }

    public boolean getEventButtonState() {
        return eventLog != null && eventLog.pressed;
    }

    @Override
    public void stopAll() {
        unpressButton(MouseButton.Type.values());
        scheduledTasks.clear();
    }
}
