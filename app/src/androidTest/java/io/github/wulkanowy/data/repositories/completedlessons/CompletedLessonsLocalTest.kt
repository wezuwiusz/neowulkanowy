package io.github.wulkanowy.data.repositories.completedlessons

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.github.wulkanowy.data.db.AppDatabase
import io.github.wulkanowy.data.db.entities.CompletedLesson
import io.github.wulkanowy.data.db.entities.Semester
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDate.now
import org.threeten.bp.LocalDate.of
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
class CompletedLessonsLocalTest {

    private lateinit var completedLessonsLocal: CompletedLessonsLocal

    private lateinit var testDb: AppDatabase

    @Before
    fun createDb() {
        testDb = Room
            .inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), AppDatabase::class.java)
            .build()
        completedLessonsLocal = CompletedLessonsLocal(testDb.completedLessonsDao)
    }

    @After
    fun closeDb() {
        testDb.close()
    }

    @Test
    fun saveAndReadTest() {
        val list = listOf(
            getCompletedLesson(of(2018, 9, 10), 1),
            getCompletedLesson(of(2018, 9, 14), 2),
            getCompletedLesson(of(2018, 9, 17), 3)
        )
        runBlocking { completedLessonsLocal.saveCompletedLessons(list) }

        val semester = Semester(1, 2, "", 1, 3, 2019, now(), now(), 1, 1)
        val completed = runBlocking { completedLessonsLocal.getCompletedLessons(semester, of(2018, 9, 10), of(2018, 9, 14)).first() }
        assertEquals(2, completed.size)
        assertEquals(completed[0].date, of(2018, 9, 10))
        assertEquals(completed[1].date, of(2018, 9, 14))
    }

    private fun getCompletedLesson(date: LocalDate, number: Int): CompletedLesson {
        return CompletedLesson(1, 2, date, number, "", "", "", "", "", "", "")
    }
}
