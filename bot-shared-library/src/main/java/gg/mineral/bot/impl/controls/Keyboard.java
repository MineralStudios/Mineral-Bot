package gg.mineral.bot.impl.controls;

import gg.mineral.bot.api.controls.Key.Type;
import gg.mineral.bot.api.event.EventHandler;
import gg.mineral.bot.api.event.peripherals.KeyboardKeyEvent;
import gg.mineral.bot.api.instance.ClientInstance;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import lombok.val;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Keyboard implements gg.mineral.bot.api.controls.Keyboard {
    private static final Logger logger = LogManager.getLogger(Keyboard.class);
    private final Key[] keys;
    private final Queue<Log> logs = new ConcurrentLinkedQueue<>();
    private Log eventLog = null, currentLog = null;
    private Iterator<Log> iterator = null;
    private final EventHandler eventHandler;

    private final Object2LongOpenHashMap<Runnable> scheduledTasks = new Object2LongOpenHashMap<>();

    public void onGameLoop(long time) {
        scheduledTasks.keySet().removeIf(runnable -> {
            if (time >= scheduledTasks.getLong(runnable)) {
                runnable.run();
                return true;
            }
            return false;
        });

        // --- Begin added logic to replicate LWJGL 2.9.4 mouse grab behavior ---
        // If the eventHandler is a ClientInstance, we can check the mouse.
        if (eventHandler instanceof ClientInstance clientInstance) {
            // When the mouse is ungrabbed, release all pressed keys.
            if (!clientInstance.getMouse().isGrabbed()) {
                for (val key : keys) {
                    if (key.isPressed()) {
                        // Using duration 0 so that no reschedule occurs.
                        unpressKey(0, key.getType());
                    }
                }
            }
        }
        // --- End added logic ---
    }

    public void schedule(Runnable runnable, long delay) {
        scheduledTasks.put(runnable, (System.nanoTime() / 1000000) + delay);
    }

    public Keyboard(EventHandler eventHandler) {
        this.eventHandler = eventHandler;
        keys = new Key[Key.Type.values().length];

        for (val type : Key.Type.values())
            keys[type.ordinal()] = new Key(type);
    }

    @Override
    public Key getKey(Key.Type type) {
        return keys[type.ordinal()];
    }

    @Override
    public void pressKey(int durationMillis, Key.Type... types) {
        for (val type : types) {
            val key = getKey(type);

            if (key == null || key.isPressed())
                continue;

            val event = new KeyboardKeyEvent(type, true);

            if (eventHandler.callEvent(event))
                continue;

            logger.debug("Pressing key: {} for {}ms", type, durationMillis);
            key.setPressed(true);
            if (currentLog != null)
                logs.add(currentLog);
            currentLog = new Log(type, true);
            if (durationMillis > 0 && durationMillis < Integer.MAX_VALUE)
                schedule(() -> unpressKey(type), durationMillis);
        }
    }

    @Override
    public void unpressKey(int durationMillis, Key.Type... types) {
        for (val type : types) {
            val key = getKey(type);

            if (key == null || !key.isPressed())
                continue;

            val event = new KeyboardKeyEvent(type, false);

            if (eventHandler.callEvent(event))
                continue;

            logger.debug("Unpressing key: {} for {}ms", type, durationMillis);
            key.setPressed(false);
            if (currentLog != null)
                logs.add(currentLog);
            currentLog = new Log(type, false);

            if (durationMillis > 0 && durationMillis < Integer.MAX_VALUE)
                schedule(() -> pressKey(type), durationMillis);
        }
    }

    @Override
    public boolean next() {
        if (currentLog != null) {
            this.logs.add(currentLog);
            currentLog = null;
        }

        if (iterator == null) {
            keysLoop:
            for (val key : keys) {
                if (key.isPressed()) {
                    for (val log : logs)
                        if (log.type == key.getType())
                            continue keysLoop;
                    logs.add(new Log(key.getType(), true));
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
    public int getEventKey() {
        return eventLog != null ? eventLog.type.getKeyCode() : -1;
    }

    @Override
    public Type getEventKeyType() {
        return eventLog != null ? eventLog.type : null;
    }

    @Override
    public boolean isKeyDown(Key.Type type) {
        Key key = getKey(type);
        return key != null && key.isPressed();
    }

    @Override
    public boolean getEventKeyState() {
        return eventLog != null && eventLog.pressed;
    }

    @Override
    public void stopAll() {
        unpressKey(Key.Type.values());
        scheduledTasks.clear();
    }

    public record Log(Key.Type type, boolean pressed) {
    }
}