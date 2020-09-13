package io.github.wulkanowy.data

data class Resource<T>(val status: Status, val data: T?, val error: Throwable?) {
    companion object {
        fun <T> success(data: T?): Resource<T> {
            return Resource(Status.SUCCESS, data, null)
        }

        fun <T> error(error: Throwable?, data: T? = null): Resource<T> {
            return Resource(Status.ERROR, data, error)
        }

        fun <T> loading(data: T? = null): Resource<T> {
            return Resource(Status.LOADING, data, null)
        }
    }
}

enum class Status {
    LOADING,
    SUCCESS,
    ERROR
}
