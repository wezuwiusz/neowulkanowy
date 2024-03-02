package io.github.wulkanowy.data.mappers

import io.github.wulkanowy.data.db.entities.SchoolAnnouncement
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.sdk.pojo.DirectorInformation as SdkDirectorInformation
import io.github.wulkanowy.sdk.pojo.LastAnnouncement as SdkLastAnnouncement

@JvmName("mapDirectorInformationToEntities")
fun List<SdkDirectorInformation>.mapToEntities(student: Student) = map {
    SchoolAnnouncement(
        userLoginId = student.userLoginId,
        date = it.date,
        subject = it.subject,
        content = it.content,
        author = null,
    )
}

@JvmName("mapLastAnnouncementsToEntities")
fun List<SdkLastAnnouncement>.mapToEntities(student: Student) = map {
    SchoolAnnouncement(
        userLoginId = student.userLoginId,
        date = it.date,
        subject = it.subject,
        content = it.content,
        author = it.author,
    )
}
