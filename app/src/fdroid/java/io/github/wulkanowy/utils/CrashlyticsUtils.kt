@file:Suppress("UNUSED_PARAMETER")

package io.github.wulkanowy.utils

import android.content.Context
import timber.log.Timber

class CrashlyticsTree : Timber.Tree() {

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        // do nothing
    }
}

fun initCrashlytics(context: Context) {
    // do nothing
}
