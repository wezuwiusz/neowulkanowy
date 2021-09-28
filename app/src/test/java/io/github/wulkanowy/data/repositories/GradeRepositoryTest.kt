package io.github.wulkanowy.data.repositories

import io.github.wulkanowy.data.db.dao.GradeDao
import io.github.wulkanowy.data.db.dao.GradeSummaryDao
import io.github.wulkanowy.data.mappers.mapToEntities
import io.github.wulkanowy.getSemesterEntity
import io.github.wulkanowy.getStudentEntity
import io.github.wulkanowy.sdk.Sdk
import io.github.wulkanowy.utils.AutoRefreshHelper
import io.github.wulkanowy.utils.toFirstResult
import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.SpyK
import io.mockk.just
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDate.of
import io.github.wulkanowy.sdk.pojo.Grade as SdkGrade

class GradeRepositoryTest {

    @SpyK
    private var sdk = Sdk()

    @MockK
    private lateinit var gradeDb: GradeDao

    @MockK
    private lateinit var gradeSummaryDb: GradeSummaryDao

    @MockK(relaxUnitFun = true)
    private lateinit var refreshHelper: AutoRefreshHelper

    private val semester = getSemesterEntity()

    private val student = getStudentEntity()

    private lateinit var gradeRepository: GradeRepository

    @Before
    fun initApi() {
        MockKAnnotations.init(this)
        every { refreshHelper.shouldBeRefreshed(any()) } returns false

        gradeRepository = GradeRepository(gradeDb, gradeSummaryDb, sdk, refreshHelper)

        coEvery { gradeDb.deleteAll(any()) } just Runs
        coEvery { gradeDb.insertAll(any()) } returns listOf()

        coEvery { gradeSummaryDb.loadAll(1, 1) } returnsMany listOf(flowOf(listOf()), flowOf(listOf()), flowOf(listOf()))
        coEvery { gradeSummaryDb.deleteAll(any()) } just Runs
        coEvery { gradeSummaryDb.insertAll(any()) } returns listOf()
    }

    @Test
    fun `mark grades older than registration date as read`() {
        // prepare
        val boundaryDate = of(2019, 2, 27).atStartOfDay()
        val remoteList = listOf(
            createGradeApi(5, 4.0, of(2019, 2, 25), "Ocena pojawiła się"),
            createGradeApi(5, 4.0, of(2019, 2, 26), "przed zalogowanie w aplikacji"),
            createGradeApi(5, 4.0, of(2019, 2, 27), "Ocena z dnia logowania"),
            createGradeApi(5, 4.0, of(2019, 2, 28), "Ocena jeszcze nowsza")
        )
        coEvery { sdk.getGrades(1) } returns (remoteList to emptyList())

        coEvery { gradeDb.loadAll(1, 1) } returnsMany listOf(
            flowOf(listOf()), // empty because it is new user
            flowOf(listOf()), // empty again, after fetch end before save result
            flowOf(remoteList.mapToEntities(semester)),
        )

        // execute
        val res = runBlocking { gradeRepository.getGrades(student.copy(registrationDate = boundaryDate), semester, true).toFirstResult() }

        // verify
        assertEquals(null, res.error)
        assertEquals(4, res.data?.first?.size)
        coVerify {
            gradeDb.insertAll(withArg {
                assertEquals(4, it.size)
                assertTrue(it[0].isRead)
                assertTrue(it[1].isRead)
                assertFalse(it[2].isRead)
                assertFalse(it[3].isRead)
            })
        }
    }

    @Test
    fun `mitigate mark grades as unread when old grades changed`() {
        // prepare
        val remoteList = listOf(
            createGradeApi(5, 2.0, of(2019, 2, 25), "Ocena ma datę, jest inna, ale nie zostanie powiadomiona"),
            createGradeApi(4, 3.0, of(2019, 2, 26), "starszą niż ostatnia lokalnie"),
            createGradeApi(3, 4.0, of(2019, 2, 27), "Ta jest z tego samego dnia co ostatnia lokalnie"),
            createGradeApi(2, 5.0, of(2019, 2, 28), "Ta jest już w ogóle nowa")
        )
        coEvery { sdk.getGrades(1) } returns (remoteList to emptyList())

        val localList = listOf(
            createGradeApi(5, 3.0, of(2019, 2, 25), "Jedna ocena"),
            createGradeApi(4, 4.0, of(2019, 2, 26), "Druga"),
            createGradeApi(3, 4.0, of(2019, 2, 27), "Ta jest z tego samego dnia co ostatnia lokalnie")
        )
        coEvery { gradeDb.loadAll(1, 1) } returnsMany listOf(
            flowOf(localList.mapToEntities(semester)),
            flowOf(localList.mapToEntities(semester)), // after fetch end before save result
            flowOf(remoteList.mapToEntities(semester))
        )

        // execute
        val res = runBlocking { gradeRepository.getGrades(student, semester, true).toFirstResult() }

        // verify
        assertEquals(null, res.error)
        assertEquals(4, res.data?.first?.size)
        coVerify {
            gradeDb.insertAll(withArg {
                assertEquals(3, it.size)
                assertTrue(it[0].isRead)
                assertTrue(it[1].isRead)
                assertFalse(it[2].isRead)
                assertEquals(remoteList.mapToEntities(semester).last(), it[2])
            })
        }
        coVerify {
            gradeDb.deleteAll(withArg {
                assertEquals(2, it.size)
            })
        }
    }

    @Test
    fun `force refresh when local contains duplicated grades`() {
        // prepare
        val remoteList = listOf(
            createGradeApi(5, 3.0, of(2019, 2, 25), "Taka sama ocena"),
            createGradeApi(3, 5.0, of(2019, 2, 26), "Jakaś inna ocena")
        )
        coEvery { sdk.getGrades(1) } returns (remoteList to emptyList())

        val localList = listOf(
            createGradeApi(5, 3.0, of(2019, 2, 25), "Taka sama ocena"),
            createGradeApi(5, 3.0, of(2019, 2, 25), "Taka sama ocena"), // will be removed...
            createGradeApi(3, 5.0, of(2019, 2, 26), "Jakaś inna ocena")
        )
        coEvery { gradeDb.loadAll(1, 1) } returnsMany listOf(
            flowOf(localList.mapToEntities(semester)),
            flowOf(localList.mapToEntities(semester)), // after fetch end before save result
            flowOf(remoteList.mapToEntities(semester))
        )

        // execute
        val res = runBlocking { gradeRepository.getGrades(student, semester, true).toFirstResult() }

        // verify
        assertEquals(null, res.error)
        assertEquals(2, res.data?.first?.size)
        coVerify { gradeDb.insertAll(match { it.isEmpty() }) }
        coVerify { gradeDb.deleteAll(match { it.size == 1 }) } // ... here
    }

    @Test
    fun `force refresh when remote contains duplicated grades`() {
        // prepare
        val remoteList = listOf(
            createGradeApi(5, 3.0, of(2019, 2, 25), "Taka sama ocena"),
            createGradeApi(5, 3.0, of(2019, 2, 25), "Taka sama ocena"), // will be added...
            createGradeApi(3, 5.0, of(2019, 2, 26), "Jakaś inna ocena")
        )
        coEvery { sdk.getGrades(1) } returns (remoteList to emptyList())

        val localList = listOf(
            createGradeApi(5, 3.0, of(2019, 2, 25), "Taka sama ocena"),
            createGradeApi(3, 5.0, of(2019, 2, 26), "Jakaś inna ocena")
        )
        coEvery { gradeDb.loadAll(1, 1) } returnsMany listOf(
            flowOf(localList.mapToEntities(semester)),
            flowOf(localList.mapToEntities(semester)), // after fetch end before save result
            flowOf(remoteList.mapToEntities(semester))
        )

        // execute
        val res = runBlocking { gradeRepository.getGrades(student, semester, true).toFirstResult() }

        // verify
        assertEquals(null, res.error)
        assertEquals(3, res.data?.first?.size)
        coVerify { gradeDb.insertAll(match { it.size == 1 }) } // ... here
        coVerify { gradeDb.deleteAll(match { it.isEmpty() }) }
    }

    @Test
    fun `force refresh when local is empty`() {
        // prepare
        val remoteList = listOf(
            createGradeApi(5, 3.0, of(2019, 2, 25), "Taka sama ocena"),
            createGradeApi(5, 3.0, of(2019, 2, 25), "Taka sama ocena"),
            createGradeApi(3, 5.0, of(2019, 2, 26), "Jakaś inna ocena")
        )
        coEvery { sdk.getGrades(1) } returns (remoteList to emptyList())

        coEvery { gradeDb.loadAll(1, 1) } returnsMany listOf(
            flowOf(listOf()),
            flowOf(listOf()), // after fetch end before save result
            flowOf(remoteList.mapToEntities(semester))
        )

        // execute
        val res = runBlocking { gradeRepository.getGrades(student, semester, true).toFirstResult() }

        // verify
        assertEquals(null, res.error)
        assertEquals(3, res.data?.first?.size)
    }

    @Test
    fun `force refresh when remote is empty`() {
        // prepare
        val remoteList = emptyList<SdkGrade>()
        coEvery { sdk.getGrades(semester.semesterId) } returns (remoteList to emptyList())

        val localList = listOf(
            createGradeApi(5, 3.0, of(2019, 2, 25), "Taka sama ocena"),
            createGradeApi(3, 5.0, of(2019, 2, 26), "Jakaś inna ocena")
        )
        coEvery { gradeDb.loadAll(semester.semesterId, student.studentId) } returnsMany listOf(
            flowOf(localList.mapToEntities(semester)),
            flowOf(remoteList.mapToEntities(semester)),
        )

        // execute
        val res = runBlocking { gradeRepository.getGrades(student, semester, true).toFirstResult() }

        // verify
        assertEquals(null, res.error)
        assertEquals(0, res.data?.first?.size)
    }

    private fun createGradeApi(value: Int, weight: Double, date: LocalDate, desc: String) = SdkGrade(
        subject = "",
        color = "",
        comment = "",
        date = date,
        description = desc,
        entry = "",
        modifier = .0,
        symbol = "",
        teacher = "",
        value = value.toDouble(),
        weight = weight.toString(),
        weightValue = weight
    )
}
