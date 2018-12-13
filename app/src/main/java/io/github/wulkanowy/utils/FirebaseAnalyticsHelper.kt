package io.github.wulkanowy.utils

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import javax.inject.Singleton

@Singleton
class FirebaseAnalyticsHelper(private val analytics: FirebaseAnalytics) {

    fun logEvent(name: String, params: Map<String, Any?>) {
        Bundle().apply {
            params.forEach {
                if (it.value == null) return@forEach
                when (it.value) {
                    is String, is String? -> putString(it.key, it.value as String)
                    is Int, is Int? -> putInt(it.key, it.value as Int)
                    is Boolean, is Boolean? -> putBoolean(it.key, it.value as Boolean)
                }
            }
            analytics.logEvent(name, this)
        }
    }
}
