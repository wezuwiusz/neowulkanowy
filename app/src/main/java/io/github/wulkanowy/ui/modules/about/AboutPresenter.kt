package io.github.wulkanowy.ui.modules.about

import io.github.wulkanowy.data.repositories.student.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.utils.FirebaseAnalyticsHelper
import io.github.wulkanowy.utils.SchedulersProvider
import timber.log.Timber
import javax.inject.Inject

class AboutPresenter @Inject constructor(
    schedulers: SchedulersProvider,
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository,
    private val analytics: FirebaseAnalyticsHelper
) : BasePresenter<AboutView>(errorHandler, studentRepository, schedulers) {

    override fun onAttachView(view: AboutView) {
        super.onAttachView(view)
        view.initView()
        Timber.i("About view was initialized")
        loadData()
    }

    fun onItemSelected(name: String) {
        view?.run {
            when (name) {
                versionRes?.first -> {
                    Timber.i("Opening log viewer")
                    openLogViewer()
                    analytics.logEvent("about_open", "name" to "log_viewer")
                }
                feedbackRes?.first -> {
                    Timber.i("Opening email client")
                    openEmailClient()
                    analytics.logEvent("about_open", "name" to "feedback")
                }
                faqRes?.first -> {
                    Timber.i("Opening faq page")
                    openFaqPage()
                    analytics.logEvent("about_open", "name" to "faq")
                }
                discordRes?.first -> {
                    Timber.i("Opening discord")
                    openDiscordInvite()
                    analytics.logEvent("about_open", "name" to "discord")
                }
                homepageRes?.first -> {
                    Timber.i("Opening homepage")
                    openHomepage()
                    analytics.logEvent("about_open", "name" to "homepage")
                }
                licensesRes?.first -> {
                    Timber.i("Opening licenses view")
                    openLicenses()
                    analytics.logEvent("about_open", "name" to "licenses")
                }
                creatorsRes?.first -> {
                    Timber.i("Opening creators view")
                    openCreators()
                    analytics.logEvent("about_open", "name" to "creators")
                }
                privacyRes?.first -> {
                    Timber.i("Opening privacy page ")
                    openPrivacyPolicy()
                    analytics.logEvent("about_open", "name" to "privacy")
                }
            }
        }
    }

    private fun loadData() {
        view?.run {
            updateData(listOfNotNull(
                versionRes,
                creatorsRes,
                feedbackRes,
                faqRes,
                discordRes,
                homepageRes,
                licensesRes,
                privacyRes
            ))
        }
    }
}
