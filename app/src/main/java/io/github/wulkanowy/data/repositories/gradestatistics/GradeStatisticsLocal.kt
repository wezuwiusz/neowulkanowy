package io.github.wulkanowy.data.repositories.gradestatistics

import io.github.wulkanowy.data.db.dao.GradePointsStatisticsDao
import io.github.wulkanowy.data.db.dao.GradeStatisticsDao
import io.github.wulkanowy.data.db.entities.GradePointsStatistics
import io.github.wulkanowy.data.db.entities.GradeStatistics
import io.github.wulkanowy.data.db.entities.Semester
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GradeStatisticsLocal @Inject constructor(
    private val gradeStatisticsDb: GradeStatisticsDao,
    private val gradePointsStatisticsDb: GradePointsStatisticsDao
) {

    suspend fun getGradesStatistics(semester: Semester, isSemester: Boolean): List<GradeStatistics> {
        return gradeStatisticsDb.loadAll(semester.semesterId, semester.studentId, isSemester)
    }

    suspend fun getGradesPointsStatistics(semester: Semester): List<GradePointsStatistics> {
        return gradePointsStatisticsDb.loadAll(semester.semesterId, semester.studentId)
    }

    suspend fun getGradesStatistics(semester: Semester, isSemester: Boolean, subjectName: String): List<GradeStatistics> {
        return when (subjectName) {
            "Wszystkie" -> {
                val statistics = gradeStatisticsDb.loadAll(semester.semesterId, semester.studentId, isSemester)
                statistics.groupBy { it.grade }.map {
                    GradeStatistics(semester.studentId, semester.semesterId, subjectName, it.key,
                        it.value.fold(0) { acc, e -> acc + e.amount }, false)
                } + statistics
            }
            else -> gradeStatisticsDb.loadSubject(semester.semesterId, semester.studentId, subjectName, isSemester)
        }
    }

    suspend fun getGradesPointsStatistics(semester: Semester, subjectName: String): List<GradePointsStatistics> {
        return when (subjectName) {
            "Wszystkie" -> gradePointsStatisticsDb.loadAll(semester.semesterId, semester.studentId)
            else -> gradePointsStatisticsDb.loadSubject(semester.semesterId, semester.studentId, subjectName)
        }
    }

    suspend fun saveGradesStatistics(gradesStatistics: List<GradeStatistics>) {
        gradeStatisticsDb.insertAll(gradesStatistics)
    }

    suspend fun saveGradesPointsStatistics(gradePointsStatistics: List<GradePointsStatistics>) {
        gradePointsStatisticsDb.insertAll(gradePointsStatistics)
    }

    suspend fun deleteGradesStatistics(gradesStatistics: List<GradeStatistics>) {
        gradeStatisticsDb.deleteAll(gradesStatistics)
    }

    suspend fun deleteGradesPointsStatistics(gradesPointsStatistics: List<GradePointsStatistics>) {
        gradePointsStatisticsDb.deleteAll(gradesPointsStatistics)
    }
}
