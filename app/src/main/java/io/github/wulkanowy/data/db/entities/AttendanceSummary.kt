package io.github.wulkanowy.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.time.Month

@Entity(tableName = "AttendanceSummary")
data class AttendanceSummary(

    @ColumnInfo(name = "student_id")
    val studentId: Int,

    @ColumnInfo(name = "diary_id")
    val diaryId: Int,

    @ColumnInfo(name = "subject_id")
    val subjectId: Int,

    val month: Month,

    val presence: Int,

    val absence: Int,

    @ColumnInfo(name = "absence_excused")
    val absenceExcused: Int,

    @ColumnInfo(name = "absence_for_school_reasons")
    val absenceForSchoolReasons: Int,

    val lateness: Int,

    @ColumnInfo(name = "lateness_excused")
    val latenessExcused: Int,

    val exemption: Int
) : Serializable {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}
