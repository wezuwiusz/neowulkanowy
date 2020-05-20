package io.github.wulkanowy.data.repositories.grade

import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.InternetObservingSettings
import io.github.wulkanowy.data.db.entities.Grade
import io.github.wulkanowy.data.db.entities.GradeSummary
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.utils.uniqueSubtract
import io.reactivex.Completable
import io.reactivex.Single
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GradeRepository @Inject constructor(
    private val settings: InternetObservingSettings,
    private val local: GradeLocal,
    private val remote: GradeRemote
) {

    fun getGrades(student: Student, semester: Semester, forceRefresh: Boolean = false, notify: Boolean = false): Single<Pair<List<Grade>, List<GradeSummary>>> {
        return local.getGradesDetails(semester).flatMap { details ->
            local.getGradesSummary(semester).map { summary -> details to summary }
        }.filter { !forceRefresh }
            .switchIfEmpty(ReactiveNetwork.checkInternetConnectivity(settings).flatMap {
                if (it) remote.getGrades(student, semester)
                else Single.error(UnknownHostException())
            }.flatMap { (newDetails, newSummary) ->
                local.getGradesDetails(semester).toSingle(emptyList())
                    .doOnSuccess { old ->
                        val notifyBreakDate = old.maxBy { it.date }?.date ?: student.registrationDate.toLocalDate()
                        local.deleteGrades(old.uniqueSubtract(newDetails))
                        local.saveGrades(newDetails.uniqueSubtract(old)
                            .onEach {
                                if (it.date >= notifyBreakDate) it.apply {
                                    isRead = false
                                    if (notify) isNotified = false
                                }
                            })
                    }.flatMap {
                        local.getGradesSummary(semester).toSingle(emptyList())
                            .doOnSuccess { old ->
                                local.deleteGradesSummary(old.uniqueSubtract(newSummary))
                                local.saveGradesSummary(newSummary.uniqueSubtract(old))
                            }
                    }
            }.flatMap {
                local.getGradesDetails(semester).toSingle(emptyList()).flatMap { details ->
                    local.getGradesSummary(semester).toSingle(emptyList()).map { summary ->
                        details to summary
                    }
                }
            })
    }

    fun getUnreadGrades(semester: Semester): Single<List<Grade>> {
        return local.getGradesDetails(semester).map { it.filter { grade -> !grade.isRead } }.toSingle(emptyList())
    }

    fun getNotNotifiedGrades(semester: Semester): Single<List<Grade>> {
        return local.getGradesDetails(semester).map { it.filter { grade -> !grade.isNotified } }.toSingle(emptyList())
    }

    fun updateGrade(grade: Grade): Completable {
        return Completable.fromCallable { local.updateGrades(listOf(grade)) }
    }

    fun updateGrades(grades: List<Grade>): Completable {
        return Completable.fromCallable { local.updateGrades(grades) }
    }
}
