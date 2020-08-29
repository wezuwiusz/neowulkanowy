package io.github.wulkanowy.data.repositories.timetable

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.github.wulkanowy.data.db.AppDatabase
import io.github.wulkanowy.data.db.entities.Semester
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate
import java.time.LocalDateTime.of
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
        val list = listOf(
            createTimetableLocal(of(2018, 9, 10, 0, 0, 0), 1),
            createTimetableLocal(of(2018, 9, 14, 0, 0, 0), 1),
            createTimetableLocal(of(2018, 9, 17, 0, 0, 0), 1)
        )
        runBlocking { timetableDb.saveTimetable(list) }

        val semester = Semester(1, 2, "", 1, 1, 2019, LocalDate.now(), LocalDate.now(), 1, 1)
        val exams = runBlocking {
            timetableDb.getTimetable(
                semester = semester,
                startDate = LocalDate.of(2018, 9, 10),
                endDate = LocalDate.of(2018, 9, 14)
            ).first()
        }

        assertEquals(2, exams.size)
        assertEquals(exams[0].date, LocalDate.of(2018, 9, 10))
        assertEquals(exams[1].date, LocalDate.of(2018, 9, 14))
    }
}
