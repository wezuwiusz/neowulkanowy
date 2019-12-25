package io.github.wulkanowy.data.repositories.exam

import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.sdk.Sdk
import io.github.wulkanowy.sdk.pojo.Exam
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.reactivex.Single
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDate.of

class ExamRemoteTest {

    @MockK
    private lateinit var mockSdk: Sdk

    @MockK
    private lateinit var semesterMock: Semester

    @Before
    fun initApi() {
        MockKAnnotations.init(this)
    }

    @Test
    fun getExamsTest() {
        every {
            mockSdk.getExams(
                of(2018, 9, 10),
                of(2018, 9, 15),
                1
            )
        } returns Single.just(listOf(
            getExam(of(2018, 9, 10)),
            getExam(of(2018, 9, 17))
        ))

        every { semesterMock.studentId } returns 1
        every { semesterMock.diaryId } returns 1
        every { semesterMock.schoolYear } returns 2019
        every { semesterMock.semesterId } returns 1
        every { mockSdk.switchDiary(any(), any()) } returns mockSdk

        val exams = ExamRemote(mockSdk)
            .getExams(semesterMock,
                of(2018, 9, 10),
                of(2018, 9, 15)
            ).blockingGet()
        assertEquals(2, exams.size)
    }

    private fun getExam(date: LocalDate): Exam {
        return Exam(
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
}
