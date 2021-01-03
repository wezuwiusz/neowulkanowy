package io.github.wulkanowy.ui.modules.timetable.completed

import android.content.res.Resources
import io.github.wulkanowy.sdk.scrapper.exception.FeatureDisabledException
import io.github.wulkanowy.ui.base.ErrorHandler
import javax.inject.Inject

class CompletedLessonsErrorHandler @Inject constructor(resources: Resources) : ErrorHandler(resources) {

    var onFeatureDisabled: () -> Unit = {}

    override fun proceed(error: Throwable) {
        when (error) {
            is FeatureDisabledException -> onFeatureDisabled()
            else -> super.proceed(error)
        }
    }

    override fun clear() {
        super.clear()
        onFeatureDisabled = {}
    }
}
