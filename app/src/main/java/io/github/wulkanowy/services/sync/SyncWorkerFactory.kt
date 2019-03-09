package io.github.wulkanowy.services.sync

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import timber.log.Timber
import javax.inject.Inject

class SyncWorkerFactory @Inject constructor(private val syncWorkerFactory: SyncWorker.Factory) : WorkerFactory() {

    override fun createWorker(appContext: Context, workerClassName: String, workerParameters: WorkerParameters): ListenableWorker? {
        return if (workerClassName == SyncWorker::class.java.name) {
            syncWorkerFactory.create(appContext, workerParameters)
        } else {
            Timber.e(IllegalArgumentException("Unknown worker class name: $workerClassName"))
            null
        }
    }
}
