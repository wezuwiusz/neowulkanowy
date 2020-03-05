package io.github.wulkanowy.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import java.io.Serializable

@Entity(tableName = "Timetable")
data class Timetable(

    @ColumnInfo(name = "student_id")
    val studentId: Int,

    @ColumnInfo(name = "diary_id")
    val diaryId: Int,

    val number: Int,

    val start: LocalDateTime,

    val end: LocalDateTime,

    val date: LocalDate,

    val subject: String,

    val subjectOld: String,

    val group: String,

    val room: String,

    val roomOld: String,

    val teacher: String,

    val teacherOld: String,

    val info: String,

    @ColumnInfo(name = "student_plan")
    val isStudentPlan: Boolean,

    val changes: Boolean,

    val canceled: Boolean
) : Serializable {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}
