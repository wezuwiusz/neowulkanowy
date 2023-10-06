package io.github.wulkanowy.ui.modules.settings.advanced

import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.utils.AnalyticsHelper
import timber.log.Timber
import javax.inject.Inject

class AdvancedPresenter @Inject constructor(
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository,
    private val analytics: AnalyticsHelper,
) : BasePresenter<AdvancedView>(errorHandler, studentRepository) {

    override fun onAttachView(view: AdvancedView) {
        super.onAttachView(view)
        Timber.i("Settings advanced view was initialized")
    }

    fun onSharedPreferenceChanged(key: String?) {
        key ?: return
        Timber.i("Change settings $key")
        analytics.logEvent("setting_changed", "name" to key)
    }
}
