package io.github.wulkanowy.data.db.entities

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "Semesters",
        indices = [Index(value = ["diary_id", "semester_id"], unique = true)])
data class Semester(

        @PrimaryKey
        var id: Long = 0,

        @ColumnInfo(name = "diary_id")
        var diaryId: String,

        @ColumnInfo(name = "diary_name")
        var diaryName: String = "",

        @ColumnInfo(name = "semester_id")
        var semesterId: String,

        @ColumnInfo(name = "semester_name")
        var semesterName: String = ""
)
