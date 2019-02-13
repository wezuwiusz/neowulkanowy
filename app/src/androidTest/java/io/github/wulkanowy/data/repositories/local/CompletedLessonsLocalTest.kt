package io.github.wulkanowy.data.repositories.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.github.wulkanowy.data.db.AppDatabase
import io.github.wulkanowy.data.db.entities.CompletedLesson
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.repositories.completedlessons.CompletedLessonsLocal
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.threeten.bp.LocalDate
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
class CompletedLessonsLocalTest {

    private lateinit var completedLessonsLocal: CompletedLessonsLocal

    private lateinit var testDb: AppDatabase

    @Before
    fun createDb() {
        testDb = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), AppDatabase::class.java)
            .build()
        completedLessonsLocal = CompletedLessonsLocal(testDb.completedLessonsDao)
    }

    @After
    fun closeDb() {
        testDb.close()
    }

    @Test
    fun saveAndReadTest() {
        completedLessonsLocal.saveCompletedLessons(listOf(
            getCompletedLesson(LocalDate.of(2018, 9, 10), 1),
            getCompletedLesson(LocalDate.of(2018, 9, 14), 2),
            getCompletedLesson(LocalDate.of(2018, 9, 17), 3)
        ))

        val completed = completedLessonsLocal
            .getCompletedLessons(Semester(1, 1, 2, "", 3, 1),
                LocalDate.of(2018, 9, 10),
                LocalDate.of(2018, 9, 14)
            )
            .blockingGet()
        assertEquals(2, completed.size)
        assertEquals(completed[0].date, LocalDate.of(2018, 9, 10))
        assertEquals(completed[1].date, LocalDate.of(2018, 9, 14))
    }

    private fun getCompletedLesson(date: LocalDate, number: Int): CompletedLesson {
        return CompletedLesson(1, 2, date, number, "", "", "", "", "", "", "")
    }
}
