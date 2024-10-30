package gg.mineral.bot.impl.controls;

import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.eclipse.jdt.annotation.Nullable;

import gg.mineral.bot.api.controls.Key.Type;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import lombok.Data;
import lombok.val;

@Data
public class KeyboardState implements gg.mineral.bot.api.controls.KeyboardState {
    private final Key[] keys;
    private final Queue<Log> logs = new ConcurrentLinkedQueue<>();
    @Nullable
    private Log eventLog = null, currentLog = null;
    @Nullable
    private Iterator<Log> iterator = null;

    private final Object2LongOpenHashMap<Runnable> scheduledTasks = new Object2LongOpenHashMap<>();

    public KeyboardState() {
        this.keys = new Key[Key.Type.values().length];

        for (val type : Key.Type.values())
            keys[type.ordinal()] = new Key(type);
    }

    public record Log(Key.Type type, boolean pressed) {
    }

    @Override
    public Key getKey(Type type) {
        return keys[type.ordinal()];
    }

    @Override
    public boolean next() {
        if (this.currentLog != null) {
            this.logs.add(this.currentLog);
            this.currentLog = null;
        }

        if (this.iterator == null) {
            buttonsLoop: for (val key : this.keys) {
                if (key.isPressed()) {
                    for (val log : this.logs)
                        if (log.type == key.getType())
                            continue buttonsLoop;

                    this.logs.add(
                            new Log(key.getType(), true));
                }
            }

            this.iterator = this.logs.iterator();
        }

        val iter = this.iterator;

        if (iter != null && iter.hasNext()) {
            this.eventLog = iter.next();
            iter.remove();
            return true;
        }

        this.iterator = null;

        return false;
    }

    @Override
    public Type getEventKeyType() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getEventKeyType'");
    }

    @Override
    public int getEventKey() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getEventKey'");
    }

    @Override
    public boolean isKeyDown(Type type) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'isKeyDown'");
    }

    @Override
    public boolean getEventKeyState() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getEventKeyState'");
    }
}
