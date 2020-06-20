package io.github.wulkanowy.data.repositories.gradestatistics

import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.getStudentEntity
import io.github.wulkanowy.sdk.Sdk
import io.github.wulkanowy.sdk.pojo.GradePointsStatistics
import io.github.wulkanowy.sdk.pojo.GradeStatistics
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
            getGradeStatistics("Fizyka"),
            getGradeStatistics("Matematyka")
        )

        every { semesterMock.studentId } returns 1
        every { semesterMock.diaryId } returns 1
        every { semesterMock.schoolYear } returns 2019
        every { semesterMock.semesterId } returns 1
        every { mockSdk.switchDiary(any(), any()) } returns mockSdk

        val stats = runBlocking { GradeStatisticsRemote(mockSdk).getGradeStatistics(student, semesterMock, false) }
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

    private fun getGradeStatistics(subjectName: String): GradeStatistics {
        return GradeStatistics(
            subject = subjectName,
            gradeValue = 5,
            amount = 10,
            grade = "",
            semesterId = 1
        )
    }

    private fun getGradePointsStatistics(subjectName: String): GradePointsStatistics {
        return GradePointsStatistics(
            semesterId = 1,
            subject = subjectName,
            student = 0.80,
            others = 0.40
        )
    }
}
