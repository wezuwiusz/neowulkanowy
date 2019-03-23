package io.github.wulkanowy.services.sync

import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.BigTextStyle
import androidx.core.app.NotificationCompat.PRIORITY_DEFAULT
import androidx.core.app.NotificationManagerCompat
import androidx.work.ListenableWorker
import androidx.work.RxWorker
import androidx.work.WorkerParameters
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import io.github.wulkanowy.R
import io.github.wulkanowy.api.interceptor.FeatureDisabledException
import io.github.wulkanowy.data.repositories.preferences.PreferencesRepository
import io.github.wulkanowy.data.repositories.semester.SemesterRepository
import io.github.wulkanowy.data.repositories.student.StudentRepository
import io.github.wulkanowy.services.sync.channels.DebugChannel
import io.github.wulkanowy.services.sync.works.Work
import io.github.wulkanowy.utils.getCompatColor
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import timber.log.Timber
import kotlin.random.Random

class SyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParameters: WorkerParameters,
    private val studentRepository: StudentRepository,
    private val semesterRepository: SemesterRepository,
    private val works: Set<@JvmSuppressWildcards Work>,
    private val preferencesRepository: PreferencesRepository,
    private val notificationManager: NotificationManagerCompat
) : RxWorker(appContext, workerParameters) {

    override fun createWork(): Single<Result> {
        return studentRepository.isStudentSaved()
            .flatMapCompletable { isSaved ->
                if (isSaved) {
                    studentRepository.getCurrentStudent()
                        .flatMapCompletable { student ->
                            semesterRepository.getCurrentSemester(student)
                                .flatMapCompletable { semester ->
                                    Completable.mergeDelayError(Flowable.fromIterable(works.map { it.create(student, semester) }), 3)
                                }
                        }
                } else Completable.complete()
            }
            .toSingleDefault(Result.success())
            .onErrorReturn {
                Timber.e(it, "There was an error during synchronization")
                if (it is FeatureDisabledException) Result.success()
                else Result.retry()
            }
            .doOnSuccess { if (preferencesRepository.isDebugNotificationEnable) notify(it) }
    }

    private fun notify(result: Result) {
        notificationManager.notify(Random.nextInt(Int.MAX_VALUE), NotificationCompat.Builder(applicationContext, DebugChannel.CHANNEL_ID)
            .setContentTitle("Debug notification")
            .setSmallIcon(R.drawable.ic_more_settings_24dp)
            .setAutoCancel(true)
            .setColor(applicationContext.getCompatColor(R.color.colorPrimary))
            .setStyle(BigTextStyle().bigText("${SyncWorker::class.java.simpleName} result: $result"))
            .setPriority(PRIORITY_DEFAULT)
            .build())
    }

    @AssistedInject.Factory
    interface Factory {

        fun create(appContext: Context, workerParameters: WorkerParameters): ListenableWorker
    }
}

