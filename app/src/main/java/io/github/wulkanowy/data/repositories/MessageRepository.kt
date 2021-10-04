package io.github.wulkanowy.data.repositories

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.wulkanowy.R
import io.github.wulkanowy.data.Resource
import io.github.wulkanowy.data.db.SharedPrefProvider
import io.github.wulkanowy.data.db.dao.MessageAttachmentDao
import io.github.wulkanowy.data.db.dao.MessagesDao
import io.github.wulkanowy.data.db.entities.Message
import io.github.wulkanowy.data.db.entities.MessageWithAttachment
import io.github.wulkanowy.data.db.entities.Recipient
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.enums.MessageFolder
import io.github.wulkanowy.data.enums.MessageFolder.RECEIVED
import io.github.wulkanowy.data.mappers.mapFromEntities
import io.github.wulkanowy.data.mappers.mapToEntities
import io.github.wulkanowy.data.pojos.MessageDraft
import io.github.wulkanowy.sdk.Sdk
import io.github.wulkanowy.sdk.pojo.Folder
import io.github.wulkanowy.sdk.pojo.SentMessage
import io.github.wulkanowy.utils.AutoRefreshHelper
import io.github.wulkanowy.utils.getRefreshKey
import io.github.wulkanowy.utils.init
import io.github.wulkanowy.utils.networkBoundResource
import io.github.wulkanowy.utils.uniqueSubtract
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import timber.log.Timber
import java.time.LocalDateTime.now
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessageRepository @Inject constructor(
    private val messagesDb: MessagesDao,
    private val messageAttachmentDao: MessageAttachmentDao,
    private val sdk: Sdk,
    @ApplicationContext private val context: Context,
    private val refreshHelper: AutoRefreshHelper,
    private val sharedPrefProvider: SharedPrefProvider,
    private val json: Json,
) {

    private val saveFetchResultMutex = Mutex()

    private val cacheKey = "message"

    @Suppress("UNUSED_PARAMETER")
    fun getMessages(
        student: Student,
        semester: Semester,
        folder: MessageFolder,
        forceRefresh: Boolean,
        notify: Boolean = false,
    ): Flow<Resource<List<Message>>> = networkBoundResource(
        mutex = saveFetchResultMutex,
        shouldFetch = {
            val isExpired = refreshHelper.shouldBeRefreshed(
                key = getRefreshKey(cacheKey, student, folder)
            )
            it.isEmpty() || forceRefresh || isExpired
        },
        query = { messagesDb.loadAll(student.id.toInt(), folder.id) },
        fetch = {
            sdk.init(student).getMessages(Folder.valueOf(folder.name), now().minusMonths(3), now())
                .mapToEntities(student)
        },
        saveFetchResult = { old, new ->
            messagesDb.deleteAll(old uniqueSubtract new)
            messagesDb.insertAll((new uniqueSubtract old).onEach {
                it.isNotified = !notify
            })
            messagesDb.updateAll(getMessagesWithReadByChange(old, new, !notify))

            refreshHelper.updateLastRefreshTimestamp(getRefreshKey(cacheKey, student, folder))
        }
    )

    private fun getMessagesWithReadByChange(
        old: List<Message>,
        new: List<Message>,
        setNotified: Boolean
    ): List<Message> {
        val oldMeta = old.map { Triple(it, it.readBy, it.unreadBy) }
        val newMeta = new.map { Triple(it, it.readBy, it.unreadBy) }

        val updatedItems = newMeta uniqueSubtract oldMeta

        return updatedItems.map {
            val oldItem = old.find { item -> item.messageId == it.first.messageId }
            it.first.apply {
                id = oldItem?.id ?: 0
                isNotified = oldItem?.isNotified ?: setNotified
                content = oldItem?.content.orEmpty()
            }
        }
    }

    fun getMessage(
        student: Student,
        message: Message,
        markAsRead: Boolean = false,
    ): Flow<Resource<MessageWithAttachment?>> = networkBoundResource(
        shouldFetch = {
            checkNotNull(it, { "This message no longer exist!" })
            Timber.d("Message content in db empty: ${it.message.content.isEmpty()}")
            it.message.unread || it.message.content.isEmpty()
        },
        query = { messagesDb.loadMessageWithAttachment(student.id.toInt(), message.messageId) },
        fetch = {
            sdk.init(student).getMessageDetails(
                messageId = it!!.message.messageId,
                folderId = message.folderId,
                read = markAsRead,
                id = message.realId
            ).let { details ->
                details.content to details.attachments.mapToEntities()
            }
        },
        saveFetchResult = { old, (downloadedMessage, attachments) ->
            checkNotNull(old, { "Fetched message no longer exist!" })
            messagesDb.updateAll(listOf(old.message.apply {
                id = old.message.id
                unread = !markAsRead
                content = content.ifBlank { downloadedMessage }
            }))
            messageAttachmentDao.insertAttachments(attachments)
            Timber.d("Message ${message.messageId} with blank content: ${old.message.content.isBlank()}, marked as read")
        }
    )

    fun getMessagesFromDatabase(student: Student): Flow<List<Message>> {
        return messagesDb.loadAll(student.id.toInt(), RECEIVED.id)
    }

    suspend fun updateMessages(messages: List<Message>) {
        return messagesDb.updateAll(messages)
    }

    suspend fun sendMessage(
        student: Student,
        subject: String,
        content: String,
        recipients: List<Recipient>,
    ): SentMessage = sdk.init(student).sendMessage(
        subject = subject,
        content = content,
        recipients = recipients.mapFromEntities()
    )

    suspend fun deleteMessage(student: Student, message: Message) {
        val isDeleted = sdk.init(student).deleteMessages(
            messages = listOf(message.messageId), message.folderId
        )

        if (message.folderId != MessageFolder.TRASHED.id && isDeleted) {
            val deletedMessage = message.copy(folderId = MessageFolder.TRASHED.id).apply {
                id = message.id
                content = message.content
            }
            messagesDb.updateAll(listOf(deletedMessage))
        } else messagesDb.deleteAll(listOf(message))
    }

    var draftMessage: MessageDraft?
        get() = sharedPrefProvider.getString(context.getString(R.string.pref_key_message_send_draft))
            ?.let { json.decodeFromString(it) }
        set(value) = sharedPrefProvider.putString(
            context.getString(R.string.pref_key_message_send_draft),
            value?.let { json.encodeToString(it) }
        )
}
