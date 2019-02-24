package io.github.wulkanowy.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import io.github.wulkanowy.data.db.entities.ReportingUnit
import io.reactivex.Maybe
import javax.inject.Singleton

@Singleton
@Dao
interface ReportingUnitDao {

    @Insert
    fun insertAll(reportingUnits: List<ReportingUnit>)

    @Delete
    fun deleteAll(reportingUnits: List<ReportingUnit>)

    @Query("SELECT * FROM ReportingUnits WHERE student_id = :studentId")
    fun load(studentId: Int): Maybe<List<ReportingUnit>>

    @Query("SELECT * FROM ReportingUnits WHERE student_id = :studentId AND real_id = :unitId")
    fun loadOne(studentId: Int, unitId: Int): Maybe<ReportingUnit>
}
