package io.github.wulkanowy.utils.schedulers

import io.reactivex.Scheduler

interface SchedulersManager {

    fun mainThread(): Scheduler

    fun backgroundThread(): Scheduler
}
