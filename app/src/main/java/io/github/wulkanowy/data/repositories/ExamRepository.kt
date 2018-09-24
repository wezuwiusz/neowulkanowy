package io.github.wulkanowy.data.repositories

import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.InternetObservingSettings
import io.github.wulkanowy.data.db.entities.Exam
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.repositories.local.ExamLocal
import io.github.wulkanowy.data.repositories.remote.ExamRemote
import io.reactivex.Single
import org.threeten.bp.LocalDate
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ExamRepository @Inject constructor(
        private val settings: InternetObservingSettings,
        private val local: ExamLocal,
        private val remote: ExamRemote
) {

    fun getExams(semester: Semester, date: LocalDate, forceRefresh: Boolean = false): Single<List<Exam>> {
        return local.getExams(semester, date).filter { !forceRefresh }
                .switchIfEmpty(ReactiveNetwork.checkInternetConnectivity(settings)
                        .flatMap {
                            if (it) remote.getExams(semester, date)
                            else Single.error(UnknownHostException())
                        }.flatMap { newExams ->
                            local.getExams(semester, date).toSingle(emptyList())
                                    .map {
                                        local.deleteExams(it - newExams)
                                        local.saveExams(newExams - it)

                                        newExams
                                    }
                        }
                )
    }
}
