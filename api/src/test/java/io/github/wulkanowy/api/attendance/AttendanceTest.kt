package io.github.wulkanowy.api.attendance

import io.github.wulkanowy.api.StudentAndParentTestCase
import org.junit.Assert.*
import org.junit.Test

class AttendanceTest : StudentAndParentTestCase() {

    private val full by lazy { Attendance(getSnp("Frekwencja-full.html")) }

    private val excellent by lazy { Attendance(getSnp("Frekwencja-excellent.html")) }

    @Test fun getAttendanceFull() {
        assertTrue(full.getAttendance().isNotEmpty())
        assertEquals(38, full.getAttendance().size)
    }

    @Test fun getAttendanceExcellent() {
        assertTrue(excellent.getAttendance().isNotEmpty())
        assertEquals(22, excellent.getAttendance().size)
    }

    @Test fun getLessonSubject() {
        assertEquals("Uroczyste rozpoczęcie roku szkolnego 2015/2016", excellent.getAttendance()[0].subject)
        assertEquals("Geografia", excellent.getAttendance()[11].subject)

        assertEquals("Naprawa komputera", full.getAttendance()[14].subject)
        assertEquals("Religia", full.getAttendance()[23].subject)
        assertEquals("Metodologia programowania", full.getAttendance()[34].subject)
    }

    @Test fun getLessonIsPresence() {
        assertTrue(excellent.getAttendance()[0].presence)
        assertTrue(excellent.getAttendance()[15].presence)

        assertTrue(full.getAttendance()[0].presence)
        assertTrue(full.getAttendance()[21].presence)
        assertFalse(full.getAttendance()[36].presence)
        assertFalse(full.getAttendance()[37].presence)
    }


    @Test fun getLessonIsAbsenceUnexcused() {
        assertFalse(excellent.getAttendance()[0].absenceUnexcused)

        assertTrue(full.getAttendance()[14].absenceUnexcused)
        assertFalse(full.getAttendance()[24].absenceUnexcused)
        assertFalse(full.getAttendance()[37].absenceUnexcused)
    }

    @Test fun getLessonIsAbsenceExcused() {
        assertFalse(excellent.getAttendance()[0].absenceExcused)

        assertFalse(full.getAttendance()[5].absenceExcused)
        assertFalse(full.getAttendance()[10].absenceExcused)
        assertTrue(full.getAttendance()[36].absenceExcused)
    }

    @Test fun getLessonIsAbsenceForSchoolReasons() {
        assertFalse(excellent.getAttendance()[6].absenceForSchoolReasons)

        assertTrue(full.getAttendance()[19].absenceForSchoolReasons)
        assertFalse(full.getAttendance()[0].absenceForSchoolReasons)
        assertFalse(full.getAttendance()[37].absenceForSchoolReasons)
    }

    @Test fun getLessonIsUnexcusedLateness() {
        assertFalse(excellent.getAttendance()[7].unexcusedLateness)

        assertTrue(full.getAttendance()[12].unexcusedLateness)
        assertFalse(full.getAttendance()[13].unexcusedLateness)
        assertFalse(full.getAttendance()[36].unexcusedLateness)
    }

    @Test fun getLessonIsExcusedLateness() {
        assertFalse(excellent.getAttendance()[8].excusedLateness)

        assertTrue(full.getAttendance()[13].excusedLateness)
        assertFalse(full.getAttendance()[14].excusedLateness)
        assertFalse(full.getAttendance()[35].excusedLateness)
    }

    @Test fun getLessonIsExemption() {
        assertFalse(excellent.getAttendance()[9].exemption)

        assertFalse(full.getAttendance()[0].exemption)
        assertFalse(full.getAttendance()[15].exemption)
        assertTrue(full.getAttendance()[37].exemption)
    }
}
