package io.github.wulkanowy.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import io.github.wulkanowy.data.db.entities.ReportingUnit
import javax.inject.Singleton

@Singleton
@Dao
interface ReportingUnitDao : BaseDao<ReportingUnit> {

    @Query("SELECT * FROM ReportingUnits WHERE student_id = :studentId")
    suspend fun load(studentId: Int): List<ReportingUnit>

    @Query("SELECT * FROM ReportingUnits WHERE student_id = :studentId AND real_id = :unitId")
    suspend fun loadOne(studentId: Int, unitId: Int): ReportingUnit?
}
