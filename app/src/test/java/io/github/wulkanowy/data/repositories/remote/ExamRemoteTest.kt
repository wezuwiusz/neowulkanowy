package io.github.wulkanowy.data.repositories.remote

import io.github.wulkanowy.api.Api
import io.github.wulkanowy.api.exams.Exam
import io.github.wulkanowy.data.db.entities.Semester
import io.reactivex.Single
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.Mock
import org.mockito.Mockito.doReturn
import org.mockito.MockitoAnnotations
import org.threeten.bp.LocalDate
import java.sql.Date

class ExamRemoteTest {

    @Mock
    private lateinit var mockApi: Api

    @Mock
    private lateinit var semesterMock: Semester

    @Before
    fun initApi() {
        MockitoAnnotations.initMocks(this)
    }

    @Test
    fun getExamsTest() {
        doReturn(Single.just(listOf(
                getExam("2018-09-10"),
                getExam("2018-09-17")
        ))).`when`(mockApi).getExams(any())

        doReturn("1").`when`(semesterMock).studentId
        doReturn("1").`when`(semesterMock).diaryId

        val exams = ExamRemote(mockApi).getExams(semesterMock, LocalDate.of(2018, 9, 10)).blockingGet()
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
