package io.github.wulkanowy.data.repositories.local

import io.github.wulkanowy.data.db.dao.GradeSummaryDao
import io.github.wulkanowy.data.db.entities.GradeSummary
import io.github.wulkanowy.data.db.entities.Semester
import io.reactivex.Maybe
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GradeSummaryLocal @Inject constructor(private val gradeSummaryDb: GradeSummaryDao) {

    fun getGradesSummary(semester: Semester): Maybe<List<GradeSummary>> {
        return gradeSummaryDb.getGradesSummary(semester.semesterId, semester.studentId)
                .filter { !it.isEmpty() }
    }

    fun saveGradesSummary(gradesSummary: List<GradeSummary>) {
        gradeSummaryDb.insertAll(gradesSummary)
    }
}
