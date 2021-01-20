package io.github.wulkanowy.utils

import io.github.wulkanowy.data.db.entities.Timetable
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalDateTime.now

class TimetableExtensionTest {

    @Test
    fun isShowTimeUntil() {
        assertFalse(getTimetableEntity().isShowTimeUntil(null))
        assertFalse(getTimetableEntity(isStudentPlan = false).isShowTimeUntil(null))
        assertFalse(getTimetableEntity(isStudentPlan = true, canceled = true).isShowTimeUntil(null))
        assertFalse(getTimetableEntity(isStudentPlan = true, canceled = false, start = now().minusSeconds(1)).isShowTimeUntil(null))
        assertFalse(getTimetableEntity(isStudentPlan = true, canceled = false, start = now().plusMinutes(5)).isShowTimeUntil(now().plusMinutes(5)))
        assertFalse(getTimetableEntity(isStudentPlan = true, canceled = false, start = now().plusMinutes(61)).isShowTimeUntil(now().minusMinutes(5)))

        assertTrue(getTimetableEntity(isStudentPlan = true, canceled = false, start = now().plusMinutes(60)).isShowTimeUntil(now().minusMinutes(5)))
        assertTrue(getTimetableEntity(isStudentPlan = true, canceled = false, start = now().plusMinutes(60)).isShowTimeUntil(null))

        assertFalse(getTimetableEntity(isStudentPlan = true, canceled = false, start = now().minusSeconds(1)).isShowTimeUntil(null))
    }

    @Test
    fun getLeft() {
        assertEquals(null, getTimetableEntity(canceled = true).left)
        assertEquals(null, getTimetableEntity(start = now().plusMinutes(5), end = now().plusMinutes(50)).left)
        assertEquals(null, getTimetableEntity(start = now().minusMinutes(1), end = now().plusMinutes(44), isStudentPlan = false).left)
        assertNotEquals(null, getTimetableEntity(start = now().minusMinutes(1), end = now().plusMinutes(44), isStudentPlan = true).left)
    }

    @Test
    fun isJustFinished() {
        assertFalse(getTimetableEntity(end = now().minusSeconds(16)).isJustFinished)
        assertTrue(getTimetableEntity(end = now().minusSeconds(14)).isJustFinished)
        assertTrue(getTimetableEntity(end = now().minusSeconds(1)).isJustFinished)
        assertFalse(getTimetableEntity(end = now().plusSeconds(1)).isJustFinished)
    }

    private fun getTimetableEntity(
        isStudentPlan: Boolean = false,
        canceled: Boolean = false,
        start: LocalDateTime = now(),
        end: LocalDateTime = now()
    ) = Timetable(
        studentId = 0,
        subject = "",
        number = 0,
        diaryId = 0,
        canceled = canceled,
        changes = false,
        date = LocalDate.now(),
        end = end,
        group = "",
        info = "",
        isStudentPlan = isStudentPlan,
        room = "",
        roomOld = "",
        start = start,
        subjectOld = "",
        teacher = "",
        teacherOld = ""
    )
}
