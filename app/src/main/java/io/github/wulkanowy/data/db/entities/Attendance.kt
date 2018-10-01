package io.github.wulkanowy.data.db.entities

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import org.threeten.bp.LocalDate
import java.io.Serializable

@Entity(tableName = "Attendance")
data class Attendance(

        @PrimaryKey(autoGenerate = true)
        var id: Long = 0,

        @ColumnInfo(name = "student_id")
        var studentId: String = "",

        @ColumnInfo(name = "diary_id")
        var diaryId: String = "",

        var date: LocalDate,

        var number: Int = 0,

        var subject: String = "",

        var name: String = "",

        var presence: Boolean = false,

        var absence: Boolean = false,

        var exemption: Boolean = false,

        var lateness: Boolean = false,

        var excused: Boolean = false,

        var deleted: Boolean = false
) : Serializable
