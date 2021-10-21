package io.github.wulkanowy

import io.github.wulkanowy.utils.DispatchersProvider
import kotlinx.coroutines.Dispatchers

class TestDispatchersProvider : DispatchersProvider() {

    override val io get() = Dispatchers.Unconfined
}
