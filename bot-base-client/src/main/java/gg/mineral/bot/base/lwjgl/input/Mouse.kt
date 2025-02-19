package gg.mineral.bot.base.lwjgl.input

import gg.mineral.bot.api.controls.MouseButton
import gg.mineral.bot.api.controls.MouseButton.Type.Companion.fromKeyCode
import gg.mineral.bot.api.event.EventHandler
import gg.mineral.bot.impl.config.BotGlobalConfig
import gg.mineral.bot.impl.controls.Mouse

class Mouse(eventHandler: EventHandler) : Mouse(eventHandler) {
    override var isGrabbed: Boolean = false
        get() = super.isGrabbed
        set(value) {
            if (BotGlobalConfig.headless || BotGlobalConfig.control) {
                field = value
                return
            }

            org.lwjgl.input.Mouse.setGrabbed(value)
        }

    override fun next(): Boolean {
        if (BotGlobalConfig.headless || BotGlobalConfig.control) return super.next()
        return org.lwjgl.input.Mouse.next()
    }

    override fun isButtonDown(i: Int): Boolean {
        if (BotGlobalConfig.headless || BotGlobalConfig.control) return super.next()

        return org.lwjgl.input.Mouse.isButtonDown(i)
    }

    override fun isButtonDown(type: MouseButton.Type): Boolean {
        if (BotGlobalConfig.headless || BotGlobalConfig.control) return super.isButtonDown(type)

        return org.lwjgl.input.Mouse.isButtonDown(type.keyCode)
    }

    override val eventButtonType: MouseButton.Type?
        get() {
            if (BotGlobalConfig.headless || BotGlobalConfig.control) return super.eventButtonType

            return fromKeyCode(org.lwjgl.input.Mouse.getEventButton())
        }

    override val eventButton: Int
        get() {
            if (BotGlobalConfig.headless || BotGlobalConfig.control) return super.eventButton

            return org.lwjgl.input.Mouse.getEventButton()
        }

    override val eventDWheel: Int
        get() {
            if (BotGlobalConfig.headless || BotGlobalConfig.control) return super.eventDWheel

            return org.lwjgl.input.Mouse.getEventDWheel()
        }

    override var x: Int
        get() = super.x
        set(x) {
            if (BotGlobalConfig.headless || BotGlobalConfig.control) super.x =
                x
            else org.lwjgl.input.Mouse.setCursorPosition(x, y)
        }

    override var y: Int
        get() = super.y
        set(y) {
            if (BotGlobalConfig.headless || BotGlobalConfig.control) super.y =
                y
            else org.lwjgl.input.Mouse.setCursorPosition(x, y)
        }

    override fun setCursorPosition(x: Int, y: Int) {
        if (BotGlobalConfig.headless || BotGlobalConfig.control) {
            super.setCursorPosition(x, y)
        } else org.lwjgl.input.Mouse.setCursorPosition(x, y)
    }

    override var dX: Int
        get() {
            if (BotGlobalConfig.headless || BotGlobalConfig.control) return super.dX

            return org.lwjgl.input.Mouse.getDX()
        }
        set(dX) {
            super.dX = dX
        }

    override var dY: Int
        get() {
            if (BotGlobalConfig.headless || BotGlobalConfig.control) return super.dY

            return org.lwjgl.input.Mouse.getDY()
        }
        set(dY) {
            super.dY = dY
        }

    val isCreated: Boolean
        get() {
            if (BotGlobalConfig.headless || BotGlobalConfig.control) return true

            return org.lwjgl.input.Mouse.isCreated()
        }

    override val eventButtonState: Boolean
        get() {
            if (BotGlobalConfig.headless || BotGlobalConfig.control) return super.eventButtonState

            return org.lwjgl.input.Mouse.getEventButtonState()
        }
}
