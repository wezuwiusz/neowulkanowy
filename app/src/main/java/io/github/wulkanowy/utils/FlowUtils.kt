package io.github.wulkanowy.utils

import io.github.wulkanowy.data.Resource
import io.github.wulkanowy.data.Status
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.takeWhile

inline fun <ResultType, RequestType> networkBoundResource(
    showSavedOnLoading: Boolean = true,
    crossinline query: () -> Flow<ResultType>,
    crossinline fetch: suspend (ResultType) -> RequestType,
    crossinline saveFetchResult: suspend (old: ResultType, new: RequestType) -> Unit,
    crossinline onFetchFailed: (Throwable) -> Unit = { },
    crossinline shouldFetch: (ResultType) -> Boolean = { true },
    crossinline filterResult: (ResultType) -> ResultType = { it }
) = flow {
    emit(Resource.loading())

    val data = query().first()
    emitAll(if (shouldFetch(data)) {
        if (showSavedOnLoading) emit(Resource.loading(filterResult(data)))

        try {
            val newData = fetch(data)
            saveFetchResult(data, newData)
            query().map { Resource.success(filterResult(it)) }
        } catch (throwable: Throwable) {
            onFetchFailed(throwable)
            query().map { Resource.error(throwable, filterResult(it)) }
        }
    } else {
        query().map { Resource.success(filterResult(it)) }
    })
}

@JvmName("networkBoundResourceWithMap")
inline fun <ResultType, RequestType, T> networkBoundResource(
    showSavedOnLoading: Boolean = true,
    crossinline query: () -> Flow<ResultType>,
    crossinline fetch: suspend (ResultType) -> RequestType,
    crossinline saveFetchResult: suspend (old: ResultType, new: RequestType) -> Unit,
    crossinline onFetchFailed: (Throwable) -> Unit = { Unit },
    crossinline shouldFetch: (ResultType) -> Boolean = { true },
    crossinline mapResult: (ResultType) -> T
) = flow {
    emit(Resource.loading())

    val data = query().first()
    emitAll(if (shouldFetch(data)) {
        if (showSavedOnLoading) emit(Resource.loading(mapResult(data)))

        try {
            saveFetchResult(data, fetch(data))
            query().map { Resource.success(mapResult(it)) }
        } catch (throwable: Throwable) {
            onFetchFailed(throwable)
            query().map { Resource.error(throwable, mapResult(it)) }
        }
    } else {
        query().map { Resource.success(mapResult(it)) }
    })
}

fun <T> flowWithResource(block: suspend () -> T) = flow {
    emit(Resource.loading())
    emit(Resource.success(block()))
}.catch { emit(Resource.error(it)) }

@OptIn(FlowPreview::class)
fun <T> flowWithResourceIn(block: suspend () -> Flow<Resource<T>>) = flow {
    emit(Resource.loading())
    emitAll(block().filter { it.status != Status.LOADING || (it.status == Status.LOADING && it.data != null) })
}.catch { emit(Resource.error(it)) }

fun <T> Flow<Resource<T>>.afterLoading(callback: () -> Unit) = onEach {
    if (it.status != Status.LOADING) callback()
}

suspend fun <T> Flow<Resource<T>>.toFirstResult() = filter { it.status != Status.LOADING }.first()

suspend fun <T> Flow<Resource<T>>.waitForResult() =
    takeWhile { it.status == Status.LOADING }.collect()
