@file:Suppress("unused")

package sg.searchhouse.agentconnect.dsl

import androidx.annotation.WorkerThread
import androidx.room.EmptyResultSetException
import io.reactivex.Single
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

@Throws(RuntimeException::class)
@WorkerThread
fun <T> Single<T>.performBlockQueryStrict(): T? = try {
    blockingGet()
} catch (e: EmptyResultSetException) {
    null
}

@WorkerThread
fun <T> Single<T>.performBlockQueryLenient(): T? = try {
    performBlockQueryStrict()
} catch (e: RuntimeException) {
    Timber.tag("RoomDSL").e("performBlockQueryLenient")
    e.printStackTrace()
    null
}

@Throws(RuntimeException::class)
@WorkerThread
fun <T> List<Single<T>>.performBlockQueriesStrict(): List<T?> = run {
    map { it.performBlockQueryStrict() }
}

@WorkerThread
fun <T> List<Single<T>>.performBlockQueriesLenient(): List<T?> = run {
    map { it.performBlockQueryLenient() }
}

fun <T> Single<T>.performAsyncQueryStrict(
    onSuccess: (T) -> Unit,
    onEmpty: () -> Unit,
    onError: () -> Unit
) =
    CoroutineScope(Dispatchers.IO).launch {
        try {
            performBlockQueryStrict()?.run { onSuccess.invoke(this) } ?: onEmpty.invoke()
        } catch (e: RuntimeException) {
            Timber.tag("RoomDSL").e("performAsyncQueryStrict")
            e.printStackTrace()
            onError.invoke()
        }
    }

fun <T> Single<T>.performAsyncQueryLenient(onSuccess: (T) -> Unit, onFail: () -> Unit) =
    CoroutineScope(Dispatchers.IO).launch {
        performBlockQueryLenient()?.run { onSuccess.invoke(this) } ?: onFail.invoke()
    }

fun <T> List<Single<T>>.performAsyncQueriesStrict(
    onComplete: (List<T?>) -> Unit,
    onError: () -> Unit
) =
    CoroutineScope(Dispatchers.IO).launch {
        val result = try {
            performBlockQueriesStrict()
        } catch (e: RuntimeException) {
            Timber.tag("RoomDSL").e("performAsyncQueriesStrict")
            e.printStackTrace()
            onError.invoke()
            return@launch
        }
        onComplete.invoke(result)
    }

fun <T> List<Single<T>>.performAsyncQueriesLenient(onComplete: (List<T?>) -> Unit) =
    CoroutineScope(Dispatchers.IO).launch {
        val result = performBlockQueriesLenient()
        onComplete.invoke(result)
    }