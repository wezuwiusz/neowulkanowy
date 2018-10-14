package io.github.wulkanowy.data.repositories.remote

import io.github.wulkanowy.api.Api
import io.github.wulkanowy.api.timetable.Timetable
import io.github.wulkanowy.data.db.entities.Semester
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.reactivex.Single
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.threeten.bp.LocalDate
import java.sql.Date

class TimetableRemoteTest {

    @MockK
    private lateinit var mockApi: Api

    @MockK
    private lateinit var semesterMock: Semester

    @Before
    fun initApi() {
        MockKAnnotations.init(this)
    }

    @Test
    fun getExamsTest() {
        every { mockApi.getTimetable(
                LocalDate.of(2018, 9, 10),
                LocalDate.of(2018, 9, 15)
        ) } returns Single.just(listOf(
                getTimetable("2018-09-10"),
                getTimetable("2018-09-17")
        ))

        every { mockApi.diaryId } returns "1"
        every { semesterMock.studentId } returns "1"
        every { semesterMock.diaryId } returns "1"

        val timetable = TimetableRemote(mockApi).getTimetable(semesterMock,
                LocalDate.of(2018, 9, 10),
                LocalDate.of(2018, 9, 15)
        ).blockingGet()
        assertEquals(2, timetable.size)
    }

    private fun getTimetable(dateString: String): Timetable {
        return Timetable(date = Date.valueOf(dateString))
    }
}
