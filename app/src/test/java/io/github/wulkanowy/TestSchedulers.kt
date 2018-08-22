package io.github.wulkanowy

import io.github.wulkanowy.utils.schedulers.SchedulersManager
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers

class TestSchedulers : SchedulersManager {

    override fun backgroundThread(): Scheduler = Schedulers.trampoline()

    override fun mainThread(): Scheduler = Schedulers.trampoline()
}

