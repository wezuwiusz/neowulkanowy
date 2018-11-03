package io.github.wulkanowy.ui.modules.about

import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.Libs.SpecialButton.SPECIAL1
import com.mikepenz.aboutlibraries.Libs.SpecialButton.SPECIAL2
import com.mikepenz.aboutlibraries.Libs.SpecialButton.SPECIAL3
import io.github.wulkanowy.data.ErrorHandler
import io.github.wulkanowy.ui.base.BasePresenter
import timber.log.Timber
import javax.inject.Inject

class AboutPresenter @Inject constructor(errorHandler: ErrorHandler) : BasePresenter<AboutView>(errorHandler) {

    fun onExtraSelect(type: Libs.SpecialButton?) {
        view?.run {
            when (type) {
                SPECIAL1 -> {
                    Timber.i("Opening github page")
                    openSourceWebView()
                }
                SPECIAL2 -> {
                    Timber.i("Opening issues page")
                    openIssuesWebView()
                }
                SPECIAL3 -> { }
            }
        }
    }
}
