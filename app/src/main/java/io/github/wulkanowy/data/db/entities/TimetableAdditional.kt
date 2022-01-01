package io.github.wulkanowy.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.time.Instant
import java.time.LocalDate
import java.util.*

@Entity(tableName = "TimetableAdditional")
data class TimetableAdditional(

    @ColumnInfo(name = "student_id")
    val studentId: Int,

    @ColumnInfo(name = "diary_id")
    val diaryId: Int,

    val start: Instant,

    val end: Instant,

    val date: LocalDate,

    val subject: String,
) : Serializable {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    @ColumnInfo(name = "repeat_id", defaultValue = "NULL")
    var repeatId: UUID? = null

    @ColumnInfo(name = "is_added_by_user", defaultValue = "0")
    var isAddedByUser: Boolean = false
}
