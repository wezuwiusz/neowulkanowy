package io.github.wulkanowy.data.repositories.exam

import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.InternetObservingSettings
import io.github.wulkanowy.data.db.entities.Exam
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.utils.sunday
import io.github.wulkanowy.utils.monday
import io.github.wulkanowy.utils.uniqueSubtract
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

    fun getExams(student: Student, semester: Semester, start: LocalDate, end: LocalDate, forceRefresh: Boolean = false): Single<List<Exam>> {
        return local.getExams(semester, start.monday, end.sunday).filter { !forceRefresh }
            .switchIfEmpty(ReactiveNetwork.checkInternetConnectivity(settings)
                .flatMap {
                    if (it) remote.getExams(student, semester, start.monday, end.sunday)
                    else Single.error(UnknownHostException())
                }.flatMap { new ->
                    local.getExams(semester, start.monday, end.sunday)
                        .toSingle(emptyList())
                        .doOnSuccess { old ->
                            local.deleteExams(old.uniqueSubtract(new))
                            local.saveExams(new.uniqueSubtract(old))
                        }
                }.flatMap {
                    local.getExams(semester, start.monday, end.sunday)
                        .toSingle(emptyList())
                }).map { list -> list.filter { it.date in start..end } }
    }
}
