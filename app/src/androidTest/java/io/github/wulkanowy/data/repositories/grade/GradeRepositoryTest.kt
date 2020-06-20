package io.github.wulkanowy.data.repositories.grade

import android.os.Build.VERSION_CODES.P
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SdkSuppress
import io.github.wulkanowy.data.db.AppDatabase
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.sdk.Sdk
import io.github.wulkanowy.sdk.pojo.Grade
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.threeten.bp.LocalDate.of
import org.threeten.bp.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@SdkSuppress(minSdkVersion = P)
@RunWith(AndroidJUnit4::class)
class GradeRepositoryTest {

    @MockK
    private lateinit var mockSdk: Sdk

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
        gradeLocal = GradeLocal(testDb.gradeDao, testDb.gradeSummaryDao)
        gradeRemote = GradeRemote(mockSdk)

        every { studentMock.registrationDate } returns LocalDateTime.of(2019, 2, 27, 12, 0)
        every { semesterMock.studentId } returns 1
        every { semesterMock.diaryId } returns 1
        every { semesterMock.schoolYear } returns 2019
        every { semesterMock.semesterId } returns 1
        every { mockSdk.switchDiary(any(), any()) } returns mockSdk
    }

    @After
    fun closeDb() {
        testDb.close()
    }

    @Test
    fun markOlderThanRegisterDateAsRead() {
        coEvery { mockSdk.getGrades(1) } returns (listOf(
            createGradeApi(5, 4.0, of(2019, 2, 25), "Ocena pojawiła się"),
            createGradeApi(5, 4.0, of(2019, 2, 26), "przed zalogowanie w aplikacji"),
            createGradeApi(5, 4.0, of(2019, 2, 27), "Ocena z dnia logowania"),
            createGradeApi(5, 4.0, of(2019, 2, 28), "Ocena jeszcze nowsza")
        ) to emptyList())

        val grades = runBlocking {
            GradeRepository(gradeLocal, gradeRemote).getGrades(studentMock, semesterMock, true)
                .first.sortedByDescending { it.date }
        }

        assertFalse { grades[0].isRead }
        assertFalse { grades[1].isRead }
        assertTrue { grades[2].isRead }
        assertTrue { grades[3].isRead }
    }

    @Test
    fun mitigateOldGradesNotifications() {
        val list = listOf(
            createGradeLocal(5, 3.0, of(2019, 2, 25), "Jedna ocena"),
            createGradeLocal(4, 4.0, of(2019, 2, 26), "Druga"),
            createGradeLocal(3, 5.0, of(2019, 2, 27), "Trzecia")
        )
        runBlocking { gradeLocal.saveGrades(list) }

        coEvery { mockSdk.getGrades(1) } returns (listOf(
            createGradeApi(5, 2.0, of(2019, 2, 25), "Ocena ma datę, jest inna, ale nie zostanie powiadomiona"),
            createGradeApi(4, 3.0, of(2019, 2, 26), "starszą niż ostatnia lokalnie"),
            createGradeApi(3, 4.0, of(2019, 2, 27), "Ta jest z tego samego dnia co ostatnia lokalnie"),
            createGradeApi(2, 5.0, of(2019, 2, 28), "Ta jest już w ogóle nowa")
        ) to emptyList())

        val grades = runBlocking {
            GradeRepository(gradeLocal, gradeRemote).getGrades(studentMock, semesterMock, true)
                .first.sortedByDescending { it.date }
        }

        assertFalse { grades[0].isRead }
        assertFalse { grades[1].isRead }
        assertTrue { grades[2].isRead }
        assertTrue { grades[3].isRead }
    }

    @Test
    fun subtractLocaleDuplicateGrades() {
        val list = listOf(
            createGradeLocal(5, 3.0, of(2019, 2, 25), "Taka sama ocena"),
            createGradeLocal(5, 3.0, of(2019, 2, 25), "Taka sama ocena"),
            createGradeLocal(3, 5.0, of(2019, 2, 26), "Jakaś inna ocena")
        )
        runBlocking { gradeLocal.saveGrades(list) }

        coEvery { mockSdk.getGrades(1) } returns (listOf(
            createGradeApi(5, 3.0, of(2019, 2, 25), "Taka sama ocena"),
            createGradeApi(3, 5.0, of(2019, 2, 26), "Jakaś inna ocena")
        ) to emptyList())

        val grades = runBlocking {
            GradeRepository(gradeLocal, gradeRemote).getGrades(studentMock, semesterMock, true)
        }

        assertEquals(2, grades.first.size)
    }

    @Test
    fun subtractRemoteDuplicateGrades() {
        val list = listOf(
            createGradeLocal(5, 3.0, of(2019, 2, 25), "Taka sama ocena"),
            createGradeLocal(3, 5.0, of(2019, 2, 26), "Jakaś inna ocena")
        )
        runBlocking { gradeLocal.saveGrades(list) }

        coEvery { mockSdk.getGrades(1) } returns (listOf(
            createGradeApi(5, 3.0, of(2019, 2, 25), "Taka sama ocena"),
            createGradeApi(5, 3.0, of(2019, 2, 25), "Taka sama ocena"),
            createGradeApi(3, 5.0, of(2019, 2, 26), "Jakaś inna ocena")
        ) to emptyList())

        val grades = runBlocking {
            GradeRepository(gradeLocal, gradeRemote).getGrades(studentMock, semesterMock, true)
        }

        assertEquals(3, grades.first.size)
    }

    @Test
    fun emptyLocal() {
        runBlocking { gradeLocal.saveGrades(listOf()) }

        coEvery { mockSdk.getGrades(1) } returns (listOf(
            createGradeApi(5, 3.0, of(2019, 2, 25), "Taka sama ocena"),
            createGradeApi(5, 3.0, of(2019, 2, 25), "Taka sama ocena"),
            createGradeApi(3, 5.0, of(2019, 2, 26), "Jakaś inna ocena")
        ) to emptyList())

        val grades = runBlocking {
            GradeRepository(gradeLocal, gradeRemote).getGrades(studentMock, semesterMock, true)
        }

        assertEquals(3, grades.first.size)
    }

    @Test
    fun emptyRemote() {
        val list = listOf(
            createGradeLocal(5, 3.0, of(2019, 2, 25), "Taka sama ocena"),
            createGradeLocal(3, 5.0, of(2019, 2, 26), "Jakaś inna ocena")
        )
        runBlocking { gradeLocal.saveGrades(list) }

        coEvery { mockSdk.getGrades(1) } returns (emptyList<Grade>() to emptyList())

        val grades = runBlocking {
            GradeRepository(gradeLocal, gradeRemote).getGrades(studentMock, semesterMock, true)
        }

        assertEquals(0, grades.first.size)
    }
}
