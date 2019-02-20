package io.github.wulkanowy.data.repositories.grade

import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.InternetObservingSettings
import io.github.wulkanowy.data.db.entities.Grade
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
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
                    if (it) remote.getGrades(semester)
                    else Single.error(UnknownHostException())
                }.flatMap { newGrades ->
                    local.getGrades(semester).toSingle(emptyList())
                        .doOnSuccess { oldGrades ->
                            local.deleteGrades(oldGrades - newGrades)
                            local.saveGrades((newGrades - oldGrades)
                                .onEach {
                                    if (student.registrationDate <= it.date.atStartOfDay()) {
                                        if (notify) it.isNotified = false
                                        it.isRead = false
                                    }
                                })
                        }
                }.flatMap { local.getGrades(semester).toSingle(emptyList()) })
    }

    fun getNewGrades(semester: Semester): Single<List<Grade>> {
        return local.getNewGrades(semester).toSingle(emptyList())
    }

    fun updateGrade(grade: Grade): Completable {
        return local.updateGrade(grade)
    }

    fun updateGrades(grades: List<Grade>): Completable {
        return local.updateGrades(grades)
    }
}
