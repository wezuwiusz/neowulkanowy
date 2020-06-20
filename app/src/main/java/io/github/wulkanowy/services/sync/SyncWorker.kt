package io.github.wulkanowy.services.sync

import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.BigTextStyle
import androidx.core.app.NotificationCompat.PRIORITY_DEFAULT
import androidx.core.app.NotificationManagerCompat
import androidx.work.Data
import androidx.work.ListenableWorker
import androidx.work.RxWorker
import androidx.work.WorkerParameters
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import io.github.wulkanowy.R
import io.github.wulkanowy.data.repositories.preferences.PreferencesRepository
import io.github.wulkanowy.data.repositories.semester.SemesterRepository
import io.github.wulkanowy.data.repositories.student.StudentRepository
import io.github.wulkanowy.sdk.exception.FeatureNotAvailableException
import io.github.wulkanowy.sdk.scrapper.exception.FeatureDisabledException
import io.github.wulkanowy.services.sync.channels.DebugChannel
import io.github.wulkanowy.services.sync.works.Work
import io.github.wulkanowy.utils.getCompatColor
import io.reactivex.Completable
import io.reactivex.Single
import kotlinx.coroutines.rx2.rxMaybe
import kotlinx.coroutines.rx2.rxSingle
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
        Timber.i("SyncWorker is starting")
        return rxSingle { studentRepository.isCurrentStudentSet() }
            .filter { true }
            .flatMap { rxMaybe { studentRepository.getCurrentStudent() } }
            .flatMapCompletable { student ->
                rxSingle { semesterRepository.getCurrentSemester(student, true) }
                    .flatMapCompletable { semester ->
                        Completable.mergeDelayError(works.map { work ->
                            work.create(student, semester)
                                .onErrorResumeNext {
                                    if (it is FeatureDisabledException || it is FeatureNotAvailableException) Completable.complete()
                                    else Completable.error(it)
                                }
                                .doOnSubscribe { Timber.i("${work::class.java.simpleName} is starting") }
                                .doOnError { Timber.i("${work::class.java.simpleName} result: An exception occurred") }
                                .doOnComplete { Timber.i("${work::class.java.simpleName} result: Success") }
                        })
                    }
            }
            .toSingleDefault(Result.success())
            .onErrorReturn {
                Timber.e(it, "There was an error during synchronization")
                when {
                    inputData.getBoolean("one_time", false) -> {
                        Result.failure(Data.Builder()
                            .putString("error", it.toString())
                            .build()
                        )
                    }
                    else -> Result.retry()
                }
            }
            .doOnSuccess {
                if (preferencesRepository.isDebugNotificationEnable) notify(it)
                Timber.i("SyncWorker result: $it")
            }
    }

    private fun notify(result: Result) {
        notificationManager.notify(Random.nextInt(Int.MAX_VALUE), NotificationCompat.Builder(applicationContext, DebugChannel.CHANNEL_ID)
            .setContentTitle("Debug notification")
            .setSmallIcon(R.drawable.ic_stat_push)
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
