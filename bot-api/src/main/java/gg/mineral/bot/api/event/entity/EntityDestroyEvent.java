package gg.mineral.bot.api.event.entity;

import gg.mineral.bot.api.entity.ClientEntity;
import gg.mineral.bot.api.event.Event;

public record EntityDestroyEvent(ClientEntity destroyedEntity) implements Event {
}
