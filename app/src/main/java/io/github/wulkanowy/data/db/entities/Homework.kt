package io.github.wulkanowy.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.threeten.bp.LocalDate
import java.io.Serializable

@Entity(tableName = "Homework")
data class Homework(

    @ColumnInfo(name = "semester_id")
    val semesterId: Int,

    @ColumnInfo(name = "student_id")
    val studentId: Int,

    val date: LocalDate,

    @ColumnInfo(name = "entry_date")
    val entryDate: LocalDate,

    val subject: String,

    val content: String,

    val teacher: String,

    @ColumnInfo(name = "teacher_symbol")
    val teacherSymbol: String

) : Serializable {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}
