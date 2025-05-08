package gg.mineral.bot.api.behaviour.node

import gg.mineral.bot.api.behaviour.BehaviourTree
import gg.mineral.bot.api.controls.Key
import gg.mineral.bot.api.controls.MouseButton
import gg.mineral.bot.api.instance.ClientInstance
import gg.mineral.bot.api.util.dsl.angleDifference

abstract class ChildNode(open val tree: BehaviourTree) : BTNode() {
    fun callTick() = callTick(tree.treeStack)

    val clientInstance: ClientInstance
        get() = tree.clientInstance
    private val mouse: gg.mineral.bot.api.controls.Mouse
        get() = clientInstance.mouse
    private val keyboard: gg.mineral.bot.api.controls.Keyboard
        get() = clientInstance.keyboard

    fun setMouseYaw(yaw: Float) {
        val fakePlayer = clientInstance.fakePlayer
        val rotYaw = fakePlayer.yaw
        mouse.changeYaw(angleDifference(rotYaw, yaw))
    }

    fun setMousePitch(pitch: Float) {
        val fakePlayer = clientInstance.fakePlayer
        val rotPitch = fakePlayer.pitch
        mouse.changePitch(angleDifference(rotPitch, pitch))
    }

    fun getButton(type: MouseButton.Type): MouseButton {
        return mouse.getButton(type)!!
    }

    fun getKey(type: Key.Type): Key {
        return keyboard.getKey(type)!!
    }

    fun pressKey(durationMillis: Int, vararg types: Key.Type) {
        keyboard.pressKey(durationMillis, *types)
    }

    fun pressKey(vararg types: Key.Type) {
        keyboard.pressKey(Int.MAX_VALUE, *types)
    }

    fun unpressKey(durationMillis: Int, vararg types: Key.Type) {
        keyboard.unpressKey(durationMillis, *types)
    }

    fun unpressKey(vararg types: Key.Type) {
        keyboard.unpressKey(Int.MAX_VALUE, *types)
    }

    fun pressButton(durationMillis: Int, vararg types: MouseButton.Type) {
        mouse.pressButton(durationMillis, *types)
    }

    fun pressButton(vararg types: MouseButton.Type) {
        mouse.pressButton(*types)
    }

    fun unpressButton(durationMillis: Int, vararg types: MouseButton.Type) {
        mouse.unpressButton(durationMillis, *types)
    }

    fun unpressButton(vararg types: MouseButton.Type) {
        mouse.unpressButton(*types)
    }

    fun stopAll() {
        mouse.stopAll()
        keyboard.stopAll()
    }

    fun mouseStopAll() {
        mouse.stopAll()
    }

    fun keyboardStopAll() {
        keyboard.stopAll()
    }
}