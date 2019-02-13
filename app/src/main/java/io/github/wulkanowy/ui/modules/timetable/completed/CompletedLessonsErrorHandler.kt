package io.github.wulkanowy.ui.modules.timetable.completed

import android.content.res.Resources
import com.readystatesoftware.chuck.api.ChuckCollector
import io.github.wulkanowy.api.interceptor.FeatureDisabledException
import io.github.wulkanowy.ui.base.session.SessionErrorHandler
import javax.inject.Inject

class CompletedLessonsErrorHandler @Inject constructor(
    resources: Resources,
    chuckCollector: ChuckCollector
) : SessionErrorHandler(resources, chuckCollector) {

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
