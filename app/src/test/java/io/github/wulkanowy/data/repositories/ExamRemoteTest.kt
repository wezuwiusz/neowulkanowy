package io.github.wulkanowy.data.repositories

import io.github.wulkanowy.data.db.dao.ExamDao
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
import org.junit.Before
import org.junit.Test
import java.time.LocalDate
import io.github.wulkanowy.sdk.pojo.Exam as SdkExam

class ExamRemoteTest {

    @SpyK
    private var sdk = Sdk()

    @MockK
    private lateinit var examDb: ExamDao

    @MockK(relaxUnitFun = true)
    private lateinit var refreshHelper: AutoRefreshHelper

    private val semester = getSemesterEntity()

    private val student = getStudentEntity()

    private lateinit var examRepository: ExamRepository

    private val remoteList = listOf(
        getExam(LocalDate.of(2021, 1, 4)),
        getExam(LocalDate.of(2021, 1, 7))
    )

    private val startDate = LocalDate.of(2021, 1, 4)

    private val endDate = LocalDate.of(2021, 1, 10)

    private val realEndDate = LocalDate.of(2021, 1, 31)

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        every { refreshHelper.shouldBeRefreshed(any()) } returns false

        examRepository = ExamRepository(examDb, sdk, refreshHelper)
    }

    @Test
    fun `force refresh without difference`() {
        // prepare
        coEvery { sdk.getExams(startDate, realEndDate, 1) } returns remoteList
        coEvery { examDb.loadAll(1, 1, startDate, realEndDate) } returnsMany listOf(
            flowOf(remoteList.mapToEntities(semester)),
            flowOf(remoteList.mapToEntities(semester))
        )
        coEvery { examDb.insertAll(any()) } returns listOf(1, 2, 3)
        coEvery { examDb.deleteAll(any()) } just Runs

        // execute
        val res = runBlocking { examRepository.getExams(student, semester, startDate, endDate, true).toFirstResult() }

        // verify
        assertEquals(null, res.error)
        assertEquals(2, res.data?.size)
        coVerify { sdk.getExams(startDate, realEndDate, 1) }
        coVerify { examDb.loadAll(1, 1, startDate, realEndDate) }
        coVerify { examDb.insertAll(match { it.isEmpty() }) }
        coVerify { examDb.deleteAll(match { it.isEmpty() }) }
    }

    @Test
    fun `force refresh with more items in remote`() {
        // prepare
        coEvery { sdk.getExams(startDate, realEndDate, 1) } returns remoteList
        coEvery { examDb.loadAll(1, 1, startDate, realEndDate) } returnsMany listOf(
            flowOf(remoteList.dropLast(1).mapToEntities(semester)),
            flowOf(remoteList.dropLast(1).mapToEntities(semester)), // after fetch end before save result
            flowOf(remoteList.mapToEntities(semester))
        )
        coEvery { examDb.insertAll(any()) } returns listOf(1, 2, 3)
        coEvery { examDb.deleteAll(any()) } just Runs

        // execute
        val res = runBlocking { examRepository.getExams(student, semester, startDate, endDate, true).toFirstResult() }

        // verify
        assertEquals(null, res.error)
        assertEquals(2, res.data?.size)
        coVerify { sdk.getExams(startDate, realEndDate, 1) }
        coVerify { examDb.loadAll(1, 1, startDate, realEndDate) }
        coVerify {
            examDb.insertAll(match {
                it.size == 1 && it[0] == remoteList.mapToEntities(semester)[1]
            })
        }
        coVerify { examDb.deleteAll(match { it.isEmpty() }) }
    }

    @Test
    fun `force refresh with more items in local`() {
        // prepare
        coEvery { sdk.getExams(startDate, realEndDate, 1) } returns remoteList.dropLast(1)
        coEvery { examDb.loadAll(1, 1, startDate, realEndDate) } returnsMany listOf(
            flowOf(remoteList.mapToEntities(semester)),
            flowOf(remoteList.mapToEntities(semester)), // after fetch end before save result
            flowOf(remoteList.dropLast(1).mapToEntities(semester))
        )
        coEvery { examDb.insertAll(any()) } returns listOf(1, 2, 3)
        coEvery { examDb.deleteAll(any()) } just Runs

        // execute
        val res = runBlocking { examRepository.getExams(student, semester, startDate, endDate, true).toFirstResult() }

        // verify
        assertEquals(null, res.error)
        assertEquals(1, res.data?.size)
        coVerify { sdk.getExams(startDate, realEndDate, 1) }
        coVerify { examDb.loadAll(1, 1, startDate, realEndDate) }
        coVerify { examDb.insertAll(match { it.isEmpty() }) }
        coVerify {
            examDb.deleteAll(match {
                it.size == 1 && it[0] == remoteList.mapToEntities(semester)[1]
            })
        }
    }

    private fun getExam(date: LocalDate) = SdkExam(
        subject = "",
        group = "",
        type = "",
        description = "",
        teacher = "",
        teacherSymbol = "",
        date = date,
        entryDate = date
    )
}
