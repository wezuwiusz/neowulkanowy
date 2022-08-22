package io.github.wulkanowy.data.repositories

import io.github.wulkanowy.data.db.dao.RecipientDao
import io.github.wulkanowy.data.db.entities.*
import io.github.wulkanowy.data.mappers.mapToEntities
import io.github.wulkanowy.sdk.Sdk
import io.github.wulkanowy.utils.AutoRefreshHelper
import io.github.wulkanowy.utils.getRefreshKey
import io.github.wulkanowy.utils.init
import io.github.wulkanowy.utils.uniqueSubtract
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecipientRepository @Inject constructor(
    private val recipientDb: RecipientDao,
    private val sdk: Sdk,
    private val refreshHelper: AutoRefreshHelper,
) {

    private val cacheKey = "recipient"

    suspend fun refreshRecipients(student: Student, mailbox: Mailbox, type: MailboxType) {
        val new = sdk.init(student).getRecipients(mailbox.globalKey)
            .mapToEntities(mailbox.globalKey)
        val old = recipientDb.loadAll(type, mailbox.globalKey)

        recipientDb.deleteAll(old uniqueSubtract new)
        recipientDb.insertAll(new uniqueSubtract old)

        refreshHelper.updateLastRefreshTimestamp(getRefreshKey(cacheKey, student))
    }

    suspend fun getRecipients(
        student: Student,
        mailbox: Mailbox,
        type: MailboxType
    ): List<Recipient> {
        val cached = recipientDb.loadAll(type, mailbox.globalKey)

        val isExpired = refreshHelper.shouldBeRefreshed(getRefreshKey(cacheKey, student))
        return if (cached.isEmpty() || isExpired) {
            refreshRecipients(student, mailbox, type)
            recipientDb.loadAll(type, mailbox.globalKey)
        } else cached
    }

    suspend fun getMessageSender(
        student: Student,
        mailbox: Mailbox,
        message: Message
    ): List<Recipient> = sdk.init(student)
        .getMessageReplayDetails(message.messageGlobalKey)
        .sender
        .let(::listOf)
        .mapToEntities(mailbox.globalKey)
}
