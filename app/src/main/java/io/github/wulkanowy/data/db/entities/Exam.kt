package io.github.wulkanowy.data.db.entities

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import org.threeten.bp.LocalDate
import java.io.Serializable

@Entity(tableName = "Exams")
data class Exam(

        @ColumnInfo(name = "student_id")
        var studentId: Int,

        @ColumnInfo(name = "diary_id")
        var diaryId: Int,

        var date: LocalDate,

        @ColumnInfo(name = "entry_date")
        var entryDate: LocalDate = LocalDate.now(),

        var subject: String,

        var group: String,

        var type: String,

        var description: String,

        var teacher: String,

        @ColumnInfo(name = "teacher_symbol")
        var teacherSymbol: String
) : Serializable {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}
