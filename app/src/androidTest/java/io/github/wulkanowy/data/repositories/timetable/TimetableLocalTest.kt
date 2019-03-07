package io.github.wulkanowy.data.repositories.timetable

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.github.wulkanowy.data.db.AppDatabase
import io.github.wulkanowy.data.db.entities.Semester
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime.of
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
class TimetableLocalTest {

    private lateinit var timetableDb: TimetableLocal

    private lateinit var testDb: AppDatabase

    @Before
    fun createDb() {
        testDb = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), AppDatabase::class.java)
            .build()
        timetableDb = TimetableLocal(testDb.timetableDao)
    }

    @After
    fun closeDb() {
        testDb.close()
    }

    @Test
    fun saveAndReadTest() {
        timetableDb.saveTimetable(listOf(
            createTimetableLocal(1, of(2018, 9, 10, 0, 0, 0)),
            createTimetableLocal(1, of(2018, 9, 14, 0, 0, 0)),
            createTimetableLocal(1, of(2018, 9, 17, 0, 0, 0))
        ))

        val exams = timetableDb.getTimetable(
            Semester(1, 2, "", 1, 1, true, 1, 1),
            LocalDate.of(2018, 9, 10),
            LocalDate.of(2018, 9, 14)
        ).blockingGet()

        assertEquals(2, exams.size)
        assertEquals(exams[0].date, LocalDate.of(2018, 9, 10))
        assertEquals(exams[1].date, LocalDate.of(2018, 9, 14))
    }
}
