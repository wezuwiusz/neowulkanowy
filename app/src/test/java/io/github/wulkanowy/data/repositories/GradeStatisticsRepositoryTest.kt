package io.github.wulkanowy.data.repositories

import io.github.wulkanowy.data.db.dao.GradePartialStatisticsDao
import io.github.wulkanowy.data.db.dao.GradePointsStatisticsDao
import io.github.wulkanowy.data.db.dao.GradeSemesterStatisticsDao
import io.github.wulkanowy.data.mappers.mapToEntities
import io.github.wulkanowy.getSemesterEntity
import io.github.wulkanowy.getStudentEntity
import io.github.wulkanowy.sdk.Sdk
import io.github.wulkanowy.sdk.pojo.GradeStatisticsItem
import io.github.wulkanowy.sdk.pojo.GradeStatisticsSubject
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

class GradeStatisticsRepositoryTest {

    @SpyK
    private var sdk = Sdk()

    @MockK
    private lateinit var gradePartialStatisticsDb: GradePartialStatisticsDao

    @MockK
    private lateinit var gradePointsStatisticsDb: GradePointsStatisticsDao

    @MockK
    private lateinit var gradeSemesterStatisticsDb: GradeSemesterStatisticsDao

    @MockK(relaxUnitFun = true)
    private lateinit var refreshHelper: AutoRefreshHelper

    private val semester = getSemesterEntity()

    private val student = getStudentEntity()

    private lateinit var gradeStatisticsRepository: GradeStatisticsRepository

    private val remotePartialList = listOf(
        getGradeStatisticsPartialSubject("Fizyka"),
        getGradeStatisticsPartialSubject("Matematyka")
    )

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        every { refreshHelper.shouldBeRefreshed(any()) } returns false

        gradeStatisticsRepository = GradeStatisticsRepository(gradePartialStatisticsDb, gradePointsStatisticsDb, gradeSemesterStatisticsDb, sdk, refreshHelper)
    }

    @Test
    fun `force refresh without difference`() {
        // prepare
        coEvery { sdk.getGradesPartialStatistics(1) } returns remotePartialList
        coEvery { gradePartialStatisticsDb.loadAll(1, 1) } returnsMany listOf(
            flowOf(remotePartialList.mapToEntities(semester)),
            flowOf(remotePartialList.mapToEntities(semester))
        )
        coEvery { gradePartialStatisticsDb.insertAll(any()) } returns listOf(1, 2, 3)
        coEvery { gradePartialStatisticsDb.deleteAll(any()) } just Runs

        // execute
        val res = runBlocking { gradeStatisticsRepository.getGradesPartialStatistics(student, semester, "Wszystkie", true).toFirstResult() }

        // verify
        assertEquals(null, res.error)
        assertEquals(2 + 1, res.data?.size)
        coVerify { sdk.getGradesPartialStatistics(1) }
        coVerify { gradePartialStatisticsDb.loadAll(1, 1) }
        coVerify { gradePartialStatisticsDb.insertAll(match { it.isEmpty() }) }
        coVerify { gradePartialStatisticsDb.deleteAll(match { it.isEmpty() }) }
    }

    private fun getGradeStatisticsPartialSubject(subjectName: String) = GradeStatisticsSubject(
        subject = subjectName,
        studentAverage = "",
        classAverage = "",
        classItems = listOf(
            GradeStatisticsItem(
                subject = subjectName,
                grade = 0,
                amount = 0
            )
        ),
        studentItems = listOf()
    )
}
