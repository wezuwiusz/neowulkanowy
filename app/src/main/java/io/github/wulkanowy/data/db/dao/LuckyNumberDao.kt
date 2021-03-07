package io.github.wulkanowy.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import io.github.wulkanowy.data.db.entities.LuckyNumber
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import javax.inject.Singleton

@Singleton
@Dao
interface LuckyNumberDao : BaseDao<LuckyNumber> {

    @Query("SELECT * FROM LuckyNumbers WHERE student_id = :studentId AND date = :date")
    fun load(studentId: Int, date: LocalDate): Flow<LuckyNumber?>

    @Query("SELECT * FROM LuckyNumbers WHERE student_id = :studentId AND date >= :start AND date <= :end")
    fun getAll(studentId: Int, start: LocalDate, end: LocalDate): Flow<List<LuckyNumber>>
}
