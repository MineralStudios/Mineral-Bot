package gg.mineral.bot.api.event.peripherals

import gg.mineral.bot.api.controls.Key
import gg.mineral.bot.api.event.Event

@JvmRecord
data class KeyboardKeyEvent(val type: Key.Type, val pressed: Boolean) : Event 