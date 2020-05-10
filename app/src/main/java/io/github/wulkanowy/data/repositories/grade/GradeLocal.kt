package io.github.wulkanowy.data.repositories.grade

import io.github.wulkanowy.data.db.dao.GradeDao
import io.github.wulkanowy.data.db.dao.GradeSummaryDao
import io.github.wulkanowy.data.db.entities.Grade
import io.github.wulkanowy.data.db.entities.GradeSummary
import io.github.wulkanowy.data.db.entities.Semester
import io.reactivex.Maybe
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GradeLocal @Inject constructor(
    private val gradeDb: GradeDao,
    private val gradeSummaryDb: GradeSummaryDao
) {

    fun saveGrades(grades: List<Grade>) {
        gradeDb.insertAll(grades)
    }

    fun deleteGrades(grades: List<Grade>) {
        gradeDb.deleteAll(grades)
    }

    fun updateGrades(grades: List<Grade>) {
        gradeDb.updateAll(grades)
    }

    fun getGradesDetails(semester: Semester): Maybe<List<Grade>> {
        return gradeDb.loadAll(semester.semesterId, semester.studentId).filter { it.isNotEmpty() }
    }

    fun saveGradesSummary(gradesSummary: List<GradeSummary>) {
        gradeSummaryDb.insertAll(gradesSummary)
    }

    fun deleteGradesSummary(gradesSummary: List<GradeSummary>) {
        gradeSummaryDb.deleteAll(gradesSummary)
    }

    fun getGradesSummary(semester: Semester): Maybe<List<GradeSummary>> {
        return gradeSummaryDb.loadAll(semester.semesterId, semester.studentId).filter { it.isNotEmpty() }
    }
}
