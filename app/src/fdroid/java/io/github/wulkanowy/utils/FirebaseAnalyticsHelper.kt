package io.github.wulkanowy.utils

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseAnalyticsHelper @Inject constructor() {

    @Suppress("UNUSED_PARAMETER")
    fun logEvent(name: String, vararg params: Pair<String, Any?>) {
        // do nothing
    }
}
