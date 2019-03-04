package io.github.wulkanowy.data.repositories.gradestatistics

import io.github.wulkanowy.api.Api
import io.github.wulkanowy.data.db.entities.GradeStatistics
import io.github.wulkanowy.data.db.entities.Semester
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GradeStatisticsRemote @Inject constructor(private val api: Api) {

    fun getGradeStatistics(semester: Semester, isSemester: Boolean): Single<List<GradeStatistics>> {
        return Single.just(api.apply { diaryId = semester.diaryId })
            .flatMap { it.getGradesStatistics(semester.semesterId, isSemester) }
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
}
