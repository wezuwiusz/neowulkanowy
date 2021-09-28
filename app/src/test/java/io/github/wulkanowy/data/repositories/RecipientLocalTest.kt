package io.github.wulkanowy.data.repositories

import io.github.wulkanowy.data.db.dao.RecipientDao
import io.github.wulkanowy.data.db.entities.ReportingUnit
import io.github.wulkanowy.data.mappers.mapToEntities
import io.github.wulkanowy.getStudentEntity
import io.github.wulkanowy.sdk.Sdk
import io.github.wulkanowy.utils.AutoRefreshHelper
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.SpyK
import io.mockk.just
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
        SdkRecipient("2rPracownik", "Kowalski Jan", 3, 4, 2, "hash", "Kowalski Jan [KJ] - Pracownik (Fake123456)"),
        SdkRecipient("3rPracownik", "Kowalska Karolina", 4, 4, 2, "hash", "Kowalska Karolina [KK] - Pracownik (Fake123456)"),
        SdkRecipient("4rPracownik", "Krupa Stanisław", 5, 4, 1, "hash", "Krupa Stanisław [KS] - Uczeń (Fake123456)")
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
        coEvery { recipientDb.loadAll(4, 123, 7) } returnsMany listOf(
            remoteList.mapToEntities(4),
            remoteList.mapToEntities(4)
        )
        coEvery { recipientDb.insertAll(any()) } returns listOf(1, 2, 3)
        coEvery { recipientDb.deleteAll(any()) } just Runs

        // execute
        val res = runBlocking { recipientRepository.getRecipients(student, ReportingUnit(4, 123, "", 4, "", listOf()), 7) }

        // verify
        assertEquals(3, res.size)
        coVerify { recipientDb.loadAll(4, 123, 7) }
    }

    @Test
    fun `load recipients when database is empty`() {
        // prepare
        coEvery { sdk.getRecipients(123, 7) } returns remoteList
        coEvery { recipientDb.loadAll(4, 123, 7) } returnsMany listOf(
            emptyList(),
            remoteList.mapToEntities(4)
        )
        coEvery { recipientDb.insertAll(any()) } returns listOf(1, 2, 3)
        coEvery { recipientDb.deleteAll(any()) } just Runs

        // execute
        val res = runBlocking { recipientRepository.getRecipients(student, ReportingUnit(4, 123, "", 4, "", listOf()), 7) }

        // verify
        assertEquals(3, res.size)
        coVerify { sdk.getRecipients(123, 7) }
        coVerify { recipientDb.loadAll(4, 123, 7) }
        coVerify { recipientDb.insertAll(match { it.isEmpty() }) }
        coVerify { recipientDb.deleteAll(match { it.isEmpty() }) }
    }
}
