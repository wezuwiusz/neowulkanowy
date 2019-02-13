package io.github.wulkanowy.data.repositories.local

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.github.wulkanowy.data.db.AppDatabase
import io.github.wulkanowy.data.db.entities.Attendance
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.repositories.attendance.AttendanceLocal
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
        testDb = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), AppDatabase::class.java).build()
        attendanceLocal = AttendanceLocal(testDb.attendanceDao)
    }

    @After
    fun closeDb() {
        testDb.close()
    }

    @Test
    fun saveAndReadTest() {
        attendanceLocal.saveAttendance(listOf(
                Attendance(1, 2, LocalDate.of(2018, 9, 10), 0, "", ""),
                Attendance(1, 2, LocalDate.of(2018, 9, 14), 0, "", ""),
                Attendance(1, 2, LocalDate.of(2018, 9, 17), 0, "", "")
        ))

        val attendance = attendanceLocal
                .getAttendance(Semester(1, 1, 2, "", 3, 1),
                        LocalDate.of(2018, 9, 10),
                        LocalDate.of(2018, 9, 14)
                )
                .blockingGet()
        assertEquals(2, attendance.size)
        assertEquals(attendance[0].date, LocalDate.of(2018, 9, 10))
        assertEquals(attendance[1].date, LocalDate.of(2018, 9, 14))
    }
}
