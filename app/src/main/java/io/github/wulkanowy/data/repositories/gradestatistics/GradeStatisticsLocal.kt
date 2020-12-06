package io.github.wulkanowy.data.repositories.gradestatistics

import io.github.wulkanowy.data.db.dao.GradePartialStatisticsDao
import io.github.wulkanowy.data.db.dao.GradePointsStatisticsDao
import io.github.wulkanowy.data.db.dao.GradeSemesterStatisticsDao
import io.github.wulkanowy.data.db.entities.GradePartialStatistics
import io.github.wulkanowy.data.db.entities.GradePointsStatistics
import io.github.wulkanowy.data.db.entities.GradeSemesterStatistics
import io.github.wulkanowy.data.db.entities.Semester
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GradeStatisticsLocal @Inject constructor(
    private val gradePartialStatisticsDb: GradePartialStatisticsDao,
    private val gradePointsStatisticsDb: GradePointsStatisticsDao,
    private val gradeSemesterStatisticsDb: GradeSemesterStatisticsDao
) {

    // partial
    fun getGradePartialStatistics(semester: Semester): Flow<List<GradePartialStatistics>> {
        return gradePartialStatisticsDb.loadAll(semester.semesterId, semester.studentId)
    }

    suspend fun saveGradePartialStatistics(items: List<GradePartialStatistics>) {
        gradePartialStatisticsDb.insertAll(items)
    }

    suspend fun deleteGradePartialStatistics(items: List<GradePartialStatistics>) {
        gradePartialStatisticsDb.deleteAll(items)
    }

    // points
    fun getGradePointsStatistics(semester: Semester): Flow<List<GradePointsStatistics>> {
        return gradePointsStatisticsDb.loadAll(semester.semesterId, semester.studentId)
    }

    suspend fun saveGradePointsStatistics(gradePointsStatistics: List<GradePointsStatistics>) {
        gradePointsStatisticsDb.insertAll(gradePointsStatistics)
    }

    suspend fun deleteGradePointsStatistics(gradesPointsStatistics: List<GradePointsStatistics>) {
        gradePointsStatisticsDb.deleteAll(gradesPointsStatistics)
    }

    // semester
    fun getGradeSemesterStatistics(semester: Semester): Flow<List<GradeSemesterStatistics>> {
        return gradeSemesterStatisticsDb.loadAll(semester.semesterId, semester.studentId)
    }

    suspend fun saveGradeSemesterStatistics(items: List<GradeSemesterStatistics>) {
        gradeSemesterStatisticsDb.insertAll(items)
    }

    suspend fun deleteGradeSemesterStatistics(items: List<GradeSemesterStatistics>) {
        gradeSemesterStatisticsDb.deleteAll(items)
    }
}
