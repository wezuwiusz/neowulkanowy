package io.github.wulkanowy.data.db.entities

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "Students")
data class Student(

        @PrimaryKey(autoGenerate = true)
        var id: Long = 0,

        var endpoint: String,

        var loginType: String,

        var email: String,

        var password: String,

        var symbol: String = "",

        @ColumnInfo(name = "student_id")
        var studentId: Int = 0,

        @ColumnInfo(name = "student_name")
        var studentName: String = "",

        @ColumnInfo(name = "school_id")
        var schoolSymbol: String = "",

        @ColumnInfo(name = "school_name")
        var schoolName: String = ""
)
