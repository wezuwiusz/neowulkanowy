package io.github.wulkanowy.data.repositories.timetable

import android.os.Build.VERSION_CODES.P
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SdkSuppress
import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.InternetObservingSettings
import io.github.wulkanowy.api.Api
import io.github.wulkanowy.data.db.AppDatabase
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.repositories.TestInternetObservingStrategy
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.SpyK
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

    @SpyK
    private var mockApi = Api()

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
        timetableRemote = TimetableRemote(mockApi)

        every { semesterMock.studentId } returns 1
        every { semesterMock.diaryId } returns 2
    }

    @After
    fun closeDb() {
        testDb.close()
    }

    @Test
    fun copyDetailsToCompletedFromPrevious() {
        timetableLocal.saveTimetable(listOf(
            createTimetableLocal(1, of(2019, 3, 5, 8, 0), "123", "Przyroda"),
            createTimetableLocal(1, of(2019, 3, 5, 8, 50), "321", "Religia"),
            createTimetableLocal(1, of(2019, 3, 5, 9, 40), "213", "W-F")
        ))

        every { mockApi.getTimetable(any(), any()) } returns Single.just(listOf(
            createTimetableRemote(1, of(2019, 3, 5, 8, 0), "", "Przyroda"),
            createTimetableRemote(1, of(2019, 3, 5, 8, 50), "", "Religia"),
            createTimetableRemote(1, of(2019, 3, 5, 9, 40), "", "W-F")
        ))

        val lessons = TimetableRepository(settings, timetableLocal, timetableRemote)
            .getTimetable(semesterMock, LocalDate.of(2019, 3, 5), LocalDate.of(2019, 3, 5), true)
            .blockingGet()

        assertEquals(3, lessons.size)
        assertEquals("123", lessons[0].room)
        assertEquals("321", lessons[1].room)
        assertEquals("213", lessons[2].room)
    }
}
