package io.github.wulkanowy.utils

import android.app.Activity
import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnalyticsHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val analytics by lazy { FirebaseAnalytics.getInstance(context) }

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
            analytics.logEvent(name, this)
        }
    }

    fun setCurrentScreen(activity: Activity, name: String?) {
        analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, Bundle().apply {
            putString(FirebaseAnalytics.Param.SCREEN_NAME, name)
            putString(FirebaseAnalytics.Param.SCREEN_CLASS, activity::class.simpleName)
        })
    }
}
