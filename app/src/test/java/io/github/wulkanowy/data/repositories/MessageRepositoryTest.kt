package io.github.wulkanowy.data.repositories

import android.content.Context
import io.github.wulkanowy.data.dataOrNull
import io.github.wulkanowy.data.db.SharedPrefProvider
import io.github.wulkanowy.data.db.dao.MessageAttachmentDao
import io.github.wulkanowy.data.db.dao.MessagesDao
import io.github.wulkanowy.data.db.entities.Message
import io.github.wulkanowy.data.db.entities.MessageWithAttachment
import io.github.wulkanowy.data.enums.MessageFolder
import io.github.wulkanowy.data.errorOrNull
import io.github.wulkanowy.data.toFirstResult
import io.github.wulkanowy.getMailboxEntity
import io.github.wulkanowy.getStudentEntity
import io.github.wulkanowy.sdk.Sdk
import io.github.wulkanowy.sdk.pojo.Folder
import io.github.wulkanowy.utils.AutoRefreshHelper
import io.github.wulkanowy.utils.Status
import io.github.wulkanowy.utils.status
import io.mockk.*
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.SpyK
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.net.UnknownHostException
import java.time.Instant
import java.time.ZoneOffset
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
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

    private val mailbox = getMailboxEntity()

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
    fun `get messages when fetched completely new message without notify`() = runBlocking {
        every { messageDb.loadAll(any(), any()) } returns flowOf(emptyList())
        coEvery { sdk.getMessages(Folder.RECEIVED, any()) } returns listOf(
            getMessageDto()
        )
        coEvery { messageDb.deleteAll(any()) } just Runs
        coEvery { messageDb.insertAll(any()) } returns listOf()

        repository.getMessages(
            student = student,
            mailbox = mailbox,
            folder = MessageFolder.RECEIVED,
            forceRefresh = true,
            notify = false,
        ).toFirstResult().dataOrNull.orEmpty()

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
            messageDb.loadMessageWithAttachment("v4")
        } throws NoSuchElementException("No message in database")

        runBlocking { repository.getMessage(student, testMessage).toFirstResult() }
    }

    @Test
    fun `get message when content already in db`() {
        val testMessage = getMessageEntity(123, "Test", false)
        val messageWithAttachment = MessageWithAttachment(testMessage, emptyList())

        coEvery { messageDb.loadMessageWithAttachment("v4") } returns flowOf(
            messageWithAttachment
        )

        val res = runBlocking { repository.getMessage(student, testMessage).toFirstResult() }

        assertEquals(null, res.errorOrNull)
        assertEquals(Status.SUCCESS, res.status)
        assertEquals("Test", res.dataOrNull!!.message.content)
    }

    @Test
    fun `get message when content in db is empty`() = runTest {
        val testMessage = getMessageEntity(123, "", true)
        val testMessageWithContent = testMessage.copy().apply { content = "Test" }

        val mWa = MessageWithAttachment(testMessage, emptyList())
        val mWaWithContent = MessageWithAttachment(testMessageWithContent, emptyList())

        coEvery {
            messageDb.loadMessageWithAttachment("v4")
        } returnsMany listOf(flowOf(mWa), flowOf(mWaWithContent))
        coEvery {
            sdk.getMessageDetails("v4", any())
        } returns mockk {
            every { sender } returns ""
            every { recipients } returns listOf("")
            every { attachments } returns listOf()
        }
        coEvery { messageDb.updateAll(any()) } just Runs
        coEvery { messageAttachmentDao.insertAttachments(any()) } returns listOf(1)

        val res = repository.getMessage(student, testMessage).toFirstResult()

        assertEquals(null, res.errorOrNull)
        assertEquals(Status.SUCCESS, res.status)
        assertEquals("Test", res.dataOrNull!!.message.content)
        coVerify { messageDb.updateAll(listOf(testMessageWithContent)) }
    }

    @Test(expected = UnknownHostException::class)
    fun `get message when content in db is empty and there is no internet connection`() {
        val testMessage = getMessageEntity(123, "", false)

        coEvery {
            messageDb.loadMessageWithAttachment("v4")
        } throws UnknownHostException()

        runBlocking { repository.getMessage(student, testMessage).toFirstResult() }
    }

    @Test(expected = UnknownHostException::class)
    fun `get message when content in db is empty, unread and there is no internet connection`() {
        val testMessage = getMessageEntity(123, "", true)

        coEvery {
            messageDb.loadMessageWithAttachment("v4")
        } throws UnknownHostException()

        runBlocking { repository.getMessage(student, testMessage).toList()[1] }
    }

    private fun getMessageEntity(
        messageId: Int,
        content: String,
        unread: Boolean
    ) = Message(
        messageGlobalKey = "v4",
        mailboxKey = "",
        correspondents = "",
        messageId = messageId,
        subject = "",
        date = Instant.EPOCH,
        folderId = 1,
        unread = unread,
        hasAttachments = false
    ).apply {
        this.content = content
    }

    private fun getMessageDto() = io.github.wulkanowy.sdk.pojo.Message(
        globalKey = "v4",
        mailbox = "",
        correspondents = "",
        id = 4,
        recipients = listOf(),
        subject = "",
        content = "Test",
        dateZoned = Instant.EPOCH.atZone(ZoneOffset.UTC),
        folderId = 1,
        unread = true,
        hasAttachments = false,
    )
}
