package io.github.wulkanowy.utils

import android.app.Activity
import android.content.Context
import android.os.Bundle
import com.huawei.agconnect.crash.AGConnectCrash
import com.huawei.hms.analytics.HiAnalytics
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

    private val analytics by lazy { HiAnalytics.getInstance(context) }

    private val connectCrash by lazy { AGConnectCrash.getInstance() }

    init {
        if (!appInfo.isDebug) {
            connectCrash.setUserId(preferencesRepository.installationId)
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
            analytics.onEvent(name, this)
        }
    }

    fun setCurrentScreen(activity: Activity, name: String?) {
        analytics.pageStart(name, activity::class.simpleName)
    }

    fun popCurrentScreen(name: String?) {
        analytics.pageEnd(name)
    }
}
