package gg.mineral.bot.api.event.entity;

import gg.mineral.bot.api.entity.ClientEntity;
import gg.mineral.bot.api.event.Event;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class EntityHurtEvent implements Event {
    private final ClientEntity attackedEntity;
}
