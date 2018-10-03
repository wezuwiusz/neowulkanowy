package io.github.wulkanowy.data.repositories

import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.InternetObservingSettings
import io.github.wulkanowy.data.db.entities.Exam
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.repositories.local.ExamLocal
import io.github.wulkanowy.data.repositories.remote.ExamRemote
import io.github.wulkanowy.utils.weekFirstDayAlwaysCurrent
import io.reactivex.Single
import org.threeten.bp.DayOfWeek
import org.threeten.bp.LocalDate
import org.threeten.bp.temporal.TemporalAdjusters
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
        val start = startDate.weekFirstDayAlwaysCurrent
        val end = endDate.with(TemporalAdjusters.nextOrSame(DayOfWeek.FRIDAY))

        return local.getExams(semester, start, end).filter { !forceRefresh }
                .switchIfEmpty(ReactiveNetwork.checkInternetConnectivity(settings).flatMap {
                    if (it) remote.getExams(semester, start, end)
                    else Single.error(UnknownHostException())
                }.flatMap { newExams ->
                    local.getExams(semester, start, end).toSingle(emptyList()).map { grades ->
                        local.deleteExams(grades - newExams)
                        local.saveExams(newExams - grades)
                        newExams
                    }
                }).map { list ->
                    list.asSequence().filter {
                        it.date in startDate..endDate
                    }.toList()
                }
    }
}
