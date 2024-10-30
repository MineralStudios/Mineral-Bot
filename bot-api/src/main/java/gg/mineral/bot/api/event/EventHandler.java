package gg.mineral.bot.api.event;

import org.eclipse.jdt.annotation.NonNull;

public interface EventHandler {
    /**
     * Schedules a task to run when an event occurs.
     * 
     * @param event
     *              the event
     * 
     * @return true if the event was cancelled
     * 
     */
    <T extends Event> boolean callEvent(@NonNull T event);
}
