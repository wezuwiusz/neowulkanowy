package io.github.wulkanowy.data.mappers

import io.github.wulkanowy.data.db.entities.Message
import io.github.wulkanowy.data.db.entities.MessageAttachment
import io.github.wulkanowy.data.db.entities.Recipient
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.sdk.pojo.Recipient as SdkRecipient
import io.github.wulkanowy.sdk.pojo.MessageAttachment as SdkMessageAttachment
import java.time.LocalDateTime
import io.github.wulkanowy.sdk.pojo.Message as SdkMessage

fun List<SdkMessage>.mapToEntities(student: Student) = map {
    Message(
        studentId = student.id.toInt(),
        realId = it.id ?: 0,
        messageId = it.messageId ?: 0,
        sender = it.sender?.name.orEmpty(),
        senderId = it.sender?.loginId ?: 0,
        recipient = it.recipients.singleOrNull()?.name ?: "Wielu adresatów",
        subject = it.subject.trim(),
        date = it.date ?: LocalDateTime.now(),
        folderId = it.folderId,
        unread = it.unread ?: false,
        removed = it.removed,
        hasAttachments = it.hasAttachments
    ).apply {
        content = it.content.orEmpty()
        unreadBy = it.unreadBy ?: 0
        readBy = it.readBy ?: 0
    }
}

fun List<SdkMessageAttachment>.mapToEntities() = map {
    MessageAttachment(
        realId = it.id,
        messageId = it.messageId,
        oneDriveId = it.oneDriveId,
        url = it.url,
        filename = it.filename
    )
}

fun List<Recipient>.mapFromEntities() = map {
    SdkRecipient(
        id = it.realId,
        name = it.realName,
        loginId = it.loginId,
        reportingUnitId = it.unitId,
        role = it.role,
        hash = it.hash,
        shortName = it.name
    )
}
