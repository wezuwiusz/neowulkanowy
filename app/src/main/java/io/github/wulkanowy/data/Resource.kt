package io.github.wulkanowy.data

import io.github.wulkanowy.data.repositories.isEndDateReached
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import timber.log.Timber
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

sealed interface Resource<out T> {
    /**
     * The initial value of a resource flow. Indicates no data that is currently available to be shown,
     * however with the expectation that the state will transition to another one soon.
     */
    open class Loading<T> : Resource<T>

    /**
     * A semi-loading state with some data available to be displayed (usually cached data loaded from
     * the database). Still not the target state and it's expected to transition into another one soon.
     */
    data class Intermediate<T>(val data: T) : Loading<T>()

    /**
     * The happy-path target state. Data can either be:
     * - loaded from the database - while it may seem like this case is already handled by the
     *   Intermediate state, the difference here is semantic. Cached data is returned as Intermediate
     *   when there's a API request in progress (or soon expected to be), however when there is no
     *   intention of immediately querying the API, the cached data is returned as a Success.
     * - fetched from the API.
     */
    data class Success<T>(val data: T) : Resource<T>

    /**
     * Something bad happened and we were unable to get the requested data. This can be caused by
     * a database error, a network error, or really just any other error. Upon receiving this state
     * the UI can either: display a full screen error, or, when it has received any data previously,
     * display a snack bar informing of the problem.
     */
    data class Error<T>(val error: Throwable) : Resource<T>
}

val <T> Resource<T>.dataOrNull: T?
    get() = when (this) {
        is Resource.Success -> this.data
        is Resource.Intermediate -> this.data
        else -> null
    }

val <T> Resource<T>.dataOrThrow: T
    get() = when (this) {
        is Resource.Success -> this.data
        is Resource.Intermediate -> this.data
        is Resource.Loading -> throw IllegalStateException("Resource is in loading state")
        is Resource.Error -> throw this.error
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

/**
 * Injects another flow into this flow's resource data.
 */
inline fun <T1, T2, R> Flow<Resource<T1>>.combineWithResourceData(
    flow: Flow<T2>,
    crossinline block: suspend (T1, T2) -> R
): Flow<Resource<R>> =
    combine(flow) { resource, inject ->
        when (resource) {
            is Resource.Success -> Resource.Success(block(resource.data, inject))
            is Resource.Intermediate -> Resource.Intermediate(block(resource.data, inject))
            is Resource.Loading -> Resource.Loading()
            is Resource.Error -> Resource.Error(resource.error)
        }
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

inline fun <T, U> Flow<Resource<T>>.mapResourceData(crossinline block: suspend (T) -> U) = map {
    when (it) {
        is Resource.Success -> Resource.Success(block(it.data))
        is Resource.Intermediate -> Resource.Intermediate(block(it.data))
        is Resource.Loading -> Resource.Loading()
        is Resource.Error -> Resource.Error(it.error)
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
fun <T, U> Flow<Resource<T>>.flatMapResourceData(
    inheritIntermediate: Boolean = true, block: suspend (T) -> Flow<Resource<U>>
) = flatMapLatest {
    when (it) {
        is Resource.Success -> block(it.data)
        is Resource.Intermediate -> block(it.data).map { newRes ->
            if (inheritIntermediate && newRes is Resource.Success) Resource.Intermediate(newRes.data)
            else newRes
        }

        is Resource.Loading -> flowOf(Resource.Loading())
        is Resource.Error -> flowOf(Resource.Error(it.error))
    }
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

fun <T> Flow<Resource<T>>.onResourceError(block: suspend (Throwable) -> Unit) = onEach {
    if (it is Resource.Error) {
        block(it.error)
    }
}

fun <T> Flow<Resource<T>>.onResourceNotLoading(block: suspend () -> Unit) = onEach {
    if (it !is Resource.Loading) {
        block()
    }
}

suspend fun <T> Flow<Resource<T>>.toFirstResult() = filter { it !is Resource.Loading }.first()

suspend fun <T> Flow<Resource<T>>.waitForResult() = takeWhile { it is Resource.Loading }.collect()

// Can cause excessive amounts of `Resource.Intermediate` to be emitted. Unless that is desired,
// use `debounceIntermediates` to alleviate this behavior.
inline fun <reified T> combineResourceFlows(flows: Iterable<Flow<Resource<T>>>): Flow<Resource<List<T>>> =
    combine(flows) { items ->
        var isIntermediate = false
        val data = mutableListOf<T>()
        for (item in items) {
            when (item) {
                is Resource.Success -> data.add(item.data)
                is Resource.Intermediate -> {
                    isIntermediate = true
                    data.add(item.data)
                }

                is Resource.Loading -> return@combine Resource.Loading()
                is Resource.Error -> continue
            }
        }
        if (data.isEmpty()) {
            // All items have to be errors for this to happen, so just return the first one.
            // mapData is functionally useless and exists only to satisfy the type checker
            items.first().mapData { listOf(it) }
        } else if (isIntermediate) {
            Resource.Intermediate(data)
        } else {
            Resource.Success(data)
        }
    }

@OptIn(FlowPreview::class)
fun <T> Flow<Resource<T>>.debounceIntermediates(timeout: Duration = 5.seconds) = flow {
    var wasIntermediate = false

    emitAll(this@debounceIntermediates.debounce {
        if (it is Resource.Intermediate) {
            if (!wasIntermediate) {
                wasIntermediate = true
                Duration.ZERO
            } else {
                timeout
            }
        } else {
            wasIntermediate = false
            Duration.ZERO
        }
    })
}


inline fun <OutputType, ApiType> networkBoundResource(
    mutex: Mutex = Mutex(),
    crossinline isResultEmpty: (OutputType) -> Boolean,
    crossinline query: () -> Flow<OutputType>,
    crossinline fetch: suspend () -> ApiType,
    crossinline saveFetchResult: suspend (old: OutputType, new: ApiType) -> Unit,
    crossinline shouldFetch: (OutputType) -> Boolean = { true },
    crossinline filterResult: (OutputType) -> OutputType = { it }
) = networkBoundResource(
    mutex = mutex,
    isResultEmpty = isResultEmpty,
    query = query,
    fetch = fetch,
    saveFetchResult = saveFetchResult,
    shouldFetch = shouldFetch,
    mapResult = filterResult
)

@JvmName("networkBoundResourceWithMap")
inline fun <DatabaseType, ApiType, OutputType> networkBoundResource(
    mutex: Mutex = Mutex(),
    crossinline isResultEmpty: (OutputType) -> Boolean,
    crossinline query: () -> Flow<DatabaseType>,
    crossinline fetch: suspend () -> ApiType,
    crossinline saveFetchResult: suspend (old: DatabaseType, new: ApiType) -> Unit,
    crossinline shouldFetch: (DatabaseType) -> Boolean = { true },
    crossinline mapResult: (DatabaseType) -> OutputType,
) = flow {
    emit(Resource.Loading())

    val data = query().first()
    val updatedShouldFetch = if (isEndDateReached) false else shouldFetch(data)
    if (updatedShouldFetch) {
        emit(Resource.Intermediate(data))

        try {
            val newData = fetch()
            mutex.withLock { saveFetchResult(query().first(), newData) }
        } catch (throwable: Throwable) {
            emit(Resource.Error(throwable))
            return@flow
        }
    }

    emitAll(query().map { Resource.Success(it) })
}
    .mapResourceData { mapResult(it) }
    .filterNot { it is Resource.Intermediate && isResultEmpty(it.data) }
