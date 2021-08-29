package io.github.wulkanowy.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import io.github.wulkanowy.data.db.entities.Conference
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime
import javax.inject.Singleton

@Dao
@Singleton
interface ConferenceDao : BaseDao<Conference> {

    @Query("SELECT * FROM Conferences WHERE diary_id = :diaryId AND student_id = :studentId AND date >= :startDate")
    fun loadAll(diaryId: Int, studentId: Int, startDate: LocalDateTime): Flow<List<Conference>>
}
