package io.github.wulkanowy.data.repositories.gradestatistics

import io.github.wulkanowy.data.db.dao.GradePointsStatisticsDao
import io.github.wulkanowy.data.db.dao.GradeStatisticsDao
import io.github.wulkanowy.data.db.entities.GradePointsStatistics
import io.github.wulkanowy.data.db.entities.GradeStatistics
import io.github.wulkanowy.data.db.entities.Semester
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GradeStatisticsLocal @Inject constructor(
    private val gradeStatisticsDb: GradeStatisticsDao,
    private val gradePointsStatisticsDb: GradePointsStatisticsDao
) {

    fun getGradesStatistics(semester: Semester, isSemester: Boolean): Flow<List<GradeStatistics>> {
        return gradeStatisticsDb.loadAll(semester.semesterId, semester.studentId, isSemester)
    }

    fun getGradesPointsStatistics(semester: Semester): Flow<List<GradePointsStatistics>> {
        return gradePointsStatisticsDb.loadAll(semester.semesterId, semester.studentId)
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
