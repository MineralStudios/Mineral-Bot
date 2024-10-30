package gg.mineral.bot.impl.controls;

import gg.mineral.bot.api.controls.MouseButton.Type;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import lombok.Data;
import lombok.val;

import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.eclipse.jdt.annotation.Nullable;

@Data
public class MouseState implements gg.mineral.bot.api.controls.MouseState {
    private final MouseButton[] mouseButtons;
    int dWheel;
    int x, y, dX, dY;
    private final Queue<Log> logs = new ConcurrentLinkedQueue<>();
    @Nullable
    private Log eventLog = null, currentLog = null;
    @Nullable
    private Iterator<Log> iterator = null;
    private final Object2LongOpenHashMap<Runnable> scheduledTasks = new Object2LongOpenHashMap<>();

    public MouseState() {
        this.mouseButtons = new MouseButton[MouseButton.Type.values().length];

        for (val type : MouseButton.Type.values())
            mouseButtons[type.ordinal()] = new MouseButton(type);
    }

    @Override
    public MouseButton getButton(Type type) {
        return mouseButtons[type.ordinal()];
    }

    @Override
    public boolean next() {
        if (this.currentLog != null) {
            this.logs.add(this.currentLog);
            this.currentLog = null;
        }

        if (this.iterator == null) {
            buttonsLoop: for (val button : this.mouseButtons) {
                if (button.isPressed()) {
                    for (val log : this.logs)
                        if (log.type == button.getType())
                            continue buttonsLoop;

                    this.logs.add(
                            new Log(button.getType(), true, this.x, this.y, this.dX, this.dY, this.dWheel));
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
    public Type getEventButtonType() {
        return this.eventLog != null ? this.eventLog.type : null;
    }

    @Override
    public int getEventButton() {
        return this.eventLog != null ? this.eventLog.type.getKeyCode() : -1;
    }

    public static record Log(MouseButton.Type type, boolean pressed, int x, int y, int dX, int dY,
            int dWheel) {
    }
}
