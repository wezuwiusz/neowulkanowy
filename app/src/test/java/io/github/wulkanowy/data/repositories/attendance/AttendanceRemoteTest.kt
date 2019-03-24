package io.github.wulkanowy.data.repositories.attendance

import io.github.wulkanowy.api.Api
import io.github.wulkanowy.api.attendance.Attendance
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

class AttendanceRemoteTest {

    @SpyK
    private var mockApi = Api()

    @MockK
    private lateinit var semesterMock: Semester

    @Before
    fun initApi() {
        MockKAnnotations.init(this)
    }

    @Test
    fun getAttendanceTest() {
        every { mockApi.getAttendance(
                LocalDate.of(2018, 9, 10),
                LocalDate.of(2018, 9, 15)
        ) } returns Single.just(listOf(
                getAttendance("2018-09-10"),
                getAttendance("2018-09-17")
        ))

        every { mockApi.diaryId } returns 1
        every { semesterMock.studentId } returns 1
        every { semesterMock.diaryId } returns 1

        val attendance = AttendanceRemote(mockApi).getAttendance(semesterMock,
                LocalDate.of(2018, 9, 10),
                LocalDate.of(2018, 9, 15)).blockingGet()
        assertEquals(2, attendance.size)
    }

    private fun getAttendance(dateString: String): Attendance {
        return Attendance().apply {
            subject = "Fizyka"
            name = "Obecność"
            date = Date.valueOf(dateString)
        }
    }
}
