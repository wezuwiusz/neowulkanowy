@file:Suppress("UNUSED_PARAMETER")

package io.github.wulkanowy.utils

import android.content.Context
import timber.log.Timber

fun initCrashlytics(context: Context, appInfo: AppInfo) {
    // do nothing
}

class CrashlyticsTree : Timber.Tree() {

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        // do nothing
    }
}
