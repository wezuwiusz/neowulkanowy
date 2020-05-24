package io.github.wulkanowy.utils

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import fr.bipi.tressence.base.FormatterPriorityTree
import fr.bipi.tressence.common.StackTraceRecorder
import io.github.wulkanowy.sdk.exception.FeatureDisabledException
import io.github.wulkanowy.sdk.exception.FeatureNotAvailableException
import java.io.InterruptedIOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class CrashlyticsTree : FormatterPriorityTree(Log.VERBOSE) {

    private val crashlytics by lazy { FirebaseCrashlytics.getInstance() }

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (skipLog(priority, tag, message, t)) return

        crashlytics.log(format(priority, tag, message))
    }
}

class CrashlyticsExceptionTree : FormatterPriorityTree(Log.ERROR) {

    private val crashlytics by lazy { FirebaseCrashlytics.getInstance() }

    override fun skipLog(priority: Int, tag: String?, message: String, t: Throwable?): Boolean {
        if (t is FeatureDisabledException || t is FeatureNotAvailableException || t is UnknownHostException || t is SocketTimeoutException || t is InterruptedIOException) {
            return true
        }

        return super.skipLog(priority, tag, message, t)
    }

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (skipLog(priority, tag, message, t)) return

        crashlytics.setCustomKey("priority", priority)
        crashlytics.setCustomKey("tag", tag.orEmpty())
        crashlytics.setCustomKey("message", message)
        if (t != null) {
            crashlytics.recordException(t)
        } else {
            crashlytics.recordException(StackTraceRecorder(format(priority, tag, message)))
        }
    }
}
