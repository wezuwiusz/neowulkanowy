package io.github.wulkanowy.data.mappers

import io.github.wulkanowy.data.db.entities.*
import io.github.wulkanowy.sdk.pojo.MailboxType
import timber.log.Timber
import io.github.wulkanowy.sdk.pojo.Message as SdkMessage
import io.github.wulkanowy.sdk.pojo.MessageAttachment as SdkMessageAttachment
import io.github.wulkanowy.sdk.pojo.Recipient as SdkRecipient

fun List<SdkMessage>.mapToEntities(
    student: Student,
    mailbox: Mailbox?,
    allMailboxes: List<Mailbox>
): List<Message> = map {
    Message(
        messageGlobalKey = it.globalKey,
        mailboxKey = mailbox?.globalKey ?: allMailboxes.find { box ->
            box.fullName == it.mailbox
        }?.globalKey.let { mailboxKey ->
            if (mailboxKey == null) {
                Timber.e("Can't find ${it.mailbox} in $allMailboxes")
                "unknown"
            } else mailboxKey
        },
        email = student.email,
        messageId = it.id,
        correspondents = it.correspondents,
        subject = it.subject.trim(),
        date = it.dateZoned.toInstant(),
        folderId = it.folderId,
        unread = it.unread,
        unreadBy = it.unreadBy,
        readBy = it.readBy,
        hasAttachments = it.hasAttachments,
    ).apply {
        content = it.content.orEmpty()
    }
}

fun List<SdkMessageAttachment>.mapToEntities(messageGlobalKey: String) = map {
    MessageAttachment(
        messageGlobalKey = messageGlobalKey,
        realId = it.url.hashCode(),
        url = it.url,
        filename = it.filename
    )
}

fun List<Recipient>.mapFromEntities() = map {
    SdkRecipient(
        fullName = it.fullName,
        userName = it.userName,
        studentName = it.userName,
        mailboxGlobalKey = it.mailboxGlobalKey,
        schoolNameShort = it.schoolShortName,
        type = MailboxType.valueOf(it.type.name),
    )
}
