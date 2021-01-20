package io.github.wulkanowy.data.repositories

import io.github.wulkanowy.data.Status
import io.github.wulkanowy.data.db.dao.MessageAttachmentDao
import io.github.wulkanowy.data.db.dao.MessagesDao
import io.github.wulkanowy.data.db.entities.Message
import io.github.wulkanowy.data.db.entities.MessageWithAttachment
import io.github.wulkanowy.getStudentEntity
import io.github.wulkanowy.sdk.Sdk
import io.github.wulkanowy.sdk.pojo.MessageDetails
import io.github.wulkanowy.utils.AutoRefreshHelper
import io.github.wulkanowy.utils.toFirstResult
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.SpyK
import io.mockk.just
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.net.UnknownHostException
import java.time.LocalDateTime

class MessageRepositoryTest {

    @SpyK
    private var sdk = Sdk()

    @MockK
    private lateinit var messageDb: MessagesDao

    @MockK
    private lateinit var messageAttachmentDao: MessageAttachmentDao

    @MockK(relaxUnitFun = true)
    private lateinit var refreshHelper: AutoRefreshHelper

    private val student = getStudentEntity()

    private lateinit var messageRepository: MessageRepository

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        every { refreshHelper.isShouldBeRefreshed(any()) } returns false

        messageRepository = MessageRepository(messageDb, messageAttachmentDao, sdk, refreshHelper)
    }

    @Test(expected = NoSuchElementException::class)
    fun `throw error when message is not in the db`() {
        val testMessage = getMessageEntity(1, "", false)
        coEvery { messageDb.loadMessageWithAttachment(1, 1) } throws NoSuchElementException("No message in database")

        runBlocking { messageRepository.getMessage(student, testMessage).toFirstResult() }
    }

    @Test
    fun `get message when content already in db`() {
        val testMessage = getMessageEntity(123, "Test", false)
        val messageWithAttachment = MessageWithAttachment(testMessage, emptyList())

        coEvery { messageDb.loadMessageWithAttachment(1, testMessage.messageId) } returns flowOf(messageWithAttachment)

        val res = runBlocking { messageRepository.getMessage(student, testMessage).toFirstResult() }

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

        coEvery { messageDb.loadMessageWithAttachment(1, testMessage.messageId) } returnsMany listOf(flowOf(mWa), flowOf(mWaWithContent))
        coEvery { sdk.getMessageDetails(testMessage.messageId, 1, false, testMessage.realId) } returns MessageDetails("Test", emptyList())
        coEvery { messageDb.updateAll(any()) } just Runs
        coEvery { messageAttachmentDao.insertAttachments(any()) } returns listOf(1)

        val res = runBlocking { messageRepository.getMessage(student, testMessage).toFirstResult() }

        assertEquals(null, res.error)
        assertEquals(Status.SUCCESS, res.status)
        assertEquals("Test", res.data!!.message.content)
        coVerify { messageDb.updateAll(listOf(testMessageWithContent)) }
    }

    @Test(expected = UnknownHostException::class)
    fun `get message when content in db is empty and there is no internet connection`() {
        val testMessage = getMessageEntity(123, "", false)

        coEvery { messageDb.loadMessageWithAttachment(1, testMessage.messageId) } throws UnknownHostException()

        runBlocking { messageRepository.getMessage(student, testMessage).toFirstResult() }
    }

    @Test(expected = UnknownHostException::class)
    fun `get message when content in db is empty, unread and there is no internet connection`() {
        val testMessage = getMessageEntity(123, "", true)

        coEvery { messageDb.loadMessageWithAttachment(1, testMessage.messageId) } throws UnknownHostException()

        runBlocking { messageRepository.getMessage(student, testMessage).toList()[1] }
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
        senderId = 1,
        recipient = "",
        subject = "",
        date = LocalDateTime.now(),
        folderId = 1,
        unread = unread,
        removed = false,
        hasAttachments = false
    ).apply {
        this.content = content
        unreadBy = 1
        readBy = 1
    }
}
