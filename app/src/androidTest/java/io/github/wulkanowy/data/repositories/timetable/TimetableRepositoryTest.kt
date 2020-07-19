package io.github.wulkanowy.data.repositories.timetable

import android.os.Build.VERSION_CODES.P
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SdkSuppress
import io.github.wulkanowy.data.Status
import io.github.wulkanowy.data.db.AppDatabase
import io.github.wulkanowy.data.repositories.getSemester
import io.github.wulkanowy.data.repositories.getStudent
import io.github.wulkanowy.services.alarm.TimetableNotificationSchedulerHelper
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime.of
import kotlin.test.assertEquals

@SdkSuppress(minSdkVersion = P)
@RunWith(AndroidJUnit4::class)
class TimetableRepositoryTest {

    @MockK(relaxed = true)
    private lateinit var timetableNotificationSchedulerHelper: TimetableNotificationSchedulerHelper

    @MockK
    private lateinit var timetableRemote: TimetableRemote

    private lateinit var timetableLocal: TimetableLocal

    private lateinit var testDb: AppDatabase

    private val student = getStudent()

    private val semester = getSemester()

    @Before
    fun initApi() {
        MockKAnnotations.init(this)
        testDb = Room.inMemoryDatabaseBuilder(getApplicationContext(), AppDatabase::class.java).build()
        timetableLocal = TimetableLocal(testDb.timetableDao)
    }

    @After
    fun closeDb() {
        testDb.close()
    }

    @Test
    fun copyRoomToCompletedFromPrevious() {
        runBlocking {
            timetableLocal.saveTimetable(listOf(
                createTimetableLocal(of(2019, 3, 5, 8, 0), 1, "123", "Przyroda"),
                createTimetableLocal(of(2019, 3, 5, 8, 50), 2, "321", "Religia"),
                createTimetableLocal(of(2019, 3, 5, 9, 40), 3, "213", "W-F"),
                createTimetableLocal(of(2019, 3, 5, 10, 30), 3, "213", "W-F", "Jan Kowalski")
            ))
        }

        coEvery { timetableRemote.getTimetable(student, semester, any(), any()) } returns listOf(
            createTimetableLocal(of(2019, 3, 5, 8, 0), 1, "", "Przyroda"),
            createTimetableLocal(of(2019, 3, 5, 8, 50), 2, "", "Religia"),
            createTimetableLocal(of(2019, 3, 5, 9, 40), 3, "", "W-F"),
            createTimetableLocal(of(2019, 3, 5, 10, 30), 4, "", "W-F")
        )

        val lessons = runBlocking {
            TimetableRepository(timetableLocal, timetableRemote, timetableNotificationSchedulerHelper).getTimetable(
                student = student,
                semester = semester,
                start = LocalDate.of(2019, 3, 5),
                end = LocalDate.of(2019, 3, 5),
                forceRefresh = true
            ).filter { it.status == Status.SUCCESS }.first().data.orEmpty()
        }

        assertEquals(4, lessons.size)
        assertEquals("123", lessons[0].room)
        assertEquals("321", lessons[1].room)
        assertEquals("213", lessons[2].room)
    }

    @Test
    fun copyTeacherToCompletedFromPrevious() {
        val list = listOf(
            createTimetableLocal(of(2019, 12, 23, 8, 0), 1, "123", "Matematyka", "Paweł Poniedziałkowski", false),
            createTimetableLocal(of(2019, 12, 23, 8, 50), 2, "124", "Matematyka", "Paweł Poniedziałkowski", false),
            createTimetableLocal(of(2019, 12, 23, 9, 40), 3, "125", "Język polski", "Joanna Wtorkowska", true),
            createTimetableLocal(of(2019, 12, 23, 10, 40), 4, "126", "Język polski", "Joanna Wtorkowska", true),

            createTimetableLocal(of(2019, 12, 24, 8, 0), 1, "123", "Język polski", "Joanna Wtorkowska", false),
            createTimetableLocal(of(2019, 12, 24, 8, 50), 2, "124", "Język polski", "Joanna Wtorkowska", false),
            createTimetableLocal(of(2019, 12, 24, 9, 40), 3, "125", "Język polski", "Joanna Środowska", true),
            createTimetableLocal(of(2019, 12, 24, 10, 40), 4, "126", "Język polski", "Joanna Środowska", true),

            createTimetableLocal(of(2019, 12, 25, 8, 0), 1, "123", "Matematyka", "", false),
            createTimetableLocal(of(2019, 12, 25, 8, 50), 2, "124", "Matematyka", "", false),
            createTimetableLocal(of(2019, 12, 25, 9, 40), 3, "125", "Matematyka", "", true),
            createTimetableLocal(of(2019, 12, 25, 10, 40), 4, "126", "Matematyka", "", true)
        )
        runBlocking { timetableLocal.saveTimetable(list) }

        coEvery { timetableRemote.getTimetable(student, semester, any(), any()) } returns listOf(
            createTimetableLocal(of(2019, 12, 23, 8, 0), 1, "123", "Matematyka", "Paweł Poniedziałkowski", false),
            createTimetableLocal(of(2019, 12, 23, 8, 50), 2, "124", "Matematyka", "Jakub Wtorkowski", true),
            createTimetableLocal(of(2019, 12, 23, 9, 40), 3, "125", "Język polski", "Joanna Poniedziałkowska", false),
            createTimetableLocal(of(2019, 12, 23, 10, 40), 4, "126", "Język polski", "Joanna Wtorkowska", true),

            createTimetableLocal(of(2019, 12, 24, 8, 0), 1, "123", "Język polski", "", false),
            createTimetableLocal(of(2019, 12, 24, 8, 50), 2, "124", "Język polski", "", true),
            createTimetableLocal(of(2019, 12, 24, 9, 40), 3, "125", "Język polski", "", false),
            createTimetableLocal(of(2019, 12, 24, 10, 40), 4, "126", "Język polski", "", true),

            createTimetableLocal(of(2019, 12, 25, 8, 0), 1, "123", "Matematyka", "Paweł Środowski", false),
            createTimetableLocal(of(2019, 12, 25, 8, 50), 2, "124", "Matematyka", "Paweł Czwartkowski", true),
            createTimetableLocal(of(2019, 12, 25, 9, 40), 3, "125", "Matematyka", "Paweł Środowski", false),
            createTimetableLocal(of(2019, 12, 25, 10, 40), 4, "126", "Matematyka", "Paweł Czwartkowski", true)
        )

        val lessons = runBlocking {
            TimetableRepository(timetableLocal, timetableRemote, timetableNotificationSchedulerHelper).getTimetable(
                student = student,
                semester = semester,
                start = LocalDate.of(2019, 12, 23),
                end = LocalDate.of(2019, 12, 25),
                forceRefresh = true
            ).filter { it.status == Status.SUCCESS }.first().data.orEmpty()
        }

        assertEquals(12, lessons.size)

        assertEquals("Paweł Poniedziałkowski", lessons[0].teacher)
        assertEquals("Jakub Wtorkowski", lessons[1].teacher)
        assertEquals("Joanna Poniedziałkowska", lessons[2].teacher)
        assertEquals("Joanna Wtorkowska", lessons[3].teacher)

        assertEquals("Joanna Wtorkowska", lessons[4].teacher)
        assertEquals("", lessons[5].teacher)
        assertEquals("", lessons[6].teacher)
        assertEquals("", lessons[7].teacher)

        assertEquals("Paweł Środowski", lessons[8].teacher)
        assertEquals("Paweł Czwartkowski", lessons[9].teacher)
        assertEquals("Paweł Środowski", lessons[10].teacher)
        assertEquals("Paweł Czwartkowski", lessons[11].teacher)
    }
}
