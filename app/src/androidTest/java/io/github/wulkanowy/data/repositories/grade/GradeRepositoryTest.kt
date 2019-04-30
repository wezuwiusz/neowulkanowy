package io.github.wulkanowy.data.repositories.grade

import android.os.Build.VERSION_CODES.P
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SdkSuppress
import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.InternetObservingSettings
import io.github.wulkanowy.api.Api
import io.github.wulkanowy.data.db.AppDatabase
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
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
import org.threeten.bp.LocalDate.of
import org.threeten.bp.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import io.github.wulkanowy.api.grades.Grade as GradeApi

@SdkSuppress(minSdkVersion = P)
@RunWith(AndroidJUnit4::class)
class GradeRepositoryTest {

    @SpyK
    private var mockApi = Api()

    private val settings = InternetObservingSettings.builder()
        .strategy(TestInternetObservingStrategy())
        .build()

    @MockK
    private lateinit var semesterMock: Semester

    @MockK
    private lateinit var studentMock: Student

    private lateinit var gradeRemote: GradeRemote

    private lateinit var gradeLocal: GradeLocal

    private lateinit var testDb: AppDatabase

    @Before
    fun initApi() {
        MockKAnnotations.init(this)
        testDb = Room.inMemoryDatabaseBuilder(getApplicationContext(), AppDatabase::class.java).build()
        gradeLocal = GradeLocal(testDb.gradeDao)
        gradeRemote = GradeRemote(mockApi)

        every { mockApi.diaryId } returns 1
        every { studentMock.registrationDate } returns LocalDateTime.of(2019, 2, 27, 12, 0)
        every { semesterMock.studentId } returns 1
        every { semesterMock.semesterId } returns 1
        every { semesterMock.diaryId } returns 1
    }

    @After
    fun closeDb() {
        testDb.close()
    }

    @Test
    fun markOlderThanRegisterDateAsRead() {
        every { mockApi.getGrades(1) } returns Single.just(listOf(
            createGradeApi(5, 4.0, of(2019, 2, 25), "Ocena pojawiła się"),
            createGradeApi(5, 4.0, of(2019, 2, 26), "przed zalogowanie w aplikacji"),
            createGradeApi(5, 4.0, of(2019, 2, 27), "Ocena z dnia logowania"),
            createGradeApi(5, 4.0, of(2019, 2, 28), "Ocena jeszcze nowsza")
        ))

        val grades = GradeRepository(settings, gradeLocal, gradeRemote)
            .getGrades(studentMock, semesterMock, true).blockingGet().sortedByDescending { it.date }

        assertFalse { grades[0].isRead }
        assertFalse { grades[1].isRead }
        assertTrue { grades[2].isRead }
        assertTrue { grades[3].isRead }
    }

    @Test
    fun mitigateOldGradesNotifications() {
        gradeLocal.saveGrades(listOf(
            createGradeLocal(5, 3.0, of(2019, 2, 25), "Jedna ocena"),
            createGradeLocal(4, 4.0, of(2019, 2, 26), "Druga"),
            createGradeLocal(3, 5.0, of(2019, 2, 27), "Trzecia")
        ))

        every { mockApi.getGrades(1) } returns Single.just(listOf(
            createGradeApi(5, 2.0, of(2019, 2, 25), "Ocena ma datę, jest inna, ale nie zostanie powiadomiona"),
            createGradeApi(4, 3.0, of(2019, 2, 26), "starszą niż ostatnia lokalnie"),
            createGradeApi(3, 4.0, of(2019, 2, 27), "Ta jest z tego samego dnia co ostatnia lokalnie"),
            createGradeApi(2, 5.0, of(2019, 2, 28), "Ta jest już w ogóle nowa")
        ))

        val grades = GradeRepository(settings, gradeLocal, gradeRemote)
            .getGrades(studentMock, semesterMock, true).blockingGet().sortedByDescending { it.date }

        assertFalse { grades[0].isRead }
        assertFalse { grades[1].isRead }
        assertTrue { grades[2].isRead }
        assertTrue { grades[3].isRead }
    }

    @Test
    fun subtractLocaleDuplicateGrades() {
        gradeLocal.saveGrades(listOf(
            createGradeLocal(5, 3.0, of(2019, 2, 25), "Taka sama ocena"),
            createGradeLocal(5, 3.0, of(2019, 2, 25), "Taka sama ocena"),
            createGradeLocal(3, 5.0, of(2019, 2, 26), "Jakaś inna ocena")
        ))

        every { mockApi.getGrades(1) } returns Single.just(listOf(
            createGradeApi(5, 3.0, of(2019, 2, 25), "Taka sama ocena"),
            createGradeApi(3, 5.0, of(2019, 2, 26), "Jakaś inna ocena")
        ))

        val grades = GradeRepository(settings, gradeLocal, gradeRemote)
            .getGrades(studentMock, semesterMock, true).blockingGet()

        assertEquals(2, grades.size)
    }

    @Test
    fun subtractRemoteDuplicateGrades() {
        gradeLocal.saveGrades(listOf(
            createGradeLocal(5, 3.0, of(2019, 2, 25), "Taka sama ocena"),
            createGradeLocal(3, 5.0, of(2019, 2, 26), "Jakaś inna ocena")
        ))

        every { mockApi.getGrades(1) } returns Single.just(listOf(
            createGradeApi(5, 3.0, of(2019, 2, 25), "Taka sama ocena"),
            createGradeApi(5, 3.0, of(2019, 2, 25), "Taka sama ocena"),
            createGradeApi(3, 5.0, of(2019, 2, 26), "Jakaś inna ocena")
        ))

        val grades = GradeRepository(settings, gradeLocal, gradeRemote)
            .getGrades(studentMock, semesterMock, true).blockingGet()

        assertEquals(3, grades.size)
    }

    @Test
    fun emptyLocal() {
        gradeLocal.saveGrades(listOf())

        every { mockApi.getGrades(1) } returns Single.just(listOf(
            createGradeApi(5, 3.0, of(2019, 2, 25), "Taka sama ocena"),
            createGradeApi(5, 3.0, of(2019, 2, 25), "Taka sama ocena"),
            createGradeApi(3, 5.0, of(2019, 2, 26), "Jakaś inna ocena")
        ))

        val grades = GradeRepository(settings, gradeLocal, gradeRemote)
            .getGrades(studentMock, semesterMock, true).blockingGet()

        assertEquals(3, grades.size)
    }

    @Test
    fun emptyRemote() {
        gradeLocal.saveGrades(listOf(
            createGradeLocal(5, 3.0, of(2019, 2, 25), "Taka sama ocena"),
            createGradeLocal(3, 5.0, of(2019, 2, 26), "Jakaś inna ocena")
        ))

        every { mockApi.getGrades(1) } returns Single.just(listOf())

        val grades = GradeRepository(settings, gradeLocal, gradeRemote)
            .getGrades(studentMock, semesterMock, true).blockingGet()

        assertEquals(0, grades.size)
    }
}
