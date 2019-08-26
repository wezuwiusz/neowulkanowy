package io.github.wulkanowy.ui.modules.about

import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
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

    fun onItemSelected(item: AbstractFlexibleItem<*>) {
        if (item !is AboutItem) return
        view?.run {
            when (item.title) {
                feedbackRes?.first -> {
                    Timber.i("Opening email client ")
                    openEmailClient()
                    analytics.logEvent("about_open", "name" to "feedback")
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
            updateData(AboutScrollableHeader(), listOfNotNull(
                versionRes?.let { (title, summary, image) -> AboutItem(title, summary, image) },
                feedbackRes?.let { (title, summary, image) -> AboutItem(title, summary, image) },
                discordRes?.let { (title, summary, image) -> AboutItem(title, summary, image) },
                homepageRes?.let { (title, summary, image) -> AboutItem(title, summary, image) },
                licensesRes?.let { (title, summary, image) -> AboutItem(title, summary, image) },
                privacyRes?.let { (title, summary, image) -> AboutItem(title, summary, image) }))
        }
    }
}
