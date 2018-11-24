package io.github.wulkanowy.data.repositories.remote

import io.github.wulkanowy.api.Api
import io.github.wulkanowy.api.exams.Exam
import io.github.wulkanowy.data.db.entities.Semester
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.SpyK
import io.reactivex.Single
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.threeten.bp.LocalDate
import java.sql.Date

class ExamRemoteTest {

    @SpyK
    private var mockApi = Api()

    @MockK
    private lateinit var semesterMock: Semester

    @Before
    fun initApi() {
        MockKAnnotations.init(this)
    }

    @Test
    fun getExamsTest() {
        every { mockApi.getExams(
                LocalDate.of(2018, 9, 10),
                LocalDate.of(2018, 9, 15)
        ) } returns Single.just(listOf(
                getExam("2018-09-10"),
                getExam("2018-09-17")
        ))

        every { mockApi.diaryId } returns 1
        every { semesterMock.studentId } returns 1
        every { semesterMock.diaryId } returns 1

        val exams = ExamRemote(mockApi).getExams(semesterMock,
                LocalDate.of(2018, 9, 10),
                LocalDate.of(2018, 9, 15)
        ).blockingGet()
        assertEquals(2, exams.size)
    }

    private fun getExam(dateString: String): Exam {
        return Exam().apply {
            subject = ""
            group = ""
            type = ""
            description = ""
            teacher = ""
            teacherSymbol = ""
            date = Date.valueOf(dateString)
            entryDate = Date.valueOf(dateString)
        }
    }
}
