package io.github.wulkanowy.ui.modules.settings.ads

import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.utils.AdsHelper
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class AdsPresenter @Inject constructor(
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository,
    private val adsHelper: AdsHelper,
) : BasePresenter<AdsView>(errorHandler, studentRepository) {

    override fun onAttachView(view: AdsView) {
        super.onAttachView(view)
        view.initView()
        Timber.i("Settings ads view was initialized")
    }

    fun onWatchSingleAdSelected() {
        view?.showLoadingSupportAd(true)
        presenterScope.launch {
            runCatching { adsHelper.getSupportAd() }
                .onFailure {
                    errorHandler.dispatch(it)

                    view?.run {
                        showLoadingSupportAd(false)
                        showWatchAdOncePerVisit(false)
                    }
                }
                .onSuccess {
                    it?.let { view?.showAd(it) }

                    view?.run {
                        showLoadingSupportAd(false)
                        showWatchAdOncePerVisit(true)
                    }
                }
        }
    }

    fun onPrivacySelected() {
        view?.openPrivacyPolicy()
    }

    fun onAdsEnabledSelected(newValue: Boolean) {
        if (newValue) {
            adsHelper.initialize()
        }
    }

    fun onUmpAgreementsSelected() {
        adsHelper.openAdsUmpAgreements()
    }
}
