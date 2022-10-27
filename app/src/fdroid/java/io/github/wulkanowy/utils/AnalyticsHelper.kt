package io.github.wulkanowy.utils

import android.app.Activity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@Suppress("UNUSED_PARAMETER")
class AnalyticsHelper @Inject constructor() {

    fun logEvent(name: String, vararg params: Pair<String, Any?>) = Unit
    fun setCurrentScreen(activity: Activity, name: String?) = Unit
    fun popCurrentScreen(name: String?) = Unit
}
