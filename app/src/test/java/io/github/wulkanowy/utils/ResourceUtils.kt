package io.github.wulkanowy.utils

import io.github.wulkanowy.data.Resource

enum class Status {
    LOADING, SUCCESS, ERROR
}

val <T> Resource<T>.status
    get() = when (this) {
        is Resource.Error -> Status.ERROR
        is Resource.Loading -> Status.LOADING
        is Resource.Success -> Status.SUCCESS
    }