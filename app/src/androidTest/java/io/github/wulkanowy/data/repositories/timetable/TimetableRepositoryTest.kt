package io.github.wulkanowy.data.repositories.timetable

import android.os.Build.VERSION_CODES.P
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SdkSuppress
import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.InternetObservingSettings
import io.github.wulkanowy.data.SdkHelper
import io.github.wulkanowy.data.db.AppDatabase
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.repositories.TestInternetObservingStrategy
import io.github.wulkanowy.sdk.Sdk
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.SpyK
import io.mockk.just
import io.mockk.runs
import io.reactivex.Single
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

    @MockK
    private lateinit var mockSdk: Sdk

    private val settings = InternetObservingSettings.builder()
        .strategy(TestInternetObservingStrategy())
        .build()

    @MockK
    private lateinit var semesterMock: Semester

    private lateinit var timetableRemote: TimetableRemote

    private lateinit var timetableLocal: TimetableLocal

    private lateinit var testDb: AppDatabase

    @Before
    fun initApi() {
        MockKAnnotations.init(this)
        testDb = Room.inMemoryDatabaseBuilder(getApplicationContext(), AppDatabase::class.java).build()
        timetableLocal = TimetableLocal(testDb.timetableDao)
        timetableRemote = TimetableRemote(mockSdk)

        every { semesterMock.studentId } returns 1
        every { semesterMock.diaryId } returns 2
        every { semesterMock.schoolYear } returns 2019
        every { semesterMock.semesterId } returns 1
        every { mockSdk.switchDiary(any(), any()) } returns mockSdk
    }

    @After
    fun closeDb() {
        testDb.close()
    }

    @Test
    fun copyRoomToCompletedFromPrevious() {
        timetableLocal.saveTimetable(listOf(
            createTimetableLocal(of(2019, 3, 5, 8, 0), 1, "123", "Przyroda"),
            createTimetableLocal(of(2019, 3, 5, 8, 50), 2, "321", "Religia"),
            createTimetableLocal(of(2019, 3, 5, 9, 40), 3, "213", "W-F"),
            createTimetableLocal(of(2019, 3, 5, 10, 30),3, "213", "W-F", "Jan Kowalski")
        ))

        every { mockSdk.getTimetable(any(), any()) } returns Single.just(listOf(
            createTimetableRemote(of(2019, 3, 5, 8, 0), 1, "", "Przyroda"),
            createTimetableRemote(of(2019, 3, 5, 8, 50), 2, "", "Religia"),
            createTimetableRemote(of(2019, 3, 5, 9, 40), 3, "", "W-F"),
            createTimetableRemote(of(2019, 3, 5, 10, 30), 4, "", "W-F")
        ))

        val lessons = TimetableRepository(settings, timetableLocal, timetableRemote)
            .getTimetable(semesterMock, LocalDate.of(2019, 3, 5), LocalDate.of(2019, 3, 5), true)
            .blockingGet()

        assertEquals(4, lessons.size)
        assertEquals("123", lessons[0].room)
        assertEquals("321", lessons[1].room)
        assertEquals("213", lessons[2].room)
    }

    @Test
    fun copyTeacherToCompletedFromPrevious() {
        timetableLocal.saveTimetable(listOf(
            createTimetableLocal(of(2019, 3, 5, 8, 0), 1, "123", "Przyroda", "Jan Garnkiewicz", false),
            createTimetableLocal(of(2019, 3, 5, 8, 50), 2, "321", "Religia", "Paweł Jumper", false),
            createTimetableLocal(of(2019, 3, 5, 9, 40), 3, "213", "W-F", "", true),
            createTimetableLocal(of(2019, 3, 5, 10, 30), 4, "213", "W-F", "", false)
        ))

        every { mockSdk.getTimetable(any(), any()) } returns Single.just(listOf(
            createTimetableRemote(of(2019, 3, 5, 8, 0), 1, "", "Przyroda", "", true), // should override local
            createTimetableRemote(of(2019, 3, 5, 8, 50), 2, "", "Religia", "", false),
            createTimetableRemote(of(2019, 3, 5, 9, 40), 3, "", "W-F", "Jan Garnkiewicz", false),
            createTimetableRemote(of(2019, 3, 5, 10, 30), 4, "", "W-F", "Paweł Jumper", false)
        ))

        val lessons = TimetableRepository(settings, timetableLocal, timetableRemote)
            .getTimetable(semesterMock, LocalDate.of(2019, 3, 5), LocalDate.of(2019, 3, 5), true)
            .blockingGet()

        assertEquals(4, lessons.size)
        assertEquals("", lessons[0].teacher)
        assertEquals("Paweł Jumper", lessons[1].teacher)
        assertEquals("Jan Garnkiewicz", lessons[2].teacher)
        assertEquals("Paweł Jumper", lessons[3].teacher)
    }
}
