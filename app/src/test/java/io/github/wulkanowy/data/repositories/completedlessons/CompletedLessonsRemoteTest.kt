package io.github.wulkanowy.data.repositories.completedlessons

import io.github.wulkanowy.api.Api
import io.github.wulkanowy.api.timetable.CompletedLesson
import io.github.wulkanowy.data.db.entities.Semester
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.SpyK
import io.reactivex.Single
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.threeten.bp.LocalDate
import java.sql.Date

class CompletedLessonsRemoteTest {

    @SpyK
    private var mockApi = Api()

    @MockK
    private lateinit var semesterMock: Semester

    @Before
    fun initApi() {
        MockKAnnotations.init(this)
    }

    @Test
    fun getCompletedLessonsTest() {
        every {
            mockApi.getCompletedLessons(
                LocalDate.of(2018, 9, 10),
                LocalDate.of(2018, 9, 15)
            )
        } returns Single.just(listOf(
            getCompletedLesson("2018-09-10"),
            getCompletedLesson("2018-09-17")
        ))

        every { mockApi.diaryId } returns 1
        every { semesterMock.studentId } returns 1
        every { semesterMock.diaryId } returns 1

        val completed = CompletedLessonsRemote(mockApi).getCompletedLessons(semesterMock,
            LocalDate.of(2018, 9, 10),
            LocalDate.of(2018, 9, 15)
        ).blockingGet()
        Assert.assertEquals(2, completed.size)
    }

    private fun getCompletedLesson(dateString: String): CompletedLesson {
        return CompletedLesson().apply { date = Date.valueOf(dateString) }
    }
}
