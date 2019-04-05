package io.github.wulkanowy.data.repositories.luckynumber

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.github.wulkanowy.data.db.AppDatabase
import io.github.wulkanowy.data.db.entities.LuckyNumber
import io.github.wulkanowy.data.db.entities.Semester
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.threeten.bp.LocalDate
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
class LuckyNumberLocalTest {

    private lateinit var luckyNumberLocal: LuckyNumberLocal

    private lateinit var testDb: AppDatabase

    @Before
    fun createDb() {
        testDb = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), AppDatabase::class.java)
            .build()
        luckyNumberLocal = LuckyNumberLocal(testDb.luckyNumberDao)
    }

    @After
    fun closeDb() {
        testDb.close()
    }

    @Test
    fun saveAndReadTest() {
        luckyNumberLocal.saveLuckyNumber(LuckyNumber(1, LocalDate.of(2019, 1, 20), 14))

        val luckyNumber = luckyNumberLocal.getLuckyNumber(Semester(1, 1, "", 1, 3, 2019, true, LocalDate.now(), LocalDate.now(), 1, 1),
            LocalDate.of(2019, 1, 20)
        ).blockingGet()

        assertEquals(1, luckyNumber.studentId)
        assertEquals(LocalDate.of(2019, 1, 20), luckyNumber.date)
        assertEquals(14, luckyNumber.luckyNumber)
    }
}
