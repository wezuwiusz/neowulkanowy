package io.github.wulkanowy.data.repositories.semester

import io.github.wulkanowy.TestDispatchersProvider
import io.github.wulkanowy.createSemesterEntity
import io.github.wulkanowy.data.db.entities.Student
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test
import java.time.LocalDate.now

class SemesterRepositoryTest {

    @MockK
    private lateinit var semesterRemote: SemesterRemote

    @MockK
    private lateinit var semesterLocal: SemesterLocal

    @MockK
    private lateinit var student: Student

    private lateinit var semesterRepository: SemesterRepository

    @Before
    fun initTest() {
        MockKAnnotations.init(this)
        semesterRepository = SemesterRepository(semesterRemote, semesterLocal, TestDispatchersProvider())
        every { student.loginMode } returns "SCRAPPER"
    }

    @Test
    fun getSemesters_noSemesters() {
        val semesters = listOf(
            createSemesterEntity(1, 1, now().minusMonths(6), now().minusMonths(3)),
            createSemesterEntity(1, 2, now().minusMonths(3), now())
        )

        coEvery { semesterLocal.getSemesters(student) } returns emptyList()
        coEvery { semesterRemote.getSemesters(student) } returns semesters
        coEvery { semesterLocal.deleteSemesters(any()) } just Runs
        coEvery { semesterLocal.saveSemesters(any()) } just Runs

        runBlocking { semesterRepository.getSemesters(student) }

        coVerify { semesterLocal.saveSemesters(semesters) }
        coVerify { semesterLocal.deleteSemesters(emptyList()) }
    }

    @Test
    fun getSemesters_invalidDiary_api() {
        every { student.loginMode } returns "API"
        val badSemesters = listOf(
            createSemesterEntity(0, 1, now().minusMonths(6), now().minusMonths(3)),
            createSemesterEntity(0, 2, now().minusMonths(3), now())
        )

        val goodSemesters = listOf(
            createSemesterEntity(122, 1, now().minusMonths(6), now().minusMonths(3)),
            createSemesterEntity(123, 2, now().minusMonths(3), now())
        )

        coEvery { semesterLocal.getSemesters(student) } returns badSemesters
        coEvery { semesterRemote.getSemesters(student) } returns goodSemesters
        coEvery { semesterLocal.deleteSemesters(any()) } just Runs
        coEvery { semesterLocal.saveSemesters(any()) } just Runs

        val items = runBlocking { semesterRepository.getSemesters(student) }
        assertEquals(2, items.size)
        assertEquals(0, items[0].diaryId)
    }

    @Test
    fun getSemesters_invalidDiary_scrapper() {
        every { student.loginMode } returns "SCRAPPER"
        val badSemesters = listOf(
            createSemesterEntity(0, 1, now().minusMonths(6), now().minusMonths(3)),
            createSemesterEntity(0, 2, now().minusMonths(3), now())
        )

        val goodSemesters = listOf(
            createSemesterEntity(1, 1, now().minusMonths(6), now().minusMonths(3)),
            createSemesterEntity(1, 2, now().minusMonths(3), now())
        )

        coEvery { semesterLocal.getSemesters(student) } returnsMany listOf(badSemesters, badSemesters, goodSemesters)
        coEvery { semesterRemote.getSemesters(student) } returns goodSemesters
        coEvery { semesterLocal.deleteSemesters(any()) } just Runs
        coEvery { semesterLocal.saveSemesters(any()) } just Runs

        val items = runBlocking { semesterRepository.getSemesters(student) }
        assertEquals(2, items.size)
        assertNotEquals(0, items[0].diaryId)
    }

    @Test
    fun getSemesters_noCurrent() {
        val semesters = listOf(
            createSemesterEntity(1, 1, now().minusMonths(12), now().minusMonths(6)),
            createSemesterEntity(1, 2, now().minusMonths(6), now().minusMonths(1))
        )

        coEvery { semesterLocal.getSemesters(student) } returns semesters

        val items = runBlocking { semesterRepository.getSemesters(student) }
        assertEquals(2, items.size)
    }

    @Test
    fun getSemesters_oneCurrent() {
        val semesters = listOf(
            createSemesterEntity(1, 1, now().minusMonths(6), now().minusMonths(3)),
            createSemesterEntity(1, 2, now().minusMonths(3), now())
        )

        coEvery { semesterLocal.getSemesters(student) } returns semesters

        val items = runBlocking { semesterRepository.getSemesters(student) }
        assertEquals(2, items.size)
    }

    @Test
    fun getSemesters_doubleCurrent() {
        val semesters = listOf(
            createSemesterEntity(1, 1, now(), now()),
            createSemesterEntity(1, 2, now(), now())
        )

        coEvery { semesterLocal.getSemesters(student) } returns semesters

        val items = runBlocking { semesterRepository.getSemesters(student) }
        assertEquals(2, items.size)
    }

    @Test
    fun getSemesters_noSemesters_refreshOnNoCurrent() {
        val semesters = listOf(
            createSemesterEntity(1, 1, now().minusMonths(6), now().minusMonths(3)),
            createSemesterEntity(1, 2, now().minusMonths(3), now())
        )

        coEvery { semesterLocal.getSemesters(student) } returns emptyList()
        coEvery { semesterRemote.getSemesters(student) } returns semesters
        coEvery { semesterLocal.deleteSemesters(any()) } just Runs
        coEvery { semesterLocal.saveSemesters(any()) } just Runs

        runBlocking { semesterRepository.getSemesters(student, refreshOnNoCurrent = true) }

        coVerify { semesterLocal.deleteSemesters(emptyList()) }
        coVerify { semesterLocal.saveSemesters(semesters) }
    }

    @Test
    fun getSemesters_noCurrent_refreshOnNoCurrent() {
        val semestersWithNoCurrent = listOf(
            createSemesterEntity(1, 1, now().minusMonths(12), now().minusMonths(6)),
            createSemesterEntity(1, 2, now().minusMonths(6), now().minusMonths(1))
        )

        val newSemesters = listOf(
            createSemesterEntity(1, 1, now().minusMonths(12), now().minusMonths(6)),
            createSemesterEntity(1, 2, now().minusMonths(6), now().minusMonths(1)),

            createSemesterEntity(2, 1, now().minusMonths(1), now().plusMonths(5)),
            createSemesterEntity(2, 2, now().plusMonths(5), now().plusMonths(11)),
        )

        coEvery { semesterLocal.getSemesters(student) } returns semestersWithNoCurrent
        coEvery { semesterRemote.getSemesters(student) } returns newSemesters
        coEvery { semesterLocal.deleteSemesters(any()) } just Runs
        coEvery { semesterLocal.saveSemesters(any()) } just Runs

        val items = runBlocking { semesterRepository.getSemesters(student, refreshOnNoCurrent = true) }
        assertEquals(2, items.size)
    }

    @Test
    fun getSemesters_doubleCurrent_refreshOnNoCurrent() {
        val semesters = listOf(
            createSemesterEntity(1, 1, now(), now()),
            createSemesterEntity(1, 2, now(), now())
        )

        coEvery { semesterLocal.getSemesters(student) } returns semesters

        val items = runBlocking { semesterRepository.getSemesters(student, refreshOnNoCurrent = true) }
        assertEquals(2, items.size)
    }

    @Test(expected = IllegalArgumentException::class)
    fun getCurrentSemester_doubleCurrent() {
        val semesters = listOf(
            createSemesterEntity(1, 1, now(), now()),
            createSemesterEntity(1, 1, now(), now())
        )

        coEvery { semesterLocal.getSemesters(student) } returns semesters

        runBlocking { semesterRepository.getCurrentSemester(student) }
    }

    @Test(expected = RuntimeException::class)
    fun getCurrentSemester_emptyList() {
        coEvery { semesterLocal.getSemesters(student) } returns emptyList()

        runBlocking { semesterRepository.getCurrentSemester(student) }
    }
}
