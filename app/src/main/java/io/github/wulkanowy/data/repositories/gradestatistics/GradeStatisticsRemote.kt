package io.github.wulkanowy.data.repositories.gradestatistics

import io.github.wulkanowy.api.Api
import io.github.wulkanowy.data.db.entities.GradePointsStatistics
import io.github.wulkanowy.data.db.entities.GradeStatistics
import io.github.wulkanowy.data.db.entities.Semester
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GradeStatisticsRemote @Inject constructor(private val api: Api) {

    fun getGradeStatistics(semester: Semester, isSemester: Boolean): Single<List<GradeStatistics>> {
        return Single.just(api.apply { diaryId = semester.diaryId })
            .flatMap {
                if (isSemester) it.getGradesAnnualStatistics(semester.semesterId)
                else it.getGradesPartialStatistics(semester.semesterId)
            }
            .map { gradeStatistics ->
                gradeStatistics.map {
                    GradeStatistics(
                        semesterId = semester.semesterId,
                        studentId = semester.studentId,
                        subject = it.subject,
                        grade = it.gradeValue,
                        amount = it.amount ?: 0,
                        semester = isSemester
                    )
                }
            }
    }

    fun getGradePointsStatistics(semester: Semester): Single<List<GradePointsStatistics>> {
        return Single.just(api.apply { diaryId = semester.diaryId })
            .flatMap { it.getGradesPointsStatistics(semester.semesterId) }
            .map { gradePointsStatistics ->
                gradePointsStatistics.map {
                    GradePointsStatistics(
                        semesterId = semester.semesterId,
                        studentId = semester.studentId,
                        subject = it.subject,
                        others = it.others,
                        student = it.student
                    )
                }
            }
    }
}
