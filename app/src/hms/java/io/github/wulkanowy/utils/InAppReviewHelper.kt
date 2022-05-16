package io.github.wulkanowy.utils

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.wulkanowy.ui.modules.main.MainActivity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@Suppress("UNUSED_PARAMETER", "unused")
class InAppReviewHelper @Inject constructor(
    @ApplicationContext private val context: Context
)  {

    fun showInAppReview(activity: MainActivity) {
        // do nothing
    }
}
