package gg.mineral.bot.api.event.entity

import gg.mineral.bot.api.event.Event

@JvmRecord
data class EntityHealthUpdateEvent(val health: Float) : Event
