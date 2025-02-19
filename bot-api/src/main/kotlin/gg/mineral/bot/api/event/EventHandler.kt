package gg.mineral.bot.api.event

interface EventHandler {
    /**
     * Schedules a task to run when an event occurs.
     *
     * @param event
     * the event
     *
     * @return true if the event was cancelled
     */
    fun <T : Event> callEvent(event: T): Boolean
}
