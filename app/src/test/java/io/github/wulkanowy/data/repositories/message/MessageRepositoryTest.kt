package io.github.wulkanowy.data.repositories.message

import androidx.room.EmptyResultSetException
import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.InternetObservingSettings
import io.github.wulkanowy.data.SdkHelper
import io.github.wulkanowy.data.db.entities.Message
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.repositories.UnitTestInternetObservingStrategy
import io.reactivex.Single
import io.reactivex.observers.TestObserver
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import org.threeten.bp.LocalDateTime.now
import java.net.UnknownHostException

class MessageRepositoryTest {

    @Mock
    lateinit var sdk: SdkHelper

    @Mock
    lateinit var local: MessageLocal

    @Mock
    lateinit var remote: MessageRemote

    @Mock
    lateinit var student: Student

    private val testObservingStrategy = UnitTestInternetObservingStrategy()

    private lateinit var repo: MessageRepository

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)

        repo = MessageRepository(InternetObservingSettings.builder()
            .strategy(testObservingStrategy)
            .build(), local, remote, sdk)
    }

    @Test
    fun `throw error when message is not in the db`() {
        `when`(local.getMessage(123)).thenReturn(Single.error(EmptyResultSetException("No message in database")))

        val message = repo.getMessage(student, 123)
        val messageObserver = TestObserver<Message>()
        message.subscribe(messageObserver)
        messageObserver.assertError(EmptyResultSetException::class.java)
    }

    @Test
    fun `get message when content already in db`() {
        `when`(local.getMessage(123)).thenReturn(Single.just(
            Message(1, 1, 123, "", 1, "", "", "Test", now(), 1, false, 1, 1, false)
        ))

        val message = repo.getMessage(student, 123).blockingGet()

        assertEquals("Test", message.content)
    }

    @Test
    fun `get message when content in db is empty`() {
        val testMessage = Message(1, 1, 123, "", 1, "", "", "", now(), 1, true, 1, 1, false)
        val testMessageWithContent = testMessage.copy(content = "Test")

        `when`(local.getMessage(123))
            .thenReturn(Single.just(testMessage))
            .thenReturn(Single.just(testMessageWithContent))
        `when`(remote.getMessagesContent(testMessageWithContent)).thenReturn(Single.just("Test"))

        val message = repo.getMessage(student, 123).blockingGet()

        assertEquals("Test", message.content)
        verify(local).updateMessages(listOf(testMessageWithContent))
    }

    @Test
    fun `get message when content in db is empty and there is no internet connection`() {
        val testMessage = Message(1, 1, 123, "", 1, "", "", "", now(), 1, false, 1, 1, false)

        testObservingStrategy.isInternetConnection = false
        `when`(local.getMessage(123)).thenReturn(Single.just(testMessage))

        val message = repo.getMessage(student, 123)
        val messageObserver = TestObserver<Message>()
        message.subscribe(messageObserver)
        messageObserver.assertError(UnknownHostException::class.java)
    }

    @Test
    fun `get message when content in db is empty, unread and there is no internet connection`() {
        val testMessage = Message(1, 1, 123, "", 1, "", "", "", now(), 1, true, 1, 1, false)

        testObservingStrategy.isInternetConnection = false
        `when`(local.getMessage(123)).thenReturn(Single.just(testMessage))

        val message = repo.getMessage(student, 123)
        val messageObserver = TestObserver<Message>()
        message.subscribe(messageObserver)
        messageObserver.assertError(UnknownHostException::class.java)
    }
}
