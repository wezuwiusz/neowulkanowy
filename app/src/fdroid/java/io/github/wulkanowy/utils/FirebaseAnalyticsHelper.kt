package io.github.wulkanowy.utils

import android.app.Activity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@Suppress("UNUSED_PARAMETER")
class FirebaseAnalyticsHelper @Inject constructor() {

    fun logEvent(name: String, vararg params: Pair<String, Any?>) {
        // do nothing
    }

    fun setCurrentScreen(activity: Activity, name: String?) {
        // do nothing
    }
}
