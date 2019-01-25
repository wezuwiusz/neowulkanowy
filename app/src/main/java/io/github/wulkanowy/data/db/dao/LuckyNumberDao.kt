package io.github.wulkanowy.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import io.github.wulkanowy.data.db.entities.LuckyNumber
import io.reactivex.Maybe
import org.threeten.bp.LocalDate
import javax.inject.Singleton

@Singleton
@Dao
interface LuckyNumberDao {

    @Insert
    fun insert(luckyNumber: LuckyNumber)

    @Update
    fun update(luckyNumber: LuckyNumber)

    @Delete
    fun delete(luckyNumber: LuckyNumber)

    @Query("SELECT * FROM LuckyNumbers WHERE student_id = :studentId AND date = :date")
    fun loadFromDate(studentId: Int, date: LocalDate): Maybe<LuckyNumber>

}
