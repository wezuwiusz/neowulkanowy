package io.github.wulkanowy.ui.modules.settings

import androidx.work.WorkInfo
import com.chuckerteam.chucker.api.ChuckerCollector
import io.github.wulkanowy.data.repositories.preferences.PreferencesRepository
import io.github.wulkanowy.data.repositories.student.StudentRepository
import io.github.wulkanowy.services.sync.SyncManager
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.utils.AppInfo
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
    private val chuckerCollector: ChuckerCollector,
    private val appInfo: AppInfo
) : BasePresenter<SettingsView>(errorHandler, studentRepository, schedulers) {

    override fun onAttachView(view: SettingsView) {
        super.onAttachView(view)
        Timber.i("Settings view was initialized")
        view.setServicesSuspended(preferencesRepository.serviceEnableKey, now().isHolidays)
        view.initView()
    }

    fun onSharedPreferenceChanged(key: String) {
        Timber.i("Change settings $key")

        with(preferencesRepository) {
            when (key) {
                serviceEnableKey -> with(syncManager) { if (isServiceEnabled) startPeriodicSyncWorker() else stopSyncWorker() }
                servicesIntervalKey, servicesOnlyWifiKey -> syncManager.startPeriodicSyncWorker(true)
                isDebugNotificationEnableKey -> chuckerCollector.showNotification = isDebugNotificationEnable
                appThemeKey -> view?.recreateView()
                appLanguageKey -> view?.run {
                    updateLanguage(if (appLanguage == "system") appInfo.systemLanguage else appLanguage)
                    recreateView()
                }
                else -> Unit
            }
        }
        analytics.logEvent("setting_changed", "name" to key)
    }

    fun onSyncNowClicked() {
        view?.showForceSyncDialog()
    }

    fun onForceSyncDialogSubmit() {
        view?.run {
            val successString = syncSuccessString
            val failedString = syncFailedString
            disposable.add(syncManager.startOneTimeSyncWorker()
                .doOnSubscribe {
                    setSyncInProgress(true)
                    Timber.i("Setting sync now started")
                    analytics.logEvent("sync_now", "status" to "started")
                }
                .doFinally { setSyncInProgress(false) }
                .subscribe({ workInfo ->
                    when (workInfo.state) {
                        WorkInfo.State.SUCCEEDED -> {
                            showMessage(successString)
                            analytics.logEvent("sync_now", "status" to "success")
                        }
                        WorkInfo.State.FAILED -> {
                            showError(failedString, Throwable(workInfo.outputData.getString("error")))
                            analytics.logEvent("sync_now", "status" to "failed")
                        }
                        else -> Timber.d("Sync now state: ${workInfo.state}")
                    }
                }, {
                    Timber.e(it, "Sync now failed")
                })
            )
        }
    }
}
