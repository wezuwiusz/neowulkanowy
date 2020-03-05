package io.github.wulkanowy.data.repositories.gradestatistics

import io.github.wulkanowy.data.db.dao.GradePointsStatisticsDao
import io.github.wulkanowy.data.db.dao.GradeStatisticsDao
import io.github.wulkanowy.data.db.entities.GradePointsStatistics
import io.github.wulkanowy.data.db.entities.GradeStatistics
import io.github.wulkanowy.data.db.entities.Semester
import io.reactivex.Maybe
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GradeStatisticsLocal @Inject constructor(
    private val gradeStatisticsDb: GradeStatisticsDao,
    private val gradePointsStatisticsDb: GradePointsStatisticsDao
) {

    fun getGradesStatistics(semester: Semester, isSemester: Boolean): Maybe<List<GradeStatistics>> {
        return gradeStatisticsDb.loadAll(semester.semesterId, semester.studentId, isSemester).filter { it.isNotEmpty() }
    }

    fun getGradesPointsStatistics(semester: Semester): Maybe<List<GradePointsStatistics>> {
        return gradePointsStatisticsDb.loadAll(semester.semesterId, semester.studentId).filter { it.isNotEmpty() }
    }

    fun getGradesStatistics(semester: Semester, isSemester: Boolean, subjectName: String): Maybe<List<GradeStatistics>> {
        return when (subjectName) {
            "Wszystkie" -> gradeStatisticsDb.loadAll(semester.semesterId, semester.studentId, isSemester).map { list ->
                list.groupBy { it.grade }.map {
                    GradeStatistics(semester.studentId, semester.semesterId, subjectName, it.key,
                        it.value.fold(0) { acc, e -> acc + e.amount }, false)
                } + list
            }
            else -> gradeStatisticsDb.loadSubject(semester.semesterId, semester.studentId, subjectName, isSemester)
        }.filter { it.isNotEmpty() }
    }

    fun getGradesPointsStatistics(semester: Semester, subjectName: String): Maybe<List<GradePointsStatistics>> {
        return when (subjectName) {
            "Wszystkie" -> gradePointsStatisticsDb.loadAll(semester.semesterId, semester.studentId)
            else -> gradePointsStatisticsDb.loadSubject(semester.semesterId, semester.studentId, subjectName)
        }.filter { it.isNotEmpty() }
    }

    fun saveGradesStatistics(gradesStatistics: List<GradeStatistics>) {
        gradeStatisticsDb.insertAll(gradesStatistics)
    }

    fun saveGradesPointsStatistics(gradePointsStatistics: List<GradePointsStatistics>) {
        gradePointsStatisticsDb.insertAll(gradePointsStatistics)
    }

    fun deleteGradesStatistics(gradesStatistics: List<GradeStatistics>) {
        gradeStatisticsDb.deleteAll(gradesStatistics)
    }

    fun deleteGradesPointsStatistics(gradesPointsStatistics: List<GradePointsStatistics>) {
        gradePointsStatisticsDb.deleteAll(gradesPointsStatistics)
    }
}
