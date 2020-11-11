package io.github.wulkanowy.data.repositories.gradestatistics

import io.github.wulkanowy.data.db.entities.GradePartialStatistics
import io.github.wulkanowy.data.db.entities.GradePointsStatistics
import io.github.wulkanowy.data.db.entities.GradeSemesterStatistics
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.sdk.Sdk
import io.github.wulkanowy.utils.init
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GradeStatisticsRemote @Inject constructor(private val sdk: Sdk) {

    suspend fun getGradePartialStatistics(student: Student, semester: Semester): List<GradePartialStatistics> {
        return sdk.init(student).switchDiary(semester.diaryId, semester.schoolYear)
            .getGradesPartialStatistics(semester.semesterId)
            .map {
                GradePartialStatistics(
                    semesterId = semester.semesterId,
                    studentId = student.studentId,
                    subject = it.subject,
                    classAverage = it.classAverage,
                    studentAverage = it.studentAverage,
                    classAmounts = it.classItems
                        .sortedBy { item -> item.grade }
                        .map { item -> item.amount },
                    studentAmounts = it.studentItems.map { item -> item.amount }
                )
            }
    }

    suspend fun getGradeSemesterStatistics(student: Student, semester: Semester): List<GradeSemesterStatistics> {
        return sdk.init(student).switchDiary(semester.diaryId, semester.schoolYear)
            .getGradesSemesterStatistics(semester.semesterId)
            .map {
                GradeSemesterStatistics(
                    semesterId = semester.semesterId,
                    studentId = semester.studentId,
                    subject = it.subject,
                    amounts = it.items
                        .sortedBy { item -> item.grade }
                        .map { item -> item.amount },
                    studentGrade = it.items.singleOrNull { item -> item.isStudentHere }?.grade ?: 0
                )
            }
    }

    suspend fun getGradePointsStatistics(student: Student, semester: Semester): List<GradePointsStatistics> {
        return sdk.init(student).switchDiary(semester.diaryId, semester.schoolYear)
            .getGradesPointsStatistics(semester.semesterId)
            .map {
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
