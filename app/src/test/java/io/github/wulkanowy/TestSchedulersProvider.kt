package io.github.wulkanowy

import io.github.wulkanowy.utils.SchedulersProvider
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers

class TestSchedulersProvider : SchedulersProvider() {

    override val backgroundThread: Scheduler
        get() = Schedulers.trampoline()

    override val mainThread: Scheduler
        get() = Schedulers.trampoline()
}

