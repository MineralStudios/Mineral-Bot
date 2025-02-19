package gg.mineral.bot.base.lwjgl.input

import gg.mineral.bot.api.controls.Key
import gg.mineral.bot.api.controls.Key.Type.Companion.fromKeyCode
import gg.mineral.bot.api.event.EventHandler
import gg.mineral.bot.impl.config.BotGlobalConfig
import gg.mineral.bot.impl.controls.Keyboard

class Keyboard(eventHandler: EventHandler) : Keyboard(eventHandler) {
    private var repeatEvents = false

    override fun next(): Boolean {
        if (BotGlobalConfig.headless || BotGlobalConfig.control) return super.next()
        return org.lwjgl.input.Keyboard.next()
    }

    override fun isKeyDown(type: Key.Type): Boolean {
        if (BotGlobalConfig.headless || BotGlobalConfig.control) return super.isKeyDown(type)
        return org.lwjgl.input.Keyboard.isKeyDown(type.keyCode)
    }

    fun isKeyDown(keyCode: Int): Boolean {
        val type = fromKeyCode(keyCode)
        return isKeyDown(type)
    }

    override val eventKeyType: Key.Type?
        get() {
            if (BotGlobalConfig.headless || BotGlobalConfig.control) return super.eventKeyType
            return fromKeyCode(org.lwjgl.input.Keyboard.getEventKey())
        }

    override val eventKey: Int
        get() {
            if (BotGlobalConfig.headless || BotGlobalConfig.control) return super.eventKey
            return org.lwjgl.input.Keyboard.getEventKey()
        }

    override val eventKeyState: Boolean
        get() {
            if (BotGlobalConfig.headless || BotGlobalConfig.control) return super.eventKeyState
            return org.lwjgl.input.Keyboard.getEventKeyState()
        }

    val isRepeatEvent: Boolean
        get() {
            if (BotGlobalConfig.headless || BotGlobalConfig.control) return repeatEvents
            return org.lwjgl.input.Keyboard.isRepeatEvent()
        }

    fun enableRepeatEvents(enable: Boolean) {
        if (BotGlobalConfig.headless || BotGlobalConfig.control) repeatEvents = enable
        else org.lwjgl.input.Keyboard.enableRepeatEvents(enable)
    }

    val isCreated: Boolean
        get() {
            if (BotGlobalConfig.headless || BotGlobalConfig.control) return true

            return org.lwjgl.input.Keyboard.isCreated()
        }

    val eventCharacter: Char?
        get() {
            if (BotGlobalConfig.headless || BotGlobalConfig.control) {
                val type = eventKeyType

                return type?.character
            }

            return org.lwjgl.input.Keyboard.getEventCharacter()
        }
}
