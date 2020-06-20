package io.github.wulkanowy.data.repositories.attendance

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.github.wulkanowy.data.db.AppDatabase
import io.github.wulkanowy.data.db.entities.Attendance
import io.github.wulkanowy.data.db.entities.Semester
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.threeten.bp.LocalDate
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
        val list = listOf(
            getAttendanceEntity(
                of(2018, 9, 10),
                SentExcuseStatus.ACCEPTED
            ),
            getAttendanceEntity(
                of(2018, 9, 14),
                SentExcuseStatus.WAITING
            ),
            getAttendanceEntity(
                of(2018, 9, 17),
                SentExcuseStatus.ACCEPTED
            )
        )
        runBlocking { attendanceLocal.saveAttendance(list) }

        val semester = Semester(1, 2, "", 1, 3, 2019, now(), now(), 1, 1)
        val attendance = runBlocking { attendanceLocal.getAttendance(semester, of(2018, 9, 10), of(2018, 9, 14)) }
        assertEquals(2, attendance.size)
        assertEquals(attendance[0].date, of(2018, 9, 10))
        assertEquals(attendance[1].date, of(2018, 9, 14))
    }

    private fun getAttendanceEntity(
        date: LocalDate,
        excuseStatus: SentExcuseStatus
    ) = Attendance(
        studentId = 1,
        diaryId = 2,
        timeId = 3,
        date = date,
        number = 0,
        subject = "",
        name = "",
        presence = false,
        absence = false,
        exemption = false,
        lateness = false,
        excused = false,
        deleted = false,
        excusable = false,
        excuseStatus = excuseStatus.name
    )
}
