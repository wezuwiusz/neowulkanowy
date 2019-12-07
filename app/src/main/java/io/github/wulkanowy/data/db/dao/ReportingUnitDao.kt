package io.github.wulkanowy.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import io.github.wulkanowy.data.db.entities.ReportingUnit
import io.reactivex.Maybe
import javax.inject.Singleton

@Singleton
@Dao
interface ReportingUnitDao : BaseDao<ReportingUnit> {

    @Query("SELECT * FROM ReportingUnits WHERE student_id = :studentId")
    fun load(studentId: Int): Maybe<List<ReportingUnit>>

    @Query("SELECT * FROM ReportingUnits WHERE student_id = :studentId AND real_id = :unitId")
    fun loadOne(studentId: Int, unitId: Int): Maybe<ReportingUnit>
}
