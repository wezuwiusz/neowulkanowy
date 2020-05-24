@file:Suppress("UNUSED_PARAMETER")

package io.github.wulkanowy.utils

import android.content.Context
import timber.log.Timber

open class TimberTreeNoOp : Timber.Tree() {
    override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {}
}

class CrashlyticsTree : TimberTreeNoOp()

class CrashlyticsExceptionTree : TimberTreeNoOp()
