package io.github.wulkanowy.data.repositories.grade

import io.github.wulkanowy.data.db.dao.GradeDao
import io.github.wulkanowy.data.db.dao.GradeSummaryDao
import io.github.wulkanowy.data.db.entities.Grade
import io.github.wulkanowy.data.db.entities.GradeSummary
import io.github.wulkanowy.data.db.entities.Semester
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GradeLocal @Inject constructor(
    private val gradeDb: GradeDao,
    private val gradeSummaryDb: GradeSummaryDao
) {

    suspend fun saveGrades(grades: List<Grade>) {
        gradeDb.insertAll(grades)
    }

    suspend fun deleteGrades(grades: List<Grade>) {
        gradeDb.deleteAll(grades)
    }

    suspend fun updateGrades(grades: List<Grade>) {
        gradeDb.updateAll(grades)
    }

    suspend fun updateGradesSummary(gradesSummary: List<GradeSummary>) {
        gradeSummaryDb.updateAll(gradesSummary)
    }

    suspend fun getGradesDetails(semester: Semester): List<Grade> {
        return gradeDb.loadAll(semester.semesterId, semester.studentId)
    }

    suspend fun saveGradesSummary(gradesSummary: List<GradeSummary>) {
        gradeSummaryDb.insertAll(gradesSummary)
    }

    suspend fun deleteGradesSummary(gradesSummary: List<GradeSummary>) {
        gradeSummaryDb.deleteAll(gradesSummary)
    }

    suspend fun getGradesSummary(semester: Semester): List<GradeSummary> {
        return gradeSummaryDb.loadAll(semester.semesterId, semester.studentId)
    }
}
