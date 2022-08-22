package io.github.wulkanowy.data.mappers

import io.github.wulkanowy.data.db.entities.Mailbox
import io.github.wulkanowy.data.db.entities.MailboxType
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.sdk.pojo.Mailbox as SdkMailbox

fun List<SdkMailbox>.mapToEntities(student: Student) = map {
    Mailbox(
        globalKey = it.globalKey,
        fullName = it.fullName,
        userName = it.userName,
        userLoginId = student.userLoginId,
        studentName = it.studentName,
        schoolNameShort = it.schoolNameShort,
        type = MailboxType.valueOf(it.type.name),
    )
}
