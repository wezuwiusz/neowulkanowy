package io.github.wulkanowy.utils

import kotlinx.coroutines.Dispatchers

open class DispatchersProvider {

    open val io get() = Dispatchers.IO
}
