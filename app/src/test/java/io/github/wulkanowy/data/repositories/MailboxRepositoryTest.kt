package io.github.wulkanowy.data.repositories

import io.github.wulkanowy.data.db.dao.MailboxDao
import io.github.wulkanowy.data.db.entities.Mailbox
import io.github.wulkanowy.data.db.entities.MailboxType
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.sdk.Sdk
import io.github.wulkanowy.utils.AutoRefreshHelper
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.SpyK
import io.mockk.just
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.Instant

@OptIn(ExperimentalCoroutinesApi::class)
class MailboxRepositoryTest {

    @SpyK
    private var sdk = Sdk()

    @MockK
    private lateinit var mailboxDao: MailboxDao

    @MockK
    private lateinit var refreshHelper: AutoRefreshHelper

    private lateinit var systemUnderTest: MailboxRepository

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        coEvery { refreshHelper.shouldBeRefreshed(any()) } returns false
        coEvery { refreshHelper.updateLastRefreshTimestamp(any()) } just Runs
        coEvery { mailboxDao.deleteAll(any()) } just Runs
        coEvery { mailboxDao.insertAll(any()) } returns emptyList()
        coEvery { mailboxDao.loadAll(any()) } returns emptyList()
        coEvery { sdk.getMailboxes() } returns emptyList()

        systemUnderTest = MailboxRepository(
            mailboxDao = mailboxDao,
            sdk = sdk,
            refreshHelper = refreshHelper,
        )
    }

    @Test(expected = IllegalArgumentException::class)
    fun `get mailbox that doesn't exist`() = runTest {
        val student = getStudentEntity(
            userName = "Stanisław Kowalski",
            studentName = "Jan Kowalski",
        )
        coEvery { sdk.getMailboxes() } returns emptyList()

        systemUnderTest.getMailbox(student)
    }

    @Test
    fun `get mailbox for user with additional spaces`() = runTest {
        val student = getStudentEntity(
            userName = "  Stanisław Kowalski  ",
            studentName = "  Jan Kowalski  ",
        )
        val expectedMailbox = getMailboxEntity("Jan Kowalski ")
        coEvery { mailboxDao.loadAll(any()) } returns listOf(
            expectedMailbox,
        )

        val selectedMailbox = systemUnderTest.getMailbox(student)
        assertEquals(expectedMailbox, selectedMailbox)
    }

    @Test
    fun `get mailbox for unique non-authorized student`() = runTest {
        val student = getStudentEntity(
            userName = "Stanisław Kowalski",
            studentName = "J** K*******",
        )
        val expectedMailbox = getMailboxEntity("Jan Kowalski")
        coEvery { mailboxDao.loadAll(any()) } returns listOf(
            expectedMailbox,
        )

        assertEquals(expectedMailbox, systemUnderTest.getMailbox(student))
    }

    @Test
    fun `get mailbox for unique non-authorized student but with spaces`() = runTest {
        val student = getStudentEntity(
            userName = "Stanisław Kowalski",
            studentName = "J**  K*******",
        )
        val expectedMailbox = getMailboxEntity("Jan  Kowalski")
        coEvery { mailboxDao.loadAll(any()) } returns listOf(
            expectedMailbox,
        )

        assertEquals(expectedMailbox, systemUnderTest.getMailbox(student))
    }

    @Test(expected = IllegalArgumentException::class)
    fun `get mailbox for not-unique non-authorized student`() = runTest {
        val student = getStudentEntity(
            userName = "Stanisław Kowalski",
            studentName = "J** K*******",
        )
        coEvery { mailboxDao.loadAll(any()) } returns listOf(
            getMailboxEntity("Jan Kowalski"),
            getMailboxEntity("Jan Kurowski"),
        )

        systemUnderTest.getMailbox(student)
    }

    @Test
    fun `get mailbox for student with uppercase name`() = runTest {
        val student = getStudentEntity(
            userName = "Mochoń Julia",
            studentName = "KLAUDIA MOCHOŃ",
        )
        val expectedMailbox = getMailboxEntity("Klaudia Mochoń")
        coEvery { mailboxDao.loadAll(any()) } returns listOf(
            expectedMailbox,
        )

        assertEquals(expectedMailbox, systemUnderTest.getMailbox(student))
    }

    @Test
    fun `get mailbox for student with second name`() = runTest {
        val student = getStudentEntity(
            userName = "Fistaszek Karolina",
            studentName = "Julia Fistaszek",
        )
        val expectedMailbox = getMailboxEntity("Julia Maria Fistaszek")
        coEvery { mailboxDao.loadAll(any()) } returns listOf(
            expectedMailbox,
        )

        assertEquals(expectedMailbox, systemUnderTest.getMailbox(student))
    }

    @Test
    fun `get mailbox for student with second name and uppercase`() = runTest {
        val student = getStudentEntity(
            userName = "BEDNAREK KAMIL",
            studentName = "ALEKSANDRA BEDNAREK",
        )
        val expectedMailbox = getMailboxEntity("Aleksandra Anna Bednarek")
        coEvery { mailboxDao.loadAll(any()) } returns listOf(
            expectedMailbox,
        )

        assertEquals(expectedMailbox, systemUnderTest.getMailbox(student))
    }

    private fun getMailboxEntity(
        studentName: String,
    ) = Mailbox(
        globalKey = "",
        fullName = "",
        userName = "",
        userLoginId = 123,
        studentName = studentName,
        schoolNameShort = "",
        type = MailboxType.STUDENT,
    )

    private fun getStudentEntity(
        studentName: String,
        userName: String,
    ) = Student(
        scrapperBaseUrl = "http://fakelog.cf",
        email = "jan@fakelog.cf",
        certificateKey = "",
        classId = 0,
        className = "",
        isCurrent = false,
        isParent = false,
        loginMode = Sdk.Mode.API.name,
        loginType = Sdk.ScrapperLoginType.STANDARD.name,
        mobileBaseUrl = "",
        password = "",
        privateKey = "",
        registrationDate = Instant.now(),
        schoolName = "",
        schoolShortName = "test",
        schoolSymbol = "",
        studentId = 1,
        studentName = studentName,
        symbol = "",
        userLoginId = 1,
        userName = userName,
    )
}
