package io.github.wulkanowy.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.threeten.bp.LocalDate
import java.io.Serializable

@Entity(tableName = "CompletedLesson")
data class CompletedLesson(

    @ColumnInfo(name = "student_id")
    var studentId: Int,

    @ColumnInfo(name = "diary_id")
    var diaryId: Int,

    var date: LocalDate,

    var number: Int,

    var subject: String,

    var topic: String,

    var teacher: String,

    @ColumnInfo(name = "teacher_symbol")
    var teacherSymbol: String,

    var substitution: String,

    var absence: String,

    var resources: String
) : Serializable {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}
