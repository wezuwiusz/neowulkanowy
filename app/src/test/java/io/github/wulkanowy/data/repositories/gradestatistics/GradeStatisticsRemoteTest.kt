package io.github.wulkanowy.data.repositories.gradestatistics

import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.getStudentEntity
import io.github.wulkanowy.sdk.Sdk
import io.github.wulkanowy.sdk.pojo.GradePointsStatistics
import io.github.wulkanowy.sdk.pojo.GradeStatisticsItem
import io.github.wulkanowy.sdk.pojo.GradeStatisticsSubject
import io.github.wulkanowy.utils.init
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.SpyK
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class GradeStatisticsRemoteTest {

    @SpyK
    private var mockSdk = Sdk()

    @MockK
    private lateinit var semesterMock: Semester

    private val student = getStudentEntity()

    @Before
    fun initApi() {
        MockKAnnotations.init(this)
        every { mockSdk.init(student) } returns mockSdk
    }

    @Test
    fun getGradeStatisticsTest() {
        coEvery { mockSdk.getGradesPartialStatistics(1) } returns listOf(
            getGradeStatisticsPartialSubject("Fizyka"),
            getGradeStatisticsPartialSubject("Matematyka")
        )

        every { semesterMock.studentId } returns 1
        every { semesterMock.diaryId } returns 1
        every { semesterMock.schoolYear } returns 2019
        every { semesterMock.semesterId } returns 1
        every { mockSdk.switchDiary(any(), any()) } returns mockSdk

        val stats = runBlocking { GradeStatisticsRemote(mockSdk).getGradePartialStatistics(student, semesterMock) }
        assertEquals(2, stats.size)
    }

    @Test
    fun getGradePointsStatisticsTest() {
        coEvery { mockSdk.getGradesPointsStatistics(1) } returns listOf(
            getGradePointsStatistics("Fizyka"),
            getGradePointsStatistics("Matematyka")
        )

        every { semesterMock.studentId } returns 1
        every { semesterMock.diaryId } returns 1
        every { semesterMock.schoolYear } returns 2019
        every { semesterMock.semesterId } returns 1
        every { mockSdk.switchDiary(any(), any()) } returns mockSdk

        val stats = runBlocking { GradeStatisticsRemote(mockSdk).getGradePointsStatistics(student, semesterMock) }
        assertEquals(2, stats.size)
    }

    private fun getGradeStatisticsPartialSubject(subjectName: String): GradeStatisticsSubject {
        return GradeStatisticsSubject(
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

    private fun getGradePointsStatistics(subjectName: String): GradePointsStatistics {
        return GradePointsStatistics(
            subject = subjectName,
            student = 0.80,
            others = 0.40
        )
    }
}
