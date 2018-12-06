package io.github.wulkanowy.data.repositories

import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.InternetObservingSettings
import io.github.wulkanowy.data.db.entities.Exam
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.repositories.local.ExamLocal
import io.github.wulkanowy.data.repositories.remote.ExamRemote
import io.github.wulkanowy.utils.friday
import io.github.wulkanowy.utils.monday
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

    fun getExams(semester: Semester, startDate: LocalDate, endDate: LocalDate, forceRefresh: Boolean = false): Single<List<Exam>> {
        return Single.fromCallable { startDate.monday to endDate.friday }
            .flatMap { dates ->
                local.getExams(semester, dates.first, dates.second).filter { !forceRefresh }
                    .switchIfEmpty(ReactiveNetwork.checkInternetConnectivity(settings)
                        .flatMap {
                            if (it) remote.getExams(semester, dates.first, dates.second)
                            else Single.error(UnknownHostException())
                        }.flatMap { newExams ->
                            local.getExams(semester, dates.first, dates.second)
                                .toSingle(emptyList())
                                .doOnSuccess { oldExams ->
                                    local.deleteExams(oldExams - newExams)
                                    local.saveExams(newExams - oldExams)
                                }
                        }.flatMap {
                            local.getExams(semester, dates.first, dates.second)
                                .toSingle(emptyList())
                        }).map { list -> list.filter { it.date in startDate..endDate } }
            }
    }
}
