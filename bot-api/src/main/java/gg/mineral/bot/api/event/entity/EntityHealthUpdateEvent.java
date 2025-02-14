package gg.mineral.bot.api.event.entity;

import gg.mineral.bot.api.event.Event;

public record EntityHealthUpdateEvent(float health) implements Event {
}
