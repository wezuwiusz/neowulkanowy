package io.github.wulkanowy.data.repositories.gradestatistics

import io.github.wulkanowy.api.Api
import io.github.wulkanowy.api.grades.GradePointsSummary
import io.github.wulkanowy.api.grades.GradeStatistics
import io.github.wulkanowy.data.db.entities.Semester
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.SpyK
import io.reactivex.Single
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GradeStatisticsRemoteTest {

    @SpyK
    private var mockApi = Api()

    @MockK
    private lateinit var semesterMock: Semester

    @Before
    fun initApi() {
        MockKAnnotations.init(this)
    }

    @Test
    fun getGradeStatisticsTest() {
        every { mockApi.getGradesPartialStatistics(1) } returns Single.just(listOf(
            getGradeStatistics("Fizyka"),
            getGradeStatistics("Matematyka")
        ))

        every { mockApi.diaryId } returns 1
        every { semesterMock.studentId } returns 1
        every { semesterMock.semesterId } returns 1
        every { semesterMock.semesterName } returns 2
        every { semesterMock.diaryId } returns 1

        val stats = GradeStatisticsRemote(mockApi).getGradeStatistics(semesterMock, false).blockingGet()
        assertEquals(2, stats.size)
    }

    @Test
    fun getGradePointsStatisticsTest() {
        every { mockApi.getGradesPointsStatistics(1) } returns Single.just(listOf(
            getGradePointsStatistics("Fizyka"),
            getGradePointsStatistics("Matematyka")
        ))

        every { mockApi.diaryId } returns 1
        every { semesterMock.studentId } returns 1
        every { semesterMock.semesterId } returns 1
        every { semesterMock.semesterName } returns 2
        every { semesterMock.diaryId } returns 1

        val stats = GradeStatisticsRemote(mockApi).getGradePointsStatistics(semesterMock).blockingGet()
        assertEquals(2, stats.size)
    }

    private fun getGradeStatistics(subjectName: String): GradeStatistics {
        return GradeStatistics().apply {
            subject = subjectName
            gradeValue = 5
            amount = 10
        }
    }

    private fun getGradePointsStatistics(subjectName: String): GradePointsSummary {
        return GradePointsSummary(
            subject = subjectName,
            student = 0.80,
            others = 0.40
        )
    }
}
