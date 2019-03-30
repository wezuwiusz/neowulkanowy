package io.github.wulkanowy.data.repositories.recipient

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.github.wulkanowy.data.db.AppDatabase
import io.github.wulkanowy.data.db.entities.Recipient
import io.github.wulkanowy.data.db.entities.ReportingUnit
import io.github.wulkanowy.data.db.entities.Student
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.threeten.bp.LocalDateTime
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
class RecipientLocalTest {

    private lateinit var recipientLocal: RecipientLocal

    private lateinit var testDb: AppDatabase

    @Before
    fun createDb() {
        testDb = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), AppDatabase::class.java)
            .build()
        recipientLocal = RecipientLocal(testDb.recipientDao)
    }

    @After
    fun closeDb() {
        testDb.close()
    }

    @Test
    fun saveAndReadTest() {
        recipientLocal.saveRecipients(listOf(
            Recipient(1, "2rPracownik", "Kowalski Jan", "Kowalski Jan [KJ] - Pracownik (Fake123456)", 3, 4, 2, "hash"),
            Recipient(1, "3rPracownik", "Kowalska Karolina", "Kowalska Karolina [KK] - Pracownik (Fake123456)", 4, 4, 2, "hash"),
            Recipient(1, "4rPracownik", "Krupa Stanisław", "Krupa Stanisław [KS] - Uczeń (Fake123456)", 5, 4, 1, "hash")
        ))

        val recipients = recipientLocal.getRecipients(
            Student("fakelog.cf", "AUTO", "", "", "", 1, "", "", "", 1, true, LocalDateTime.now()),
            2,
            ReportingUnit(1, 4, "", 0, "", emptyList())
        ).blockingGet()

        assertEquals(2, recipients.size)
        assertEquals(1, recipients[0].studentId)
        assertEquals("3rPracownik", recipients[1].realId)
        assertEquals("Kowalski Jan", recipients[0].name)
        assertEquals("Kowalska Karolina [KK] - Pracownik (Fake123456)", recipients[1].realName)
        assertEquals(3, recipients[0].loginId)
        assertEquals(4, recipients[1].unitId)
        assertEquals(2, recipients[0].role)
        assertEquals("hash", recipients[1].hash)
    }
}
