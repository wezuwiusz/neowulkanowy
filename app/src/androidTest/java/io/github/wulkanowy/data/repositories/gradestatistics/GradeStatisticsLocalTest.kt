package io.github.wulkanowy.data.repositories.gradestatistics

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.github.wulkanowy.data.db.AppDatabase
import io.github.wulkanowy.data.db.entities.GradePointsStatistics
import io.github.wulkanowy.data.db.entities.GradeStatistics
import io.github.wulkanowy.data.db.entities.Semester
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.threeten.bp.LocalDate.now
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
class GradeStatisticsLocalTest {

    private lateinit var gradeStatisticsLocal: GradeStatisticsLocal

    private lateinit var testDb: AppDatabase

    @Before
    fun createDb() {
        testDb = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), AppDatabase::class.java)
            .build()
        gradeStatisticsLocal = GradeStatisticsLocal(testDb.gradeStatistics, testDb.gradePointsStatistics)
    }

    @After
    fun closeDb() {
        testDb.close()
    }

    @Test
    fun saveAndRead_subject() {
        val list = listOf(
            getGradeStatistics("Matematyka", 2, 1),
            getGradeStatistics("Fizyka", 1, 2)
        )
        runBlocking { gradeStatisticsLocal.saveGradesStatistics(list) }

        val stats = runBlocking { gradeStatisticsLocal.getGradesStatistics(getSemester(), false).first() }
        assertEquals(1, stats.size)
        assertEquals(stats[0].subject, "Matematyka")
    }

    @Test
    fun saveAndRead_all() {
        val list = listOf(
            getGradeStatistics("Matematyka", 2, 1),
            getGradeStatistics("Chemia", 2, 1),
            getGradeStatistics("Fizyka", 1, 2)
        )
        runBlocking { gradeStatisticsLocal.saveGradesStatistics(list) }

        val stats = runBlocking { gradeStatisticsLocal.getGradesStatistics(getSemester(), false).first() }
        assertEquals(2, stats.size)
//        assertEquals(3, stats.size)
//        assertEquals(stats[0].subject, "Wszystkie") // now in main repo
        assertEquals(stats[0].subject, "Matematyka")
        assertEquals(stats[1].subject, "Chemia")
    }

    @Test
    fun saveAndRead_points() {
        val list = listOf(
            getGradePointsStatistics("Matematyka", 2, 1),
            getGradePointsStatistics("Chemia", 2, 1),
            getGradePointsStatistics("Fizyka", 1, 2)
        )
        runBlocking { gradeStatisticsLocal.saveGradesPointsStatistics(list) }

        val stats = runBlocking { gradeStatisticsLocal.getGradesPointsStatistics(getSemester()).first() }
        with(stats[0]) {
            assertEquals(subject, "Matematyka")
            assertEquals(others, 5.0)
            assertEquals(student, 5.0)
        }
    }

    @Test
    fun saveAndRead_subjectEmpty() {
        runBlocking { gradeStatisticsLocal.saveGradesPointsStatistics(listOf()) }

        val stats = runBlocking { gradeStatisticsLocal.getGradesPointsStatistics(getSemester()).first() }
        assertEquals(emptyList(), stats)
    }

    @Test
    fun saveAndRead_allEmpty() {
        runBlocking { gradeStatisticsLocal.saveGradesPointsStatistics(listOf()) }

        val stats = runBlocking { gradeStatisticsLocal.getGradesPointsStatistics(getSemester()).first() }
        assertEquals(emptyList(), stats)
    }

    private fun getSemester(): Semester {
        return Semester(2, 2, "", 2019, 1, 2, now(), now(), 1, 1)
    }

    private fun getGradeStatistics(subject: String, studentId: Int, semesterId: Int): GradeStatistics {
        return GradeStatistics(studentId, semesterId, subject, 5, 5, false)
    }

    private fun getGradePointsStatistics(subject: String, studentId: Int, semesterId: Int): GradePointsStatistics {
        return GradePointsStatistics(studentId, semesterId, subject, 5.0, 5.0)
    }
}
