package io.github.wulkanowy.data.repositories

import io.github.wulkanowy.data.db.dao.CompletedLessonsDao
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
import java.time.LocalDate.of
import io.github.wulkanowy.sdk.pojo.CompletedLesson as SdkCompletedLesson

class CompletedLessonsRepositoryTest {

    @SpyK
    private var sdk = Sdk()

    @MockK
    private lateinit var completedLessonDb: CompletedLessonsDao

    @MockK(relaxUnitFun = true)
    private lateinit var refreshHelper: AutoRefreshHelper

    private val semester = getSemesterEntity()

    private val student = getStudentEntity()

    private lateinit var completedLessonRepository: CompletedLessonsRepository

    private val remoteList = listOf(
        getCompletedLesson(of(2021, 1, 4)),
        getCompletedLesson(of(2021, 1, 7))
    )

    private val startDate = of(2021, 1, 4)

    private val endDate = of(2021, 1, 10)

    @Before
    fun initApi() {
        MockKAnnotations.init(this)
        every { refreshHelper.shouldBeRefreshed(any()) } returns false

        completedLessonRepository = CompletedLessonsRepository(completedLessonDb, sdk, refreshHelper)
    }

    @Test
    fun `force refresh without difference`() {
        // prepare
        coEvery { sdk.getCompletedLessons(startDate, endDate) } returns remoteList
        coEvery { completedLessonDb.loadAll(1, 1, startDate, endDate) } returnsMany listOf(
            flowOf(remoteList.mapToEntities(semester)),
            flowOf(remoteList.mapToEntities(semester))
        )
        coEvery { completedLessonDb.insertAll(any()) } returns listOf(1, 2, 3)
        coEvery { completedLessonDb.deleteAll(any()) } just Runs

        // execute
        val res = runBlocking { completedLessonRepository.getCompletedLessons(student, semester, startDate, endDate, true).toFirstResult() }

        // verify
        assertEquals(null, res.error)
        assertEquals(2, res.data?.size)
        coVerify { sdk.getCompletedLessons(startDate, endDate) }
        coVerify { completedLessonDb.loadAll(1, 1, startDate, endDate) }
        coVerify { completedLessonDb.insertAll(match { it.isEmpty() }) }
        coVerify { completedLessonDb.deleteAll(match { it.isEmpty() }) }
    }

    @Test
    fun `force refresh with more items in remote`() {
        // prepare
        coEvery { sdk.getCompletedLessons(startDate, endDate) } returns remoteList
        coEvery { completedLessonDb.loadAll(1, 1, startDate, endDate) } returnsMany listOf(
            flowOf(remoteList.dropLast(1).mapToEntities(semester)),
            flowOf(remoteList.dropLast(1).mapToEntities(semester)), // after fetch end before save result
            flowOf(remoteList.mapToEntities(semester))
        )
        coEvery { completedLessonDb.insertAll(any()) } returns listOf(1, 2, 3)
        coEvery { completedLessonDb.deleteAll(any()) } just Runs

        // execute
        val res = runBlocking { completedLessonRepository.getCompletedLessons(student, semester, startDate, endDate, true).toFirstResult() }

        // verify
        assertEquals(null, res.error)
        assertEquals(2, res.data?.size)
        coVerify { sdk.getCompletedLessons(startDate, endDate) }
        coVerify { completedLessonDb.loadAll(1, 1, startDate, endDate) }
        coVerify {
            completedLessonDb.insertAll(match {
                it.size == 1 && it[0] == remoteList.mapToEntities(semester)[1]
            })
        }
        coVerify { completedLessonDb.deleteAll(match { it.isEmpty() }) }
    }

    @Test
    fun `force refresh with more items in local`() {
        // prepare
        coEvery { sdk.getCompletedLessons(startDate, endDate) } returns remoteList.dropLast(1)
        coEvery { completedLessonDb.loadAll(1, 1, startDate, endDate) } returnsMany listOf(
            flowOf(remoteList.mapToEntities(semester)),
            flowOf(remoteList.mapToEntities(semester)), // after fetch end before save result
            flowOf(remoteList.dropLast(1).mapToEntities(semester))
        )
        coEvery { completedLessonDb.insertAll(any()) } returns listOf(1, 2, 3)
        coEvery { completedLessonDb.deleteAll(any()) } just Runs

        // execute
        val res = runBlocking { completedLessonRepository.getCompletedLessons(student, semester, startDate, endDate, true).toFirstResult() }

        // verify
        assertEquals(null, res.error)
        assertEquals(1, res.data?.size)
        coVerify { sdk.getCompletedLessons(startDate, endDate) }
        coVerify { completedLessonDb.loadAll(1, 1, startDate, endDate) }
        coVerify { completedLessonDb.insertAll(match { it.isEmpty() }) }
        coVerify {
            completedLessonDb.deleteAll(match {
                it.size == 1 && it[0] == remoteList.mapToEntities(semester)[1]
            })
        }
    }

    private fun getCompletedLesson(date: LocalDate) = SdkCompletedLesson(
        date = date,
        subject = "",
        absence = "",
        resources = "",
        substitution = "",
        teacherSymbol = "",
        teacher = "",
        topic = "",
        number = 1
    )
}
