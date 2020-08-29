package io.github.wulkanowy.utils

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

open class DispatchersProvider {

    open val backgroundThread: CoroutineDispatcher
        get() = Dispatchers.IO
}
