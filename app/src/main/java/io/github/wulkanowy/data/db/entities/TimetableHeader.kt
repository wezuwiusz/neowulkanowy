package io.github.wulkanowy.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.time.LocalDate

@Entity(tableName = "TimetableHeaders")
data class TimetableHeader(

    @ColumnInfo(name = "student_id")
    val studentId: Int,

    @ColumnInfo(name = "diary_id")
    val diaryId: Int,

    val date: LocalDate,

    val content: String,
) : Serializable {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}
