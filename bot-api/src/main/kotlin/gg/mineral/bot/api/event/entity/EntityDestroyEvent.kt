package gg.mineral.bot.api.event.entity

import gg.mineral.bot.api.entity.ClientEntity
import gg.mineral.bot.api.event.Event

@JvmRecord
data class EntityDestroyEvent(val destroyedEntity: ClientEntity) : Event
