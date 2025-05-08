package gg.mineral.bot.base.client.concurrent

import gg.mineral.bot.api.concurrent.ListenableFuture
import gg.mineral.bot.api.instance.ClientInstance
import java.util.concurrent.ConcurrentLinkedQueue

class ListenableFutureImpl<T : ClientInstance>(instance: T) : ListenableFuture<T>(instance) {
    private val onCompleteListeners = ConcurrentLinkedQueue<(T) -> Unit>()
    private val onErrorListeners = ConcurrentLinkedQueue<(Throwable) -> Unit>()

    override fun onComplete(consumer: (T) -> Unit): ListenableFuture<T> {
        if (cancelled) return this
        if (done) {
            consumer(get())
            return this
        }
        onCompleteListeners.add(consumer)
        return this
    }

    override fun onError(consumer: (Throwable) -> Unit): ListenableFuture<T> {
        if (cancelled) return this
        if (done) {
            consumer(IllegalStateException("Future is already completed"))
            return this
        }
        onErrorListeners.add(consumer)
        return this
    }

    fun complete() {
        if (done || cancelled) return
        done = true
        onCompleteListeners.removeAll {
            it(get())
            true
        }
    }

    fun fail(throwable: Throwable) {
        if (done || cancelled) return
        onErrorListeners.removeAll {
            it(throwable)
            true
        }
    }
}