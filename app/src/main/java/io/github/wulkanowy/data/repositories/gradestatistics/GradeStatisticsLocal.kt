package io.github.wulkanowy.data.repositories.gradestatistics

import io.github.wulkanowy.data.db.dao.GradeStatisticsDao
import io.github.wulkanowy.data.db.entities.GradeStatistics
import io.github.wulkanowy.data.db.entities.Semester
import io.reactivex.Maybe
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GradeStatisticsLocal @Inject constructor(private val gradeStatisticsDb: GradeStatisticsDao) {

    fun getGradesStatistics(semester: Semester, isSemester: Boolean): Maybe<List<GradeStatistics>> {
        return gradeStatisticsDb.loadAll(semester.semesterId, semester.studentId, isSemester)
            .filter { !it.isEmpty() }
    }

    fun getGradesStatistics(semester: Semester, isSemester: Boolean, subjectName: String): Maybe<List<GradeStatistics>> {
        return (if ("Wszystkie" == subjectName) gradeStatisticsDb.loadAll(semester.semesterId, semester.studentId, isSemester).map { list ->
            list.groupBy { it.grade }.map {
                GradeStatistics(semester.studentId, semester.semesterId, subjectName, it.key, it.value.fold(0) { acc, e -> acc + e.amount }, false)
            }
        }
        else gradeStatisticsDb.loadSubject(semester.semesterId, semester.studentId, subjectName, isSemester)).filter { !it.isEmpty() }
    }

    fun saveGradesStatistics(gradesStatistics: List<GradeStatistics>) {
        gradeStatisticsDb.insertAll(gradesStatistics)
    }

    fun deleteGradesStatistics(gradesStatistics: List<GradeStatistics>) {
        gradeStatisticsDb.deleteAll(gradesStatistics)
    }
}
