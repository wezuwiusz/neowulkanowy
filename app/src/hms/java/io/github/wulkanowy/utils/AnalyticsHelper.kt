package io.github.wulkanowy.utils

import android.app.Activity
import android.content.Context
import android.os.Bundle
import com.huawei.hms.analytics.HiAnalytics
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnalyticsHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val analytics by lazy { HiAnalytics.getInstance(context) }

    fun logEvent(name: String, vararg params: Pair<String, Any?>) {
        Bundle().apply {
            params.forEach {
                if (it.second == null) return@forEach
                when (it.second) {
                    is String, is String? -> putString(it.first, it.second as String)
                    is Int, is Int? -> putInt(it.first, it.second as Int)
                    is Boolean, is Boolean? -> putBoolean(it.first, it.second as Boolean)
                }
            }
            analytics.onEvent(name, this)
        }
    }

    fun setCurrentScreen(activity: Activity, name: String?) {
        analytics.onEvent("screen_view", Bundle().apply {
            putString("screen_name", name)
            putString("screen_class", activity::class.simpleName)
        })
    }
}
