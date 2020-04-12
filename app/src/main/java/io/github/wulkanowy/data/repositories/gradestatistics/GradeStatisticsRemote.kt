package io.github.wulkanowy.data.repositories.gradestatistics

import io.github.wulkanowy.data.db.entities.GradePointsStatistics
import io.github.wulkanowy.data.db.entities.GradeStatistics
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.sdk.Sdk
import io.github.wulkanowy.utils.init
import io.reactivex.Single
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GradeStatisticsRemote @Inject constructor(private val sdk: Sdk) {

    fun getGradeStatistics(student: Student, semester: Semester, isSemester: Boolean): Single<List<GradeStatistics>> {
        return sdk.init(student).switchDiary(semester.diaryId, semester.schoolYear).let {
            if (isSemester) it.getGradesAnnualStatistics(semester.semesterId)
            else it.getGradesPartialStatistics(semester.semesterId)
        }.map { gradeStatistics ->
            gradeStatistics.map {
                GradeStatistics(
                    semesterId = semester.semesterId,
                    studentId = semester.studentId,
                    subject = it.subject,
                    grade = it.gradeValue,
                    amount = it.amount,
                    semester = isSemester
                )
            }
        }
    }

    fun getGradePointsStatistics(student: Student, semester: Semester): Single<List<GradePointsStatistics>> {
        return sdk.init(student).switchDiary(semester.diaryId, semester.schoolYear)
            .getGradesPointsStatistics(semester.semesterId)
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
