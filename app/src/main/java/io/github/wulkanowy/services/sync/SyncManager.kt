package io.github.wulkanowy.services.sync

import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.O
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.asFlow
import androidx.work.BackoffPolicy.EXPONENTIAL
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy.KEEP
import androidx.work.ExistingPeriodicWorkPolicy.REPLACE
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType.CONNECTED
import androidx.work.NetworkType.UNMETERED
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import io.github.wulkanowy.data.db.SharedPrefProvider
import io.github.wulkanowy.data.db.SharedPrefProvider.Companion.APP_VERSION_CODE_KEY
import io.github.wulkanowy.data.repositories.PreferencesRepository
import io.github.wulkanowy.services.sync.channels.Channel
import io.github.wulkanowy.utils.AppInfo
import io.github.wulkanowy.utils.isHolidays
import kotlinx.coroutines.flow.Flow
import timber.log.Timber
import java.time.LocalDate.now
import java.util.concurrent.TimeUnit.MINUTES
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncManager @Inject constructor(
    private val workManager: WorkManager,
    private val preferencesRepository: PreferencesRepository,
    channels: Set<@JvmSuppressWildcards Channel>,
    notificationManager: NotificationManagerCompat,
    sharedPrefProvider: SharedPrefProvider,
    appInfo: AppInfo
) {

    init {
        if (now().isHolidays) stopSyncWorker()

        if (SDK_INT >= O) {
            channels.forEach { it.create() }
            notificationManager.deleteNotificationChannel("lesson_channel")
            notificationManager.deleteNotificationChannel("new_entries_channel")
        }

        if (sharedPrefProvider.getLong(APP_VERSION_CODE_KEY, -1L) != appInfo.versionCode.toLong()) {
            startPeriodicSyncWorker(true)
            sharedPrefProvider.putLong(APP_VERSION_CODE_KEY, appInfo.versionCode.toLong(), true)
        }
        Timber.i("SyncManager was initialized")
    }

    fun startPeriodicSyncWorker(restart: Boolean = false) {
        if (preferencesRepository.isServiceEnabled && !now().isHolidays) {
            val serviceInterval = preferencesRepository.servicesInterval

            workManager.enqueueUniquePeriodicWork(
                SyncWorker::class.java.simpleName, if (restart) REPLACE else KEEP,
                PeriodicWorkRequestBuilder<SyncWorker>(serviceInterval, MINUTES)
                    .setInitialDelay(10, MINUTES)
                    .setBackoffCriteria(EXPONENTIAL, 30, MINUTES)
                    .setConstraints(
                        Constraints.Builder()
                            .setRequiredNetworkType(if (preferencesRepository.isServicesOnlyWifi) UNMETERED else CONNECTED)
                            .build()
                    )
                    .build()
            )
        }
    }

    fun startOneTimeSyncWorker(): Flow<WorkInfo?> {
        val work = OneTimeWorkRequestBuilder<SyncWorker>()
            .setInputData(
                Data.Builder()
                    .putBoolean("one_time", true)
                    .build()
            )
            .build()

        workManager.enqueueUniqueWork(
            "${SyncWorker::class.java.simpleName}_one_time",
            ExistingWorkPolicy.REPLACE,
            work
        )

        return workManager.getWorkInfoByIdLiveData(work.id).asFlow()
    }

    fun stopSyncWorker() {
        workManager.cancelUniqueWork(SyncWorker::class.java.simpleName)
    }
}
