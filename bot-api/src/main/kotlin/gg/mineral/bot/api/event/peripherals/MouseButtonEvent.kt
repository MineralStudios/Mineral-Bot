package gg.mineral.bot.api.event.peripherals

import gg.mineral.bot.api.controls.MouseButton
import gg.mineral.bot.api.event.Event

@JvmRecord
data class MouseButtonEvent(val type: MouseButton.Type, val pressed: Boolean) : Event 