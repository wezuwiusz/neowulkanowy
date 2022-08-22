package io.github.wulkanowy.data.mappers

import io.github.wulkanowy.data.db.entities.MailboxType
import io.github.wulkanowy.data.db.entities.Recipient
import io.github.wulkanowy.sdk.pojo.Recipient as SdkRecipient

fun List<SdkRecipient>.mapToEntities(studentMailboxGlobalKey: String) = map {
    Recipient(
        mailboxGlobalKey = it.mailboxGlobalKey,
        fullName = it.fullName,
        userName = it.userName,
        studentMailboxGlobalKey = studentMailboxGlobalKey,
        schoolShortName = it.schoolNameShort,
        type = MailboxType.valueOf(it.type.name),
    )
}
