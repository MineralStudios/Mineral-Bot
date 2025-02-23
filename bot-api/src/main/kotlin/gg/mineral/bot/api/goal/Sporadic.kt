package gg.mineral.bot.api.goal

interface Sporadic {
    var executing: Boolean

    fun callStart() {
        if (executing) return
        executing = true
        if (this is Goal) tickCount = 0
        if (this is Timebound) startTime = Goal.timeMillis()
        onStart()
    }

    fun callEnd() {
        if (!executing) return
        onEnd()
        executing = false
    }

    fun onStart()

    fun onEnd()
}