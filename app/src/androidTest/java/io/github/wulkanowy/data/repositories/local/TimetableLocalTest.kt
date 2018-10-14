package io.github.wulkanowy.data.repositories.local

import android.arch.persistence.room.Room
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import io.github.wulkanowy.data.db.AppDatabase
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Timetable
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
class TimetableLocalTest {

    private lateinit var timetableDb: TimetableLocal

    private lateinit var testDb: AppDatabase

    @Before
    fun createDb() {
        testDb = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getContext(), AppDatabase::class.java).build()
        timetableDb = TimetableLocal(testDb.timetableDao())
    }

    @After
    fun closeDb() {
        testDb.close()
    }

    @Test
    fun saveAndReadTest() {
        timetableDb.saveTimetable(listOf(
                Timetable("1", "2", 1, LocalDateTime.now(), LocalDateTime.now(),
                        LocalDate.of(2018, 9, 10), "", "", "", "", ""),
                Timetable("1", "2", 1, LocalDateTime.now(), LocalDateTime.now(),
                        LocalDate.of(2018, 9, 14), "", "", "", "", ""),
                Timetable("1", "2", 1, LocalDateTime.now(), LocalDateTime.now(),
                        LocalDate.of(2018, 9, 17), "", "", "", "", "")
        ))

        val exams = timetableDb.getTimetable(
                Semester(0, "1", "2", "3", "", 1),
                LocalDate.of(2018, 9, 10),
                LocalDate.of(2018, 9, 14)
        ).blockingGet()
        assertEquals(2, exams.size)
        assertEquals(exams[0].date, LocalDate.of(2018, 9, 10))
        assertEquals(exams[1].date, LocalDate.of(2018, 9, 14))
    }
}
