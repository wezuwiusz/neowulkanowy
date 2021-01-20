package io.github.wulkanowy.data.mappers

import io.github.wulkanowy.data.db.entities.Conference
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.sdk.pojo.Conference as SdkConference

fun List<SdkConference>.mapToEntities(semester: Semester) = map {
    Conference(
        studentId = semester.studentId,
        diaryId = semester.diaryId,
        agenda = it.agenda,
        conferenceId = it.id,
        date = it.date,
        presentOnConference = it.presentOnConference,
        subject = it.subject,
        title = it.title
    )
}
