package io.github.wulkanowy.data.repositories

import io.github.wulkanowy.data.dataOrNull
import io.github.wulkanowy.data.db.dao.GradePartialStatisticsDao
import io.github.wulkanowy.data.db.dao.GradePointsStatisticsDao
import io.github.wulkanowy.data.db.dao.GradeSemesterStatisticsDao
import io.github.wulkanowy.data.errorOrNull
import io.github.wulkanowy.data.mappers.mapToEntities
import io.github.wulkanowy.data.toFirstResult
import io.github.wulkanowy.getSemesterEntity
import io.github.wulkanowy.getStudentEntity
import io.github.wulkanowy.sdk.Sdk
import io.github.wulkanowy.sdk.pojo.GradeStatisticsItem
import io.github.wulkanowy.sdk.pojo.GradeStatisticsSubject
import io.github.wulkanowy.utils.AutoRefreshHelper
import io.mockk.*
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.SpyK
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

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        every { refreshHelper.shouldBeRefreshed(any()) } returns false

        gradeStatisticsRepository = GradeStatisticsRepository(
            gradePartialStatisticsDb = gradePartialStatisticsDb,
            gradePointsStatisticsDb = gradePointsStatisticsDb,
            gradeSemesterStatisticsDb = gradeSemesterStatisticsDb,
            sdk = sdk,
            refreshHelper = refreshHelper,
        )
    }

    @Test
    fun `force refresh without difference`() {
        // prepare
        val remotePartialList = listOf(
            getGradeStatisticsPartialSubject("Fizyka"),
            getGradeStatisticsPartialSubject("Matematyka")
        )
        coEvery { sdk.getGradesPartialStatistics(1) } returns remotePartialList
        coEvery { gradePartialStatisticsDb.loadAll(1, 1) } returnsMany listOf(
            flowOf(remotePartialList.mapToEntities(semester)),
            flowOf(remotePartialList.mapToEntities(semester))
        )
        coEvery { gradePartialStatisticsDb.insertAll(any()) } returns listOf(1, 2, 3)
        coEvery { gradePartialStatisticsDb.deleteAll(any()) } just Runs

        // execute
        val res = runBlocking {
            gradeStatisticsRepository.getGradesPartialStatistics(
                student = student,
                semester = semester,
                subjectName = "Wszystkie",
                forceRefresh = true,
            ).toFirstResult()
        }
        val items = res.dataOrNull.orEmpty()

        // verify
        assertEquals(null, res.errorOrNull)
        assertEquals(2 + 1, res.dataOrNull?.size)
        assertEquals("", items[0].partial?.studentAverage)
        assertEquals("", items[1].partial?.studentAverage)
        assertEquals("", items[2].partial?.studentAverage)
        coVerify { sdk.getGradesPartialStatistics(1) }
        coVerify { gradePartialStatisticsDb.loadAll(1, 1) }
        coVerify { gradePartialStatisticsDb.insertAll(match { it.isEmpty() }) }
        coVerify { gradePartialStatisticsDb.deleteAll(match { it.isEmpty() }) }
    }

    @Test
    fun `force refresh without difference with filled up items`() {
        // prepare
        val remotePartialList = listOf(
            getGradeStatisticsPartialSubject("Fizyka", "1.0"),
            getGradeStatisticsPartialSubject("Matematyka", "5.0")
        )
        coEvery { sdk.getGradesPartialStatistics(1) } returns remotePartialList
        coEvery { gradePartialStatisticsDb.loadAll(1, 1) } returnsMany listOf(
            flowOf(remotePartialList.mapToEntities(semester)),
            flowOf(remotePartialList.mapToEntities(semester))
        )
        coEvery { gradePartialStatisticsDb.insertAll(any()) } returns listOf(1, 2, 3)
        coEvery { gradePartialStatisticsDb.deleteAll(any()) } just Runs

        // execute
        val res = runBlocking {
            gradeStatisticsRepository.getGradesPartialStatistics(
                student = student,
                semester = semester,
                subjectName = "Wszystkie",
                forceRefresh = true,
            ).toFirstResult()
        }
        val items = res.dataOrNull.orEmpty()

        // verify
        assertEquals(null, res.errorOrNull)
        assertEquals(2 + 1, res.dataOrNull?.size)
        assertEquals("3,00", items[0].partial?.studentAverage)
        assertEquals("1.0", items[1].partial?.studentAverage)
        assertEquals("5.0", items[2].partial?.studentAverage)
        coVerify { sdk.getGradesPartialStatistics(1) }
        coVerify { gradePartialStatisticsDb.loadAll(1, 1) }
        coVerify { gradePartialStatisticsDb.insertAll(match { it.isEmpty() }) }
        coVerify { gradePartialStatisticsDb.deleteAll(match { it.isEmpty() }) }
    }

    private fun getGradeStatisticsPartialSubject(
        subjectName: String,
        studentAverage: String = "",
        classAverage: String = "",
    ) = GradeStatisticsSubject(
        subject = subjectName,
        studentAverage = studentAverage,
        classAverage = classAverage,
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
