package io.github.wulkanowy.data.repositories.local

import android.arch.persistence.room.Room
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import io.github.wulkanowy.data.db.AppDatabase
import io.github.wulkanowy.data.db.entities.Exam
import io.github.wulkanowy.data.db.entities.Semester
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.threeten.bp.LocalDate
import java.sql.Date
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
class ExamLocalTest {

    private lateinit var examLocal: ExamLocal

    private lateinit var testDb: AppDatabase

    @Before
    fun createDb() {
        testDb = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getContext(), AppDatabase::class.java).build()
        examLocal = ExamLocal(testDb.examsDao())
    }

    @After
    fun closeDb() {
        testDb.close()
    }

    @Test
    fun saveAndReadTest() {
        examLocal.saveExams(listOf(
                Exam(studentId = "1", diaryId = "2", date = Date.valueOf("2018-09-10")),
                Exam(studentId = "1", diaryId = "2", date = Date.valueOf("2018-09-14")),
                Exam(studentId = "1", diaryId = "2", date = Date.valueOf("2018-09-17")) // in next week
        ))

        val exams = examLocal
                .getExams(Semester(studentId = "1", diaryId = "2", semesterId = "3"), LocalDate.of(2018, 9, 10))
                .blockingGet()
        assertEquals(2, exams.size)
        assertEquals(exams[0].date, Date.valueOf("2018-09-10"))
        assertEquals(exams[1].date, Date.valueOf("2018-09-14"))
    }
}
