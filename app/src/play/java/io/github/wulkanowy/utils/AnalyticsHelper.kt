package io.github.wulkanowy.utils

import android.app.Activity
import android.os.Bundle
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.google.firebase.crashlytics.crashlytics
import io.github.wulkanowy.data.repositories.PreferencesRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnalyticsHelper @Inject constructor(
    preferencesRepository: PreferencesRepository,
    appInfo: AppInfo,
) {

    private val analytics by lazy { Firebase.analytics }

    private val crashlytics by lazy { Firebase.crashlytics }

    init {
        if (!appInfo.isDebug) {
            crashlytics.setUserId(preferencesRepository.installationId)
        }
    }

    fun logEvent(name: String, vararg params: Pair<String, Any?>) {
        Bundle().apply {
            params.forEach { (key, value) ->
                if (value == null) return@forEach
                when (value) {
                    is String -> putString(key, value)
                    is Int -> putInt(key, value)
                    is Boolean -> putBoolean(key, value)
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

    @Suppress("UNUSED_PARAMETER")
    fun popCurrentScreen(name: String?) {
    }
}
