package io.github.wulkanowy.ui.modules.settings

import com.readystatesoftware.chuck.api.ChuckCollector
import io.github.wulkanowy.data.repositories.PreferencesRepository
import io.github.wulkanowy.services.job.ServiceHelper
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.utils.FirebaseAnalyticsHelper
import io.github.wulkanowy.utils.isHolidays
import org.threeten.bp.LocalDate.now
import timber.log.Timber
import javax.inject.Inject

class SettingsPresenter @Inject constructor(
    errorHandler: ErrorHandler,
    private val preferencesRepository: PreferencesRepository,
    private val serviceHelper: ServiceHelper,
    private val analytics: FirebaseAnalyticsHelper,
    private val chuckCollector: ChuckCollector
) : BasePresenter<SettingsView>(errorHandler) {

    override fun onAttachView(view: SettingsView) {
        super.onAttachView(view)
        Timber.i("Settings view is attached")

        view.run {
            setServicesSuspended(preferencesRepository.serviceEnablesKey, now().isHolidays)
        }
    }

    fun onSharedPreferenceChanged(key: String) {
        Timber.i("Change settings $key")
        when (key) {
            preferencesRepository.serviceEnablesKey -> {
                if (preferencesRepository.isServiceEnabled) serviceHelper.startFullSyncService()
                else serviceHelper.stopFullSyncService()
            }
            preferencesRepository.servicesIntervalKey,
            preferencesRepository.servicesOnlyWifiKey -> {
                serviceHelper.reloadFullSyncService()
            }
            preferencesRepository.currentThemeKey -> {
                view?.setTheme(preferencesRepository.currentTheme)
            }
            preferencesRepository.isShowChuckerNotificationKey -> {
                chuckCollector.showNotification(preferencesRepository.isShowChuckerNotification)
            }
        }
        analytics.logEvent("setting_changed", "name" to key)
    }
}
