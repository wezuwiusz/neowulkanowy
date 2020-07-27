package io.github.wulkanowy.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.time.LocalDate

@Entity(tableName = "CompletedLesson")
data class CompletedLesson(

    @ColumnInfo(name = "student_id")
    val studentId: Int,

    @ColumnInfo(name = "diary_id")
    val diaryId: Int,

    val date: LocalDate,

    val number: Int,

    val subject: String,

    val topic: String,

    val teacher: String,

    @ColumnInfo(name = "teacher_symbol")
    val teacherSymbol: String,

    val substitution: String,

    val absence: String,

    val resources: String
) : Serializable {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}
