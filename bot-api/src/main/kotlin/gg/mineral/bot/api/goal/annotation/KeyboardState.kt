package gg.mineral.bot.api.goal.annotation

import gg.mineral.bot.api.controls.Key

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class KeyboardState(vararg val keys: Key.Type)

