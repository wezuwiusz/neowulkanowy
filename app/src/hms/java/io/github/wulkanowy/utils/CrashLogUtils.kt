package io.github.wulkanowy.utils

import android.util.Log
import com.huawei.agconnect.crash.AGConnectCrash
import fr.bipi.tressence.base.FormatterPriorityTree

class CrashLogTree : FormatterPriorityTree(Log.VERBOSE) {

    private val connectCrash by lazy { AGConnectCrash.getInstance() }

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (skipLog(priority, tag, message, t)) return

        connectCrash.log(format(priority, tag, message))
    }
}

class CrashLogExceptionTree : FormatterPriorityTree(Log.ERROR, ExceptionFilter) {

    private val connectCrash by lazy { AGConnectCrash.getInstance() }

    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
        if (skipLog(priority, tag, message, t)) return

        // Disabled due to a bug in the Huawei library

        /*connectCrash.setCustomKey("priority", priority)
        connectCrash.setCustomKey("tag", tag.orEmpty())
        connectCrash.setCustomKey("message", message)

        if (t != null) {
            connectCrash.recordException(t)
        } else {
            connectCrash.recordException(StackTraceRecorder(format(priority, tag, message)))
        }*/
    }
}
