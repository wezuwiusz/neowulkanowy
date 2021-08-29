package io.github.wulkanowy.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.time.LocalDateTime

@Entity(tableName = "Conferences")
data class Conference(

    @ColumnInfo(name = "student_id")
    val studentId: Int,

    @ColumnInfo(name = "diary_id")
    val diaryId: Int,

    val title: String,

    val subject: String,

    val agenda: String,

    @ColumnInfo(name = "present_on_conference")
    val presentOnConference: String,

    @ColumnInfo(name = "conference_id")
    val conferenceId: Int,

    val date: LocalDateTime
) : Serializable {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    @ColumnInfo(name = "is_notified")
    var isNotified: Boolean = true
}
