package gg.mineral.bot.api.concurrent

import java.lang.ref.WeakReference
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit

abstract class ListenableFuture<T>(instance: T) : Future<T> {
    private val instanceRef: WeakReference<T?> = WeakReference(instance)
    protected var done = false
    var cancelled = false

    override fun cancel(mayInterruptIfRunning: Boolean): Boolean {
        cancelled = true
        return true
    }

    override fun isCancelled() = cancelled

    override fun isDone() = done

    override fun get(): T {
        return instanceRef.get() ?: throw IllegalStateException("Instance is no longer available.")
    }

    override fun get(timeout: Long, unit: TimeUnit) = get()

    abstract fun onComplete(consumer: (T) -> Unit): ListenableFuture<T>

    abstract fun onError(consumer: (Throwable) -> Unit): ListenableFuture<T>
}