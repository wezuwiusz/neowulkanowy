package io.github.wulkanowy.data.db.entities

import androidx.room.Embedded
import androidx.room.Relation
import java.io.Serializable

data class StudentWithSemesters(
    @Embedded
    val student: Student,

    @Relation(parentColumn = "student_id", entityColumn = "student_id")
    val semesters: List<Semester>
) : Serializable
