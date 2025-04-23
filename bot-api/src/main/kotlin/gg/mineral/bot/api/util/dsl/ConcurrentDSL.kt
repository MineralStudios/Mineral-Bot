package gg.mineral.bot.api.util.dsl

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
inline fun <T> Deferred<T>.onComplete(crossinline block: (Result<T>) -> Unit) {
    invokeOnCompletion { throwable ->
        val result = runCatching {
            if (throwable != null) throw throwable
            getCompleted()
        }
        block(result)
    }
}
