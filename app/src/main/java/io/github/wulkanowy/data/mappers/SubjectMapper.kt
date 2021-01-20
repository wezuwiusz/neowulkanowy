package io.github.wulkanowy.data.mappers

import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Subject
import io.github.wulkanowy.sdk.pojo.Subject as SdkSubject

fun List<SdkSubject>.mapToEntities(semester: Semester) = map {
    Subject(
        studentId = semester.studentId,
        diaryId = semester.diaryId,
        name = it.name,
        realId = it.id
    )
}
