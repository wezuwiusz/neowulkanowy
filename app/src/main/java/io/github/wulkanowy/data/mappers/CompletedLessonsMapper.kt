package io.github.wulkanowy.data.mappers

import io.github.wulkanowy.data.db.entities.CompletedLesson
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.sdk.pojo.CompletedLesson as SdkCompletedLesson

fun List<SdkCompletedLesson>.mapToEntities(semester: Semester) = map {
    CompletedLesson(
        studentId = semester.studentId,
        diaryId = semester.diaryId,
        date = it.date,
        number = it.number,
        subject = it.subject,
        topic = it.topic,
        teacher = it.teacher,
        teacherSymbol = it.teacherSymbol,
        substitution = it.substitution,
        absence = it.absence,
        resources = it.resources
    )
}
