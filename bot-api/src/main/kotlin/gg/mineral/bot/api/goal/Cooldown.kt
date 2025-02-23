package gg.mineral.bot.api.goal

interface Cooldown {
    val lastExecuted: Long
    val cooldown: Long
}