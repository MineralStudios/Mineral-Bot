package gg.mineral.bot.impl.controls

import gg.mineral.bot.api.controls.MouseButton

class MouseButton(override val type: MouseButton.Type, override var isPressed: Boolean = false) : MouseButton
