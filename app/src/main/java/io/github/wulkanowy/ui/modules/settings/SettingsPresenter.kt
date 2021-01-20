package io.github.wulkanowy.ui.modules.settings

import androidx.work.WorkInfo
import com.chuckerteam.chucker.api.ChuckerCollector
import io.github.wulkanowy.data.repositories.PreferencesRepository
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.services.alarm.TimetableNotificationSchedulerHelper
import io.github.wulkanowy.services.sync.SyncManager
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.utils.AnalyticsHelper
import io.github.wulkanowy.utils.AppInfo
import io.github.wulkanowy.utils.isHolidays
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import java.time.LocalDate.now
import javax.inject.Inject

class SettingsPresenter @Inject constructor(
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository,
    private val preferencesRepository: PreferencesRepository,
    private val timetableNotificationHelper: TimetableNotificationSchedulerHelper,
    private val analytics: AnalyticsHelper,
    private val syncManager: SyncManager,
    private val chuckerCollector: ChuckerCollector,
    private val appInfo: AppInfo
) : BasePresenter<SettingsView>(errorHandler, studentRepository) {

    override fun onAttachView(view: SettingsView) {
        super.onAttachView(view)
        Timber.i("Settings view was initialized")
        view.setServicesSuspended(preferencesRepository.serviceEnableKey, now().isHolidays)
        view.initView()
    }

    fun onSharedPreferenceChanged(key: String) {
        Timber.i("Change settings $key")

        preferencesRepository.apply {
            when (key) {
                serviceEnableKey -> with(syncManager) { if (isServiceEnabled) startPeriodicSyncWorker() else stopSyncWorker() }
                servicesIntervalKey, servicesOnlyWifiKey -> syncManager.startPeriodicSyncWorker(true)
                isDebugNotificationEnableKey -> chuckerCollector.showNotification = isDebugNotificationEnable
                appThemeKey -> view?.recreateView()
                isUpcomingLessonsNotificationsEnableKey -> if (!isUpcomingLessonsNotificationsEnable) timetableNotificationHelper.cancelNotification()
                appLanguageKey -> view?.run {
                    val newLang = if (appLanguage == "system") appInfo.systemLanguage else appLanguage
                    analytics.logEvent("language", "setting_changed" to newLang)

                    updateLanguage(newLang)
                    recreateView()
                }
            }
        }
        analytics.logEvent("setting_changed", "name" to key)
    }

    fun onSyncNowClicked() {
        view?.run {
            syncManager.startOneTimeSyncWorker().onEach { workInfo ->
                when (workInfo.state) {
                    WorkInfo.State.ENQUEUED -> {
                        setSyncInProgress(true)
                        Timber.i("Setting sync now started")
                        analytics.logEvent("sync_now", "status" to "started")
                    }
                    WorkInfo.State.SUCCEEDED -> {
                        showMessage(syncSuccessString)
                        analytics.logEvent("sync_now", "status" to "success")
                    }
                    WorkInfo.State.FAILED -> {
                        showError(syncFailedString, Throwable(workInfo.outputData.getString("error")))
                        analytics.logEvent("sync_now", "status" to "failed")
                    }
                    else -> Timber.d("Sync now state: ${workInfo.state}")
                }
                if (workInfo.state.isFinished) setSyncInProgress(false)
            }.catch {
                Timber.e(it, "Sync now failed")
            }.launch("sync")
        }
    }

    fun onFixSyncIssuesClicked() {
        view?.showFixSyncDialog()
    }
}
