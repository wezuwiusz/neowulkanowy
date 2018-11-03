package io.github.wulkanowy.utils

import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

open class SchedulersProvider {

    open val mainThread: Scheduler
        get() = AndroidSchedulers.mainThread()

    open val backgroundThread: Scheduler
        get() = Schedulers.io()
}
