package gg.mineral.bot.api.goal.annotation

import gg.mineral.bot.api.controls.MouseButton

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class MouseState(vararg val buttons: MouseButton.Type) {
}
