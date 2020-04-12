package io.github.wulkanowy.data.repositories.timetable

import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.repositories.getStudentEntity
import io.github.wulkanowy.sdk.Sdk
import io.github.wulkanowy.sdk.pojo.Timetable
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.SpyK
import io.reactivex.Single
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDate.of
import org.threeten.bp.LocalDateTime.now

class TimetableRemoteTest {

    @SpyK
    private var mockSdk = Sdk()

    @MockK
    private lateinit var semesterMock: Semester

    private val student = getStudentEntity()

    @Before
    fun initApi() {
        MockKAnnotations.init(this)
    }

    @Test
    fun getTimetableTest() {
        every {
            mockSdk.getTimetable(
                of(2018, 9, 10),
                of(2018, 9, 15)
            )
        } returns Single.just(listOf(
            getTimetable(of(2018, 9, 10)),
            getTimetable(of(2018, 9, 17))
        ))

        every { semesterMock.studentId } returns 1
        every { semesterMock.diaryId } returns 1
        every { semesterMock.schoolYear } returns 2019
        every { semesterMock.semesterId } returns 1
        every { mockSdk.switchDiary(any(), any()) } returns mockSdk

        val timetable = TimetableRemote(mockSdk).getTimetable(student, semesterMock,
            of(2018, 9, 10),
            of(2018, 9, 15)
        ).blockingGet()
        assertEquals(2, timetable.size)
    }

    private fun getTimetable(date: LocalDate): Timetable {
        return Timetable(
            date = date,
            number = 0,
            teacherOld = "",
            subjectOld = "",
            roomOld = "",
            subject = "",
            teacher = "",
            group = "",
            canceled = false,
            changes = false,
            info = "",
            room = "",
            end = now(),
            start = now(),
            studentPlan = true
        )
    }
}
