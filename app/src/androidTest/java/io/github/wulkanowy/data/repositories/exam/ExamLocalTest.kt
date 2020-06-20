package io.github.wulkanowy.data.repositories.exam

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.github.wulkanowy.data.db.AppDatabase
import io.github.wulkanowy.data.db.entities.Exam
import io.github.wulkanowy.data.db.entities.Semester
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.threeten.bp.LocalDate.now
import org.threeten.bp.LocalDate.of
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
class ExamLocalTest {

    private lateinit var examLocal: ExamLocal

    private lateinit var testDb: AppDatabase

    @Before
    fun createDb() {
        testDb = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), AppDatabase::class.java).build()
        examLocal = ExamLocal(testDb.examsDao)
    }

    @After
    fun closeDb() {
        testDb.close()
    }

    @Test
    fun saveAndReadTest() {
        val list = listOf(
            Exam(1, 2, of(2018, 9, 10), now(), "", "", "", "", "", ""),
            Exam(1, 2, of(2018, 9, 14), now(), "", "", "", "", "", ""),
            Exam(1, 2, of(2018, 9, 17), now(), "", "", "", "", "", "")
        )
        runBlocking { examLocal.saveExams(list) }

        val semester = Semester(1, 2, "", 1, 3, 2019, now(), now(), 1, 1)
        val exams = runBlocking { examLocal.getExams(semester, of(2018, 9, 10), of(2018, 9, 14)) }
        assertEquals(2, exams.size)
        assertEquals(exams[0].date, of(2018, 9, 10))
        assertEquals(exams[1].date, of(2018, 9, 14))
    }
}
