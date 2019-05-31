package io.github.wulkanowy.ui.modules.settings

import com.readystatesoftware.chuck.api.ChuckCollector
import io.github.wulkanowy.data.repositories.preferences.PreferencesRepository
import io.github.wulkanowy.data.repositories.student.StudentRepository
import io.github.wulkanowy.services.sync.SyncManager
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.utils.FirebaseAnalyticsHelper
import io.github.wulkanowy.utils.SchedulersProvider
import io.github.wulkanowy.utils.isHolidays
import org.threeten.bp.LocalDate.now
import timber.log.Timber
import javax.inject.Inject

class SettingsPresenter @Inject constructor(
    schedulers: SchedulersProvider,
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository,
    private val preferencesRepository: PreferencesRepository,
    private val analytics: FirebaseAnalyticsHelper,
    private val syncManager: SyncManager,
    private val chuckCollector: ChuckCollector
) : BasePresenter<SettingsView>(errorHandler, studentRepository, schedulers) {

    override fun onAttachView(view: SettingsView) {
        super.onAttachView(view)
        Timber.i("Settings view was initialized")
        view.setServicesSuspended(preferencesRepository.serviceEnableKey, now().isHolidays)
    }

    fun onSharedPreferenceChanged(key: String) {
        Timber.i("Change settings $key")
        preferencesRepository.apply {
            when (key) {
                serviceEnableKey -> syncManager.run { if (isServiceEnabled) startSyncWorker() else stopSyncWorker() }
                servicesIntervalKey, servicesOnlyWifiKey -> syncManager.startSyncWorker(true)
                isDebugNotificationEnableKey -> chuckCollector.showNotification(isDebugNotificationEnable)
                appThemeKey -> view?.recreateView()
            }
        }
        analytics.logEvent("setting_changed", "name" to key)
    }
}
