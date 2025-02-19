package gg.mineral.bot.impl.controls

import gg.mineral.bot.api.controls.Key

/**
 * Represents a key with a type and pressed state.
 */
class Key(override val type: Key.Type, override var isPressed: Boolean = false) : Key
