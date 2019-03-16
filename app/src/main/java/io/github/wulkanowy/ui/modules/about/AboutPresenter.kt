package io.github.wulkanowy.ui.modules.about

import com.mikepenz.aboutlibraries.Libs
import com.mikepenz.aboutlibraries.Libs.SpecialButton.SPECIAL1
import com.mikepenz.aboutlibraries.Libs.SpecialButton.SPECIAL2
import com.mikepenz.aboutlibraries.Libs.SpecialButton.SPECIAL3
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.utils.FirebaseAnalyticsHelper
import timber.log.Timber
import javax.inject.Inject

class AboutPresenter @Inject constructor(
    errorHandler: ErrorHandler,
    private val analytics: FirebaseAnalyticsHelper
) : BasePresenter<AboutView>(errorHandler) {

    override fun onAttachView(view: AboutView) {
        super.onAttachView(view)
        Timber.i("About view is attached")
    }

    fun onExtraSelect(type: Libs.SpecialButton?) {
        view?.run {
            when (type) {
                SPECIAL1 -> {
                    Timber.i("Opening discord invide page")
                    analytics.logEvent("open_page", "name" to "discord")
                    openDiscordInviteView()
                }
                SPECIAL2 -> {
                    Timber.i("Opening home page")
                    analytics.logEvent("open_page", "name" to "home")
                    openHomepageWebView()
                }
                SPECIAL3 -> {
                    Timber.i("Opening email client")
                    analytics.logEvent("open_page", "name" to "email")
                    openEmailClientView()
                }
            }
        }
    }
}
