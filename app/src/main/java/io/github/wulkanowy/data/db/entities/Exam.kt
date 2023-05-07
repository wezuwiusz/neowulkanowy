package io.github.wulkanowy.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.time.LocalDate

@Entity(tableName = "Exams")
data class Exam(

    @ColumnInfo(name = "student_id")
    val studentId: Int,

    @ColumnInfo(name = "diary_id")
    val diaryId: Int,

    val date: LocalDate,

    @ColumnInfo(name = "entry_date")
    val entryDate: LocalDate,

    val subject: String,

    @Deprecated("not available anymore")
    val group: String,

    val type: String,

    val description: String,

    val teacher: String,

    @ColumnInfo(name = "teacher_symbol")
    val teacherSymbol: String
) : Serializable {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    @ColumnInfo(name = "is_notified")
    var isNotified: Boolean = true
}
