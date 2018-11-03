package io.github.wulkanowy.utils

import com.crashlytics.android.Crashlytics
import timber.log.Timber

class DebugLogTree : Timber.DebugTree() {

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        super.log(priority, "Wulkanowy", message, t)
    }
}

class CrashlyticsTree : Timber.Tree() {

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        Crashlytics.setInt("priority", priority)
        Crashlytics.setString("tag", tag)

        if (t == null) Crashlytics.log(message)
        else Crashlytics.logException(t)
    }
}
