package io.github.wulkanowy.data

import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import timber.log.Timber

sealed class Resource<T> {

    open class Loading<T> : Resource<T>()

    data class Intermediate<T>(val data: T) : Loading<T>()

    data class Success<T>(val data: T) : Resource<T>()

    data class Error<T>(val error: Throwable) : Resource<T>()
}

val <T> Resource<T>.dataOrNull: T?
    get() = when (this) {
        is Resource.Success -> this.data
        is Resource.Intermediate -> this.data
        is Resource.Loading -> null
        is Resource.Error -> null
    }

val <T> Resource<T>.errorOrNull: Throwable?
    get() = when (this) {
        is Resource.Error -> this.error
        else -> null
    }

fun <T> resourceFlow(block: suspend () -> T) = flow {
    emit(Resource.Loading())
    emit(Resource.Success(block()))
}.catch { emit(Resource.Error(it)) }

fun <T> flatResourceFlow(block: suspend () -> Flow<Resource<T>>) = flow {
    emit(Resource.Loading())
    emitAll(block().filter { it is Resource.Intermediate || it !is Resource.Loading })
}.catch { emit(Resource.Error(it)) }

fun <T, U> Resource<T>.mapData(block: (T) -> U) = when (this) {
    is Resource.Success -> Resource.Success(block(this.data))
    is Resource.Intermediate -> Resource.Intermediate(block(this.data))
    is Resource.Loading -> Resource.Loading()
    is Resource.Error -> Resource.Error(this.error)
}

fun <T> Flow<Resource<T>>.logResourceStatus(name: String, showData: Boolean = false) = onEach {
    val description = when (it) {
        is Resource.Intermediate -> "intermediate data received" + if (showData) " (data: `${it.data}`)" else ""
        is Resource.Loading -> "started"
        is Resource.Success -> "success" + if (showData) " (data: `${it.data}`)" else ""
        is Resource.Error -> "exception occurred: ${it.error}"
    }
    Timber.i("$name: $description")
}

fun <T, U> Flow<Resource<T>>.mapResourceData(block: (T) -> U) = map {
    it.mapData(block)
}

fun <T> Flow<Resource<T>>.onResourceData(block: suspend (T) -> Unit) = onEach {
    when (it) {
        is Resource.Success -> block(it.data)
        is Resource.Intermediate -> block(it.data)
        is Resource.Error,
        is Resource.Loading -> Unit
    }
}

fun <T> Flow<Resource<T>>.onResourceLoading(block: suspend () -> Unit) = onEach {
    if (it is Resource.Loading) {
        block()
    }
}

fun <T> Flow<Resource<T>>.onResourceIntermediate(block: suspend (T) -> Unit) = onEach {
    if (it is Resource.Intermediate) {
        block(it.data)
    }
}

fun <T> Flow<Resource<T>>.onResourceSuccess(block: suspend (T) -> Unit) = onEach {
    if (it is Resource.Success) {
        block(it.data)
    }
}

fun <T> Flow<Resource<T>>.onResourceError(block: (Throwable) -> Unit) = onEach {
    if (it is Resource.Error) {
        block(it.error)
    }
}

fun <T> Flow<Resource<T>>.onResourceNotLoading(block: () -> Unit) = onEach {
    if (it !is Resource.Loading) {
        block()
    }
}

suspend fun <T> Flow<Resource<T>>.toFirstResult() = filter { it !is Resource.Loading }.first()

suspend fun <T> Flow<Resource<T>>.waitForResult() = takeWhile { it is Resource.Loading }.collect()

inline fun <ResultType, RequestType> networkBoundResource(
    mutex: Mutex = Mutex(),
    showSavedOnLoading: Boolean = true,
    crossinline isResultEmpty: (ResultType) -> Boolean,
    crossinline query: () -> Flow<ResultType>,
    crossinline fetch: suspend (ResultType) -> RequestType,
    crossinline saveFetchResult: suspend (old: ResultType, new: RequestType) -> Unit,
    crossinline onFetchFailed: (Throwable) -> Unit = { },
    crossinline shouldFetch: (ResultType) -> Boolean = { true },
    crossinline filterResult: (ResultType) -> ResultType = { it }
) = flow {
    emit(Resource.Loading())

    val data = query().first()
    emitAll(if (shouldFetch(data)) {
        val filteredResult = filterResult(data)

        if (showSavedOnLoading && !isResultEmpty(filteredResult)) {
            emit(Resource.Intermediate(filteredResult))
        }

        try {
            val newData = fetch(data)
            mutex.withLock { saveFetchResult(query().first(), newData) }
            query().map { Resource.Success(filterResult(it)) }
        } catch (throwable: Throwable) {
            onFetchFailed(throwable)
            query().map { Resource.Error(throwable) }
        }
    } else {
        query().map { Resource.Success(filterResult(it)) }
    })
}

@JvmName("networkBoundResourceWithMap")
inline fun <ResultType, RequestType, T> networkBoundResource(
    mutex: Mutex = Mutex(),
    showSavedOnLoading: Boolean = true,
    crossinline isResultEmpty: (T) -> Boolean,
    crossinline query: () -> Flow<ResultType>,
    crossinline fetch: suspend (ResultType) -> RequestType,
    crossinline saveFetchResult: suspend (old: ResultType, new: RequestType) -> Unit,
    crossinline onFetchFailed: (Throwable) -> Unit = { },
    crossinline shouldFetch: (ResultType) -> Boolean = { true },
    crossinline mapResult: (ResultType) -> T
) = flow {
    emit(Resource.Loading())

    val data = query().first()
    emitAll(if (shouldFetch(data)) {
        val mappedResult = mapResult(data)

        if (showSavedOnLoading && !isResultEmpty(mappedResult)) {
            emit(Resource.Intermediate(mappedResult))
        }
        try {
            val newData = fetch(data)
            mutex.withLock { saveFetchResult(query().first(), newData) }
            query().map { Resource.Success(mapResult(it)) }
        } catch (throwable: Throwable) {
            onFetchFailed(throwable)
            query().map { Resource.Error(throwable) }
        }
    } else {
        query().map { Resource.Success(mapResult(it)) }
    })
}
