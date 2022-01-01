package io.github.wulkanowy.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.io.Serializable
import java.time.LocalDate

@Entity(
    tableName = "Semesters", indices = [Index(
        value = ["student_id", "diary_id", "kindergarten_diary_id", "semester_id"],
        unique = true
    )]
)
data class Semester(

    @ColumnInfo(name = "student_id")
    val studentId: Int,

    @ColumnInfo(name = "diary_id")
    val diaryId: Int,

    @ColumnInfo(name = "kindergarten_diary_id", defaultValue = "0")
    val kindergartenDiaryId: Int,

    @ColumnInfo(name = "diary_name")
    val diaryName: String,

    @ColumnInfo(name = "school_year")
    val schoolYear: Int,

    @ColumnInfo(name = "semester_id")
    val semesterId: Int,

    @ColumnInfo(name = "semester_name")
    val semesterName: Int,

    val start: LocalDate,

    val end: LocalDate,

    @ColumnInfo(name = "class_id")
    val classId: Int,

    @ColumnInfo(name = "unit_id")
    val unitId: Int
) : Serializable {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    @ColumnInfo(name = "is_current")
    var current: Boolean = false
}
