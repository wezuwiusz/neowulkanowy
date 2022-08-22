package io.github.wulkanowy.data.repositories

import io.github.wulkanowy.data.db.dao.RecipientDao
import io.github.wulkanowy.data.mappers.mapToEntities
import io.github.wulkanowy.getMailboxEntity
import io.github.wulkanowy.getStudentEntity
import io.github.wulkanowy.sdk.Sdk
import io.github.wulkanowy.sdk.pojo.MailboxType
import io.github.wulkanowy.utils.AutoRefreshHelper
import io.mockk.*
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.SpyK
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import io.github.wulkanowy.sdk.pojo.Recipient as SdkRecipient

class RecipientLocalTest {

    @SpyK
    private var sdk = Sdk()

    @MockK
    private lateinit var recipientDb: RecipientDao

    @MockK(relaxUnitFun = true)
    private lateinit var refreshHelper: AutoRefreshHelper

    private val student = getStudentEntity()

    private lateinit var recipientRepository: RecipientRepository

    private val remoteList = listOf(
        SdkRecipient(
            mailboxGlobalKey = "2rPracownik",
            userName = "Kowalski Jan",
            fullName = "Kowalski Jan [KJ] - Pracownik (Fake123456)",
            studentName = "",
            schoolNameShort = "",
            type = MailboxType.UNKNOWN,
        ),
        SdkRecipient(
            mailboxGlobalKey = "3rPracownik",
            userName = "Kowalska Karolina",
            fullName = "Kowalska Karolina [KK] - Pracownik (Fake123456)",
            studentName = "",
            schoolNameShort = "",
            type = MailboxType.UNKNOWN,
        ),
        SdkRecipient(
            mailboxGlobalKey = "4rPracownik",
            userName = "Krupa Stanisław",
            fullName = "Krupa Stanisław [KS] - Uczeń (Fake123456)",
            studentName = "",
            schoolNameShort = "",
            type = MailboxType.UNKNOWN,
        )
    )

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        every { refreshHelper.shouldBeRefreshed(any()) } returns false

        recipientRepository = RecipientRepository(recipientDb, sdk, refreshHelper)
    }

    @Test
    fun `load recipients when items already in database`() {
        // prepare
        coEvery { recipientDb.loadAll(io.github.wulkanowy.data.db.entities.MailboxType.UNKNOWN, "v4") } returnsMany listOf(
            remoteList.mapToEntities("v4"),
            remoteList.mapToEntities("v4")
        )
        coEvery { recipientDb.insertAll(any()) } returns listOf(1, 2, 3)
        coEvery { recipientDb.deleteAll(any()) } just Runs

        // execute
        val res = runBlocking {
            recipientRepository.getRecipients(
                student = student,
                mailbox = getMailboxEntity(),
                type = io.github.wulkanowy.data.db.entities.MailboxType.UNKNOWN,
            )
        }

        // verify
        assertEquals(3, res.size)
        coVerify {
            recipientDb.loadAll(
                type = io.github.wulkanowy.data.db.entities.MailboxType.UNKNOWN,
                studentMailboxGlobalKey = "v4"
            )
        }
    }

    @Test
    fun `load recipients when database is empty`() {
        // prepare
        coEvery { sdk.getRecipients("v4") } returns remoteList
        coEvery {
            recipientDb.loadAll(
                io.github.wulkanowy.data.db.entities.MailboxType.UNKNOWN,
                "v4"
            )
        } returnsMany listOf(
            emptyList(),
            remoteList.mapToEntities("v4")
        )
        coEvery { recipientDb.insertAll(any()) } returns listOf(1, 2, 3)
        coEvery { recipientDb.deleteAll(any()) } just Runs

        // execute
        val res = runBlocking {
            recipientRepository.getRecipients(
                student = student,
                mailbox = getMailboxEntity(),
                type = io.github.wulkanowy.data.db.entities.MailboxType.UNKNOWN,
            )
        }

        // verify
        assertEquals(3, res.size)
        coVerify { sdk.getRecipients("v4") }
        coVerify { recipientDb.loadAll(io.github.wulkanowy.data.db.entities.MailboxType.UNKNOWN, "v4") }
        coVerify { recipientDb.insertAll(match { it.isEmpty() }) }
        coVerify { recipientDb.deleteAll(match { it.isEmpty() }) }
    }
}
