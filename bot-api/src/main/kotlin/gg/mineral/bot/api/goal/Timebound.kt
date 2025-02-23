package gg.mineral.bot.api.goal

interface Timebound {
    var startTime: Long
    val maxDuration: Long
}