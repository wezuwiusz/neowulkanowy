package io.github.wulkanowy.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.threeten.bp.LocalDate
import java.io.Serializable

@Entity(tableName = "Homework")
data class Homework(

    @ColumnInfo(name = "semester_id")
    var semesterId: Int,

    @ColumnInfo(name = "student_id")
    var studentId: Int,

    var date: LocalDate,

    @ColumnInfo(name = "entry_date")
    var entryDate: LocalDate,

    var subject: String,

    var content: String,

    var teacher: String,

    @ColumnInfo(name = "teacher_symbol")
    var teacherSymbol: String

) : Serializable {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}
