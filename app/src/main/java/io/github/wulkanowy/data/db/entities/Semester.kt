package io.github.wulkanowy.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "Semesters", indices = [Index(value = ["student_id", "diary_id", "semester_id"], unique = true)])
data class Semester(

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0,

    @ColumnInfo(name = "student_id")
    var studentId: Int,

    @ColumnInfo(name = "diary_id")
    var diaryId: Int,

    @ColumnInfo(name = "diary_name")
    var diaryName: String,

    @ColumnInfo(name = "semester_id")
    var semesterId: Int,

    @ColumnInfo(name = "semester_name")
    var semesterName: Int,

    @ColumnInfo(name = "is_current")
    var isCurrent: Boolean = false
)
