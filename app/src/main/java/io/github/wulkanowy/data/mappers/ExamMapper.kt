package io.github.wulkanowy.data.mappers

import io.github.wulkanowy.data.db.entities.Exam
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.sdk.pojo.Exam as SdkExam

fun List<SdkExam>.mapToEntities(semester: Semester) = map {
    Exam(
        studentId = semester.studentId,
        diaryId = semester.diaryId,
        date = it.date,
        entryDate = it.entryDate,
        subject = it.subject,
        group = it.group,
        type = it.type,
        description = it.description,
        teacher = it.teacher,
        teacherSymbol = it.teacherSymbol
    )
}
