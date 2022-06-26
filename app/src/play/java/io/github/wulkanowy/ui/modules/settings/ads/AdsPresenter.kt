package io.github.wulkanowy.ui.modules.settings.ads

import io.github.wulkanowy.data.repositories.PreferencesRepository
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
    private val preferencesRepository: PreferencesRepository
) : BasePresenter<AdsView>(errorHandler, studentRepository) {

    override fun onAttachView(view: AdsView) {
        super.onAttachView(view)
        view.initView()
        Timber.i("Settings ads view was initialized")

        view.showProcessingDataSummary(
            preferencesRepository.isPersonalizedAdsEnabled.takeIf {
                preferencesRepository.isAgreeToProcessData
            })
    }

    fun onWatchSingleAdSelected() {
        view?.showLoadingSupportAd(true)
        presenterScope.launch {
            runCatching { adsHelper.getSupportAd() }
                .onFailure(errorHandler::dispatch)
                .onSuccess { it?.let { view?.showAd(it) } }

            view?.run {
                showLoadingSupportAd(false)
                showWatchAdOncePerVisit(true)
            }
        }
    }

    fun onConsentSelected(isChecked: Boolean) {
        if (isChecked) {
            view?.showPrivacyPolicyDialog()
        } else {
            view?.showProcessingDataSummary(null)
            view?.setCheckedAdsEnabled(false)
        }
    }

    fun onPrivacySelected() {
        view?.openPrivacyPolicy()
    }

    fun onPrivacyDialogCanceled() {
        view?.setCheckedProcessingData(false)
    }

    fun onNonPersonalizedAgree() {
        preferencesRepository.isPersonalizedAdsEnabled = false

        adsHelper.initialize()

        view?.setCheckedProcessingData(true)
        view?.showProcessingDataSummary(false)
    }

    fun onPersonalizedAgree() {
        preferencesRepository.isPersonalizedAdsEnabled = true

        adsHelper.initialize()

        view?.setCheckedProcessingData(true)
        view?.showProcessingDataSummary(true)
    }
}
