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
                Exam(1, 2, LocalDate.of(2018, 9, 10), LocalDate.now(), "", "", "", "", "", ""),
                Exam(1, 2, LocalDate.of(2018, 9, 14), LocalDate.now(), "", "", "", "", "", ""),
                Exam(1, 2, LocalDate.of(2018, 9, 17), LocalDate.now(), "", "", "", "", "", "")
        ))

        val exams = examLocal
                .getExams(Semester(1, 1, 2, "", 3, 1),
                        LocalDate.of(2018, 9, 10),
                        LocalDate.of(2018, 9, 14)
                )
                .blockingGet()
        assertEquals(2, exams.size)
        assertEquals(exams[0].date, LocalDate.of(2018, 9, 10))
        assertEquals(exams[1].date, LocalDate.of(2018, 9, 14))
    }
}
