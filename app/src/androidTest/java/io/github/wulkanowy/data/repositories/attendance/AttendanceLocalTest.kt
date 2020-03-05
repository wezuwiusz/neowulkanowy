package io.github.wulkanowy.data.repositories.attendance

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.github.wulkanowy.data.db.AppDatabase
import io.github.wulkanowy.data.db.entities.Attendance
import io.github.wulkanowy.data.db.entities.Semester
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.threeten.bp.LocalDate.now
import org.threeten.bp.LocalDate.of
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
            Attendance(1, 2, 3, of(2018, 9, 10), 0, "", "", false, false, false, false, false, false, false, SentExcuseStatus.ACCEPTED.name),
            Attendance(1, 2, 3, of(2018, 9, 14), 0, "", "", false, false, false, false, false, false, false, SentExcuseStatus.WAITING.name),
            Attendance(1, 2, 3, of(2018, 9, 17), 0, "", "", false, false, false, false, false, false, false, SentExcuseStatus.ACCEPTED.name)
        ))

        val attendance = attendanceLocal
            .getAttendance(Semester(1, 2, "", 1, 3, 2019, now(), now(), 1, 1),
                of(2018, 9, 10),
                of(2018, 9, 14)
            )
            .blockingGet()
        assertEquals(2, attendance.size)
        assertEquals(attendance[0].date, of(2018, 9, 10))
        assertEquals(attendance[1].date, of(2018, 9, 14))
    }
}
