package io.github.wulkanowy.utils

import com.crashlytics.android.Crashlytics
import timber.log.Timber

object CrashlyticsTree : Timber.Tree() {

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        Crashlytics.setInt("priority", priority)
        Crashlytics.setString("tag", tag)

        if (t == null) {
            Crashlytics.log(message)
        } else {
            Crashlytics.setString("message", message)
            Crashlytics.logException(t)
        }
    }
}

object DebugLogTree : Timber.DebugTree() {

    override fun createStackElementTag(element: StackTraceElement): String? {
        return super.createStackElementTag(element) + " - ${element.lineNumber}"
    }
}

