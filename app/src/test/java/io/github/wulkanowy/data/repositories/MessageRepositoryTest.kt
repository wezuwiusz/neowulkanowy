package io.github.wulkanowy.data.repositories

import android.content.Context
import io.github.wulkanowy.data.Status
import io.github.wulkanowy.data.db.SharedPrefProvider
import io.github.wulkanowy.data.db.dao.MessageAttachmentDao
import io.github.wulkanowy.data.db.dao.MessagesDao
import io.github.wulkanowy.data.db.entities.Message
import io.github.wulkanowy.data.db.entities.MessageWithAttachment
import io.github.wulkanowy.data.enums.MessageFolder
import io.github.wulkanowy.getSemesterEntity
import io.github.wulkanowy.getStudentEntity
import io.github.wulkanowy.sdk.Sdk
import io.github.wulkanowy.sdk.pojo.Folder
import io.github.wulkanowy.sdk.pojo.MessageDetails
import io.github.wulkanowy.sdk.pojo.Sender
import io.github.wulkanowy.utils.AutoRefreshHelper
import io.github.wulkanowy.utils.toFirstResult
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.checkEquals
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.SpyK
import io.mockk.just
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.net.UnknownHostException
import java.time.LocalDateTime
import kotlin.test.assertTrue

class MessageRepositoryTest {

    @SpyK
    private var sdk = Sdk()

    @MockK
    private lateinit var messageDb: MessagesDao

    @MockK
    private lateinit var messageAttachmentDao: MessageAttachmentDao

    @MockK
    private lateinit var context: Context

    @MockK(relaxUnitFun = true)
    private lateinit var refreshHelper: AutoRefreshHelper

    @MockK
    private lateinit var sharedPrefProvider: SharedPrefProvider

    private val student = getStudentEntity()

    private val semester = getSemesterEntity()

    private lateinit var repository: MessageRepository

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        every { refreshHelper.shouldBeRefreshed(any()) } returns false

        repository = MessageRepository(
            messagesDb = messageDb,
            messageAttachmentDao = messageAttachmentDao,
            sdk = sdk,
            context = context,
            refreshHelper = refreshHelper,
            sharedPrefProvider = sharedPrefProvider,
            json = Json,
        )
    }

    @Test
    fun `get messages when read by values was changed on already read message`() = runBlocking {
        every { messageDb.loadAll(any(), any()) } returns flow {
            val dbMessage = getMessageEntity(3, "", false).apply {
                unreadBy = 10
                readBy = 5
                isNotified = true
            }
            emit(listOf(dbMessage))
        }
        coEvery { sdk.getMessages(Folder.RECEIVED, any(), any()) } returns listOf(
            getMessageDto(messageId = 3, content = "", unread = false).copy(
                unreadBy = 5,
                readBy = 10,
            )
        )
        coEvery { messageDb.deleteAll(any()) } just Runs
        coEvery { messageDb.insertAll(any()) } returns listOf()

        repository.getMessages(
            student = student,
            semester = semester,
            folder = MessageFolder.RECEIVED,
            forceRefresh = true,
            notify = true, // all new messages will be marked as not notified
        ).toFirstResult().data.orEmpty()

        coVerify(exactly = 1) { messageDb.deleteAll(emptyList()) }
        coVerify(exactly = 1) { messageDb.insertAll(emptyList()) }
        coVerify(exactly = 1) {
            messageDb.updateAll(withArg {
                assertEquals(1, it.size)
                assertEquals(5, it.single().unreadBy)
                assertEquals(10, it.single().readBy)
            })
        }
    }

    @Test
    fun `get messages when fetched completely new message without notify`() = runBlocking {
        every { messageDb.loadAll(any(), any()) } returns flowOf(emptyList())
        coEvery { sdk.getMessages(Folder.RECEIVED, any(), any()) } returns listOf(
            getMessageDto(messageId = 4, content = "Test", unread = true).copy(
                unreadBy = 5,
                readBy = 10,
            )
        )
        coEvery { messageDb.deleteAll(any()) } just Runs
        coEvery { messageDb.insertAll(any()) } returns listOf()

        repository.getMessages(
            student = student,
            semester = semester,
            folder = MessageFolder.RECEIVED,
            forceRefresh = true,
            notify = false,
        ).toFirstResult().data.orEmpty()

        coVerify(exactly = 1) { messageDb.deleteAll(withArg { checkEquals(emptyList<Message>()) }) }
        coVerify {
            messageDb.insertAll(withArg {
                assertEquals(4, it.single().messageId)
                assertTrue(it.single().isNotified)
            })
        }
    }

    @Test(expected = NoSuchElementException::class)
    fun `throw error when message is not in the db`() {
        val testMessage = getMessageEntity(1, "", false)
        coEvery {
            messageDb.loadMessageWithAttachment(1, 1)
        } throws NoSuchElementException("No message in database")

        runBlocking { repository.getMessage(student, testMessage).toFirstResult() }
    }

    @Test
    fun `get message when content already in db`() {
        val testMessage = getMessageEntity(123, "Test", false)
        val messageWithAttachment = MessageWithAttachment(testMessage, emptyList())

        coEvery { messageDb.loadMessageWithAttachment(1, testMessage.messageId) } returns flowOf(
            messageWithAttachment
        )

        val res = runBlocking { repository.getMessage(student, testMessage).toFirstResult() }

        assertEquals(null, res.error)
        assertEquals(Status.SUCCESS, res.status)
        assertEquals("Test", res.data!!.message.content)
    }

    @Test
    fun `get message when content in db is empty`() {
        val testMessage = getMessageEntity(123, "", true)
        val testMessageWithContent = testMessage.copy().apply { content = "Test" }

        val mWa = MessageWithAttachment(testMessage, emptyList())
        val mWaWithContent = MessageWithAttachment(testMessageWithContent, emptyList())

        coEvery {
            messageDb.loadMessageWithAttachment(
                1,
                testMessage.messageId
            )
        } returnsMany listOf(flowOf(mWa), flowOf(mWaWithContent))
        coEvery {
            sdk.getMessageDetails(
                messageId = testMessage.messageId,
                folderId = 1,
                read = false,
                id = testMessage.realId
            )
        } returns MessageDetails("Test", emptyList())
        coEvery { messageDb.updateAll(any()) } just Runs
        coEvery { messageAttachmentDao.insertAttachments(any()) } returns listOf(1)

        val res = runBlocking { repository.getMessage(student, testMessage).toFirstResult() }

        assertEquals(null, res.error)
        assertEquals(Status.SUCCESS, res.status)
        assertEquals("Test", res.data!!.message.content)
        coVerify { messageDb.updateAll(listOf(testMessageWithContent)) }
    }

    @Test(expected = UnknownHostException::class)
    fun `get message when content in db is empty and there is no internet connection`() {
        val testMessage = getMessageEntity(123, "", false)

        coEvery {
            messageDb.loadMessageWithAttachment(1, testMessage.messageId)
        } throws UnknownHostException()

        runBlocking { repository.getMessage(student, testMessage).toFirstResult() }
    }

    @Test(expected = UnknownHostException::class)
    fun `get message when content in db is empty, unread and there is no internet connection`() {
        val testMessage = getMessageEntity(123, "", true)

        coEvery {
            messageDb.loadMessageWithAttachment(1, testMessage.messageId)
        } throws UnknownHostException()

        runBlocking { repository.getMessage(student, testMessage).toList()[1] }
    }

    private fun getMessageEntity(
        messageId: Int,
        content: String,
        unread: Boolean
    ) = Message(
        studentId = 1,
        realId = 1,
        messageId = messageId,
        sender = "",
        senderId = 0,
        recipient = "Wielu adresatów",
        subject = "",
        date = LocalDateTime.MAX,
        folderId = 1,
        unread = unread,
        removed = false,
        hasAttachments = false
    ).apply {
        this.content = content
        unreadBy = 1
        readBy = 1
    }

    private fun getMessageDto(
        messageId: Int,
        content: String,
        unread: Boolean,
    ) = io.github.wulkanowy.sdk.pojo.Message(
        id = 1,
        messageId = messageId,
        sender = Sender("", "", 0, 0, 0, ""),
        recipients = listOf(),
        subject = "",
        content = content,
        date = LocalDateTime.MAX,
        folderId = 1,
        unread = unread,
        unreadBy = 0,
        readBy = 0,
        removed = false,
        hasAttachments = false,
    )
}
