package io.github.wulkanowy.ui.modules.settings.sync

import androidx.work.WorkInfo
import io.github.wulkanowy.data.repositories.PreferencesRepository
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.services.sync.SyncManager
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.utils.AnalyticsHelper
import io.github.wulkanowy.utils.isHolidays
import io.github.wulkanowy.utils.toFormattedString
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import java.time.LocalDate.now
import javax.inject.Inject

class SyncPresenter @Inject constructor(
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository,
    private val preferencesRepository: PreferencesRepository,
    private val analytics: AnalyticsHelper,
    private val syncManager: SyncManager,
) : BasePresenter<SyncView>(errorHandler, studentRepository) {

    override fun onAttachView(view: SyncView) {
        super.onAttachView(view)
        Timber.i("Settings sync view was initialized")
        view.setServicesSuspended(preferencesRepository.serviceEnableKey, now().isHolidays)
        view.initView()
        setSyncDateInView()
    }

    fun onSharedPreferenceChanged(key: String) {
        Timber.i("Change settings $key")

        preferencesRepository.apply {
            when (key) {
                serviceEnableKey -> with(syncManager) { if (isServiceEnabled) startPeriodicSyncWorker() else stopSyncWorker() }
                servicesIntervalKey, servicesOnlyWifiKey -> syncManager.startPeriodicSyncWorker(true)
            }
        }
        analytics.logEvent("setting_changed", "name" to key)
    }

    fun onSyncNowClicked() {
        view?.run {
            syncManager.startOneTimeSyncWorker().onEach { workInfo ->
                when (workInfo?.state) {
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
                        showError(
                            syncFailedString,
                            Throwable(workInfo.outputData.getString("error"))
                        )
                        analytics.logEvent("sync_now", "status" to "failed")
                    }
                    else -> Timber.d("Sync now state: ${workInfo?.state}")
                }
                if (workInfo?.state?.isFinished == true) {
                    setSyncInProgress(false)
                    setSyncDateInView()
                }
            }.catch {
                Timber.e(it, "Sync now failed")
            }.launch("sync")
        }
    }

    private fun setSyncDateInView() {
        val lastSyncDate = preferencesRepository.lasSyncDate

        if (lastSyncDate.year == 1970) return

        view?.setLastSyncDate(lastSyncDate.toFormattedString("dd.MM.yyyy HH:mm:ss"))
    }
}
