package io.github.wulkanowy.utils

import io.github.wulkanowy.data.db.entities.Timetable
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.*
import java.time.Duration.ofMinutes

class TimetableExtensionTest {

    @Test
    fun isShowTimeUntil() {
        assertFalse(getTimetableEntity().isShowTimeUntil(null))
        assertFalse(getTimetableEntity(isStudentPlan = false).isShowTimeUntil(null))
        assertFalse(getTimetableEntity(isStudentPlan = true, canceled = true).isShowTimeUntil(null))
        assertFalse(getTimetableEntity(isStudentPlan = true, canceled = false, start = Instant.now().minusSeconds(1)).isShowTimeUntil(null))
        assertFalse(getTimetableEntity(isStudentPlan = true, canceled = false, start = Instant.now().plus(ofMinutes(5))).isShowTimeUntil(Instant.now().plus(ofMinutes(5))))
        assertFalse(getTimetableEntity(isStudentPlan = true, canceled = false, start = Instant.now().plus(ofMinutes(61))).isShowTimeUntil(Instant.now().minus(ofMinutes(5))))

        assertTrue(getTimetableEntity(isStudentPlan = true, canceled = false, start = Instant.now().plus(ofMinutes(60))).isShowTimeUntil(Instant.now().minus(ofMinutes(5))))
        assertTrue(getTimetableEntity(isStudentPlan = true, canceled = false, start = Instant.now().plus(ofMinutes(60))).isShowTimeUntil(null))

        assertFalse(getTimetableEntity(isStudentPlan = true, canceled = false, start = Instant.now().minusSeconds(1)).isShowTimeUntil(null))
    }

    @Test
    fun getLeft() {
        assertEquals(null, getTimetableEntity(canceled = true).left)
        assertEquals(null, getTimetableEntity(start = Instant.now().plus(ofMinutes(5)), end = Instant.now().plus(ofMinutes(50))).left)
        assertEquals(null, getTimetableEntity(start = Instant.now().minus(ofMinutes(1)), end = Instant.now().plus(ofMinutes(44)), isStudentPlan = false).left)
        assertNotEquals(null, getTimetableEntity(start = Instant.now().minus(ofMinutes(1)), end = Instant.now().plus(ofMinutes(44)), isStudentPlan = true).left)
        assertNotEquals(null, getTimetableEntity(start = Instant.now(), end = Instant.now().plus(ofMinutes(45)), isStudentPlan = true).left)
    }

    @Test
    fun isJustFinished() {
        assertFalse(getTimetableEntity(end = Instant.now().minusSeconds(16)).isJustFinished)
        assertTrue(getTimetableEntity(end = Instant.now().minusSeconds(14)).isJustFinished)
        assertTrue(getTimetableEntity(end = Instant.now().minusSeconds(1)).isJustFinished)
        assertFalse(getTimetableEntity(end = Instant.now().plusSeconds(1)).isJustFinished)
    }

    private fun getTimetableEntity(
        isStudentPlan: Boolean = false,
        canceled: Boolean = false,
        start: Instant = Instant.now(),
        end: Instant = Instant.now()
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
