package io.github.wulkanowy.data.repositories.gradestatistics

import io.github.wulkanowy.api.Api
import io.github.wulkanowy.api.grades.GradeStatistics
import io.github.wulkanowy.data.db.entities.Semester
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.SpyK
import io.reactivex.Single
import org.junit.Assert
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
        every { mockApi.getGradesStatistics(1, any()) } returns Single.just(listOf(
            getGradeStatistics("Fizyka"),
            getGradeStatistics("Matematyka")
        ))

        every { mockApi.diaryId } returns 1
        every { semesterMock.studentId } returns 1
        every { semesterMock.semesterId } returns 1
        every { semesterMock.semesterName } returns 2
        every { semesterMock.diaryId } returns 1

        val stats = GradeStatisticsRemote(mockApi).getGradeStatistics(semesterMock, false).blockingGet()
        Assert.assertEquals(2, stats.size)
    }

    private fun getGradeStatistics(subjectName: String): GradeStatistics {
        return GradeStatistics().apply {
            subject = subjectName
            gradeValue = 5
            amount = 10
        }
    }
}
