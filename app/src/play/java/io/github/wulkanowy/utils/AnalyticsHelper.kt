package io.github.wulkanowy.utils

import android.app.Activity
import android.content.Context
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.wulkanowy.data.repositories.PreferencesRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnalyticsHelper @Inject constructor(
    @ApplicationContext private val context: Context,
    preferencesRepository: PreferencesRepository,
    appInfo: AppInfo,
) {

    private val analytics by lazy { FirebaseAnalytics.getInstance(context) }

    private val crashlytics by lazy { FirebaseCrashlytics.getInstance() }

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
