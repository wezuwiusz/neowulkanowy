package io.github.wulkanowy.data.db.entities

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "Semesters")
data class Semester(

        @PrimaryKey(autoGenerate = true)
        var id: Long = 0,

        @ColumnInfo(name = "student_id")
        var studentId: String,

        @ColumnInfo(name = "diary_id")
        var diaryId: String,

        @ColumnInfo(name = "diary_name")
        var diaryName: String,

        @ColumnInfo(name = "semester_id")
        var semesterId: String,

        @ColumnInfo(name = "semester_name")
        var semesterName: Int,

        @ColumnInfo(name = "is_current")
        var current: Boolean = false
)
