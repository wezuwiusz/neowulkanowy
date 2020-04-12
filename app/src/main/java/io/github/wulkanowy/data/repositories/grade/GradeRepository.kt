package io.github.wulkanowy.data.repositories.grade

import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.InternetObservingSettings
import io.github.wulkanowy.data.db.entities.Grade
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

    fun getGrades(student: Student, semester: Semester, forceRefresh: Boolean = false, notify: Boolean = false): Single<List<Grade>> {
        return local.getGrades(semester).filter { !forceRefresh }
            .switchIfEmpty(ReactiveNetwork.checkInternetConnectivity(settings)
                .flatMap {
                    if (it) remote.getGrades(student, semester)
                    else Single.error(UnknownHostException())
                }.flatMap { new ->
                    local.getGrades(semester).toSingle(emptyList())
                        .doOnSuccess { old ->
                            val notifyBreakDate = old.maxBy { it.date }?.date ?: student.registrationDate.toLocalDate()
                            local.deleteGrades(old.uniqueSubtract(new))
                            local.saveGrades(new.uniqueSubtract(old)
                                .onEach {
                                    if (it.date >= notifyBreakDate) it.apply {
                                        isRead = false
                                        if (notify) isNotified = false
                                    }
                                })
                        }
                }.flatMap { local.getGrades(semester).toSingle(emptyList()) })
    }

    fun getUnreadGrades(semester: Semester): Single<List<Grade>> {
        return local.getGrades(semester).map { it.filter { grade -> !grade.isRead } }.toSingle(emptyList())
    }

    fun getNotNotifiedGrades(semester: Semester): Single<List<Grade>> {
        return local.getGrades(semester).map { it.filter { grade -> !grade.isNotified } }.toSingle(emptyList())
    }

    fun updateGrade(grade: Grade): Completable {
        return Completable.fromCallable { local.updateGrades(listOf(grade)) }
    }

    fun updateGrades(grades: List<Grade>): Completable {
        return Completable.fromCallable { local.updateGrades(grades) }
    }
}
