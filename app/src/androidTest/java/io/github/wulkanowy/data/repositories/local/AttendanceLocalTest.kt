package io.github.wulkanowy.data.repositories.local

import android.arch.persistence.room.Room
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import io.github.wulkanowy.data.db.AppDatabase
import io.github.wulkanowy.data.db.entities.Attendance
import io.github.wulkanowy.data.db.entities.Semester
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.threeten.bp.LocalDate
import kotlin.test.assertEquals

@RunWith(AndroidJUnit4::class)
class AttendanceLocalTest {

    private lateinit var attendanceLocal: AttendanceLocal

    private lateinit var testDb: AppDatabase

    @Before
    fun createDb() {
        testDb = Room.inMemoryDatabaseBuilder(InstrumentationRegistry.getContext(), AppDatabase::class.java).build()
        attendanceLocal = AttendanceLocal(testDb.attendanceDao())
    }

    @After
    fun closeDb() {
        testDb.close()
    }

    @Test
    fun saveAndReadTest() {
        attendanceLocal.saveAttendance(listOf(
                Attendance(studentId = "1", diaryId = "2", date = LocalDate.of(2018, 9, 10)),
                Attendance(studentId = "1", diaryId = "2", date = LocalDate.of(2018, 9, 14)),
                Attendance(studentId = "1", diaryId = "2", date = LocalDate.of(2018, 9, 17)) // in next week
        ))

        val attendance = attendanceLocal
                .getAttendance(Semester(studentId = "1", diaryId = "2", semesterId = "3"),
                        LocalDate.of(2018, 9, 10),
                        LocalDate.of(2018, 9, 14)
                )
                .blockingGet()
        assertEquals(2, attendance.size)
        assertEquals(attendance[0].date, LocalDate.of(2018, 9, 10))
        assertEquals(attendance[1].date, LocalDate.of(2018, 9, 14))
    }
}
