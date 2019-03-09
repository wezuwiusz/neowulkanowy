package io.github.wulkanowy.services.sync

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.RxWorker
import androidx.work.WorkerParameters
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import io.github.wulkanowy.data.repositories.semester.SemesterRepository
import io.github.wulkanowy.data.repositories.student.StudentRepository
import io.github.wulkanowy.services.sync.works.Work
import io.reactivex.Completable
import io.reactivex.Single
import timber.log.Timber

class SyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParameters: WorkerParameters,
    private val studentRepository: StudentRepository,
    private val semesterRepository: SemesterRepository,
    private val works: Set<@JvmSuppressWildcards Work>
) : RxWorker(appContext, workerParameters) {

    override fun createWork(): Single<Result> {
        return studentRepository.getCurrentStudent()
            .flatMapCompletable { student ->
                semesterRepository.getCurrentSemester(student, true)
                    .flatMapCompletable { semester ->
                        Completable.mergeDelayError(works.map { it.create(student, semester) })
                    }
            }
            .toSingleDefault(Result.success())
            .onErrorReturn {
                Timber.e(it, "There was an error during synchronization")
                Result.retry()
            }
    }

    @AssistedInject.Factory
    interface Factory {

        fun create(appContext: Context, workerParameters: WorkerParameters): ListenableWorker
    }
}

