package io.github.wulkanowy.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.time.LocalDate
import java.time.LocalDateTime

@Entity(tableName = "TimetableAdditional")
data class TimetableAdditional(

    @ColumnInfo(name = "student_id")
    val studentId: Int,

    @ColumnInfo(name = "diary_id")
    val diaryId: Int,

    val start: LocalDateTime,

    val end: LocalDateTime,

    val date: LocalDate,

    val subject: String,
) : Serializable {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}
