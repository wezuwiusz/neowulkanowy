package io.github.wulkanowy.utils

import android.util.Log
import com.huawei.agconnect.crash.AGConnectCrash
import fr.bipi.tressence.base.FormatterPriorityTree
import fr.bipi.tressence.common.StackTraceRecorder

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

        if (t != null) {
            connectCrash.recordException(t)
        } else {
            connectCrash.recordException(StackTraceRecorder(format(priority, tag, message)))
        }
    }
}
