package io.github.wulkanowy.data.db.entities

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.io.Serializable
import java.util.*

@Entity(tableName = "Exams")
data class Exam(

        @PrimaryKey(autoGenerate = true)
        var id: Long = 0,

        @ColumnInfo(name = "student_id")
        var studentId: String = "",

        @ColumnInfo(name = "diary_id")
        var diaryId: String = "",

        var date: Date,

        @ColumnInfo(name = "entry_date")
        var entryDate: Date = Date(),

        var subject: String = "",

        var group: String = "",

        var type: String = "",

        var description: String = "",

        var teacher: String = "",

        @ColumnInfo(name = "teacher_symbol")
        var teacherSymbol: String = ""
) : Serializable
