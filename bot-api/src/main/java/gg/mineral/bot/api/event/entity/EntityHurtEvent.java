package gg.mineral.bot.api.event.entity;

import gg.mineral.bot.api.entity.ClientEntity;
import gg.mineral.bot.api.event.Event;

import lombok.Value;

@Value
public class EntityHurtEvent implements Event {
    ClientEntity attackedEntity;
}
