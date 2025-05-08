package gg.mineral.bot.api.concurrent

import java.lang.ref.WeakReference
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit
import kotlin.concurrent.atomics.AtomicBoolean
import kotlin.concurrent.atomics.AtomicInt
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.concurrent.atomics.decrementAndFetch

@OptIn(ExperimentalAtomicApi::class)
fun <T> awaitAll(futures: List<ListenableFuture<T>>, onSuccess: () -> Unit, onError: (Throwable) -> Unit) {
    val remaining = AtomicInt(futures.size)
    val failed = AtomicBoolean(false)

    for (future in futures) {
        future.onComplete {
            if (remaining.decrementAndFetch() == 0 && !failed.load()) onSuccess()
        }
        future.onError {
            if (failed.compareAndSet(expectedValue = false, newValue = true)) onError(it)
        }
    }
}

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