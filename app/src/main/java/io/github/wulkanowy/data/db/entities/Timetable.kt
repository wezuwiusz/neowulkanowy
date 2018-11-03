package io.github.wulkanowy.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import java.io.Serializable

@Entity(tableName = "Timetable")
data class Timetable(

        @ColumnInfo(name = "student_id")
        var studentId: Int,

        @ColumnInfo(name = "diary_id")
        var diaryId: Int,

        val number: Int = 0,

        val start: LocalDateTime = LocalDateTime.now(),

        val end: LocalDateTime = LocalDateTime.now(),

        val date: LocalDate,

        val subject: String,

        val group: String,

        val room: String,

        val teacher: String,

        val info: String,

        val changes: Boolean = false,

        val canceled: Boolean = false
) : Serializable {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}
