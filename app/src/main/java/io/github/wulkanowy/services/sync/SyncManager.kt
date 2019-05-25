package io.github.wulkanowy.services.sync

import android.os.Build.VERSION.SDK_INT
import android.os.Build.VERSION_CODES.O
import androidx.work.BackoffPolicy.EXPONENTIAL
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy.KEEP
import androidx.work.ExistingPeriodicWorkPolicy.REPLACE
import androidx.work.NetworkType.CONNECTED
import androidx.work.NetworkType.UNMETERED
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import io.github.wulkanowy.data.repositories.preferences.PreferencesRepository
import io.github.wulkanowy.services.sync.channels.DebugChannel
import io.github.wulkanowy.services.sync.channels.NewEntriesChannel
import io.github.wulkanowy.utils.isHolidays
import org.threeten.bp.LocalDate.now
import timber.log.Timber
import java.util.concurrent.TimeUnit.MINUTES
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class SyncManager @Inject constructor(
    private val workManager: WorkManager,
    private val preferencesRepository: PreferencesRepository,
    newEntriesChannel: NewEntriesChannel,
    debugChannel: DebugChannel,
    @Named("isDebug") isDebug: Boolean
) {

    init {
        if (SDK_INT >= O) newEntriesChannel.create()
        if (SDK_INT >= O && isDebug) debugChannel.create()
        if (now().isHolidays) stopSyncWorker()
        Timber.i("SyncManager was initialized")
    }

    fun startSyncWorker(restart: Boolean = false) {
        if (preferencesRepository.isServiceEnabled && !now().isHolidays) {
            workManager.enqueueUniquePeriodicWork(SyncWorker::class.java.simpleName, if (restart) REPLACE else KEEP,
                PeriodicWorkRequest.Builder(SyncWorker::class.java, preferencesRepository.servicesInterval, MINUTES, 10, MINUTES)
                    .setBackoffCriteria(EXPONENTIAL, 30, MINUTES)
                    .setConstraints(Constraints.Builder()
                        .setRequiredNetworkType(if (preferencesRepository.isServicesOnlyWifi) UNMETERED else CONNECTED)
                        .build())
                    .build())
        }
    }

    fun stopSyncWorker() {
        workManager.cancelUniqueWork(SyncWorker::class.java.simpleName)
    }
}
