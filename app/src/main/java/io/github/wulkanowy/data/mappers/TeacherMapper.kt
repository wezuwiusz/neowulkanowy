package io.github.wulkanowy.data.mappers

import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Teacher
import io.github.wulkanowy.sdk.pojo.Teacher as SdkTeacher

fun List<SdkTeacher>.mapToEntities(semester: Semester) = map {
    Teacher(
        studentId = semester.studentId,
        name = it.name,
        subject = it.subject,
        shortName = it.short,
        classId = semester.classId
    )
}
