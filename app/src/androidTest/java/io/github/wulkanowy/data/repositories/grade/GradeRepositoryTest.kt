package io.github.wulkanowy.data.repositories.grade

import android.os.Build.VERSION_CODES.P
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SdkSuppress
import io.github.wulkanowy.data.Status
import io.github.wulkanowy.data.db.AppDatabase
import io.github.wulkanowy.data.db.entities.Grade
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.sdk.Sdk
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate.of
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@SdkSuppress(minSdkVersion = P)
@RunWith(AndroidJUnit4::class)
class GradeRepositoryTest {

    @MockK
    private lateinit var semesterMock: Semester

    private lateinit var studentMock: Student

    @MockK
    private lateinit var gradeRemote: GradeRemote

    private lateinit var gradeLocal: GradeLocal

    private lateinit var testDb: AppDatabase

    @Before
    fun initApi() {
        MockKAnnotations.init(this)
        testDb = Room.inMemoryDatabaseBuilder(getApplicationContext(), AppDatabase::class.java).build()
        gradeLocal = GradeLocal(testDb.gradeDao, testDb.gradeSummaryDao)
        studentMock = getStudentMock()

        every { semesterMock.studentId } returns 1
        every { semesterMock.diaryId } returns 1
        every { semesterMock.schoolYear } returns 2019
        every { semesterMock.semesterId } returns 1
    }

    @After
    fun closeDb() {
        testDb.close()
    }

    @Test
    fun markOlderThanRegisterDateAsRead() {
        coEvery { gradeRemote.getGrades(studentMock, semesterMock) } returns (listOf(
            createGradeLocal(5, 4.0, of(2019, 2, 25), "Ocena pojawiła się"),
            createGradeLocal(5, 4.0, of(2019, 2, 26), "przed zalogowanie w aplikacji"),
            createGradeLocal(5, 4.0, of(2019, 2, 27), "Ocena z dnia logowania"),
            createGradeLocal(5, 4.0, of(2019, 2, 28), "Ocena jeszcze nowsza")
        ) to emptyList())

        val grades = runBlocking {
            GradeRepository(gradeLocal, gradeRemote)
                .getGrades(studentMock, semesterMock, true)
                .filter { it.status == Status.SUCCESS }.first().data!!.first.sortedByDescending { it.date }
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

        coEvery { gradeRemote.getGrades(studentMock, semesterMock) } returns (listOf(
            createGradeLocal(5, 2.0, of(2019, 2, 25), "Ocena ma datę, jest inna, ale nie zostanie powiadomiona"),
            createGradeLocal(4, 3.0, of(2019, 2, 26), "starszą niż ostatnia lokalnie"),
            createGradeLocal(3, 4.0, of(2019, 2, 27), "Ta jest z tego samego dnia co ostatnia lokalnie"),
            createGradeLocal(2, 5.0, of(2019, 2, 28), "Ta jest już w ogóle nowa")
        ) to emptyList())

        val grades = runBlocking {
            GradeRepository(gradeLocal, gradeRemote)
                .getGrades(studentMock, semesterMock, true)
                .filter { it.status == Status.SUCCESS }.first().data!!.first.sortedByDescending { it.date }
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

        coEvery { gradeRemote.getGrades(studentMock, semesterMock) } returns (listOf(
            createGradeLocal(5, 3.0, of(2019, 2, 25), "Taka sama ocena"),
            createGradeLocal(3, 5.0, of(2019, 2, 26), "Jakaś inna ocena")
        ) to emptyList())

        val grades = runBlocking {
            GradeRepository(gradeLocal, gradeRemote)
                .getGrades(studentMock, semesterMock, true)
                .filter { it.status == Status.SUCCESS }.first().data!!
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

        coEvery { gradeRemote.getGrades(studentMock, semesterMock) } returns (listOf(
            createGradeLocal(5, 3.0, of(2019, 2, 25), "Taka sama ocena"),
            createGradeLocal(5, 3.0, of(2019, 2, 25), "Taka sama ocena"),
            createGradeLocal(3, 5.0, of(2019, 2, 26), "Jakaś inna ocena")
        ) to emptyList())

        val grades = runBlocking {
            GradeRepository(gradeLocal, gradeRemote)
                .getGrades(studentMock, semesterMock, true)
                .filter { it.status == Status.SUCCESS }.first().data!!
        }

        assertEquals(3, grades.first.size)
    }

    @Test
    fun emptyLocal() {
        runBlocking { gradeLocal.saveGrades(listOf()) }

        coEvery { gradeRemote.getGrades(studentMock, semesterMock) } returns (listOf(
            createGradeLocal(5, 3.0, of(2019, 2, 25), "Taka sama ocena"),
            createGradeLocal(5, 3.0, of(2019, 2, 25), "Taka sama ocena"),
            createGradeLocal(3, 5.0, of(2019, 2, 26), "Jakaś inna ocena")
        ) to emptyList())

        val grades = runBlocking {
            GradeRepository(gradeLocal, gradeRemote)
                .getGrades(studentMock, semesterMock, true)
                .filter { it.status == Status.SUCCESS }.first().data!!
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

        coEvery { gradeRemote.getGrades(studentMock, semesterMock) } returns (emptyList<Grade>() to emptyList())

        val grades = runBlocking {
            GradeRepository(gradeLocal, gradeRemote)
                .getGrades(studentMock, semesterMock, true)
                .filter { it.status == Status.SUCCESS }.first().data!!
        }

        assertEquals(0, grades.first.size)
    }

    private fun getStudentMock() = Student(
        scrapperBaseUrl = "http://fakelog.cf",
        email = "jan@fakelog.cf",
        certificateKey = "",
        classId = 0,
        className = "",
        isCurrent = false,
        isParent = false,
        loginMode = Sdk.Mode.SCRAPPER.name,
        loginType = "STANDARD",
        mobileBaseUrl = "",
        password = "",
        privateKey = "",
        registrationDate = LocalDateTime.of(2019, 2, 27, 12, 0),
        schoolName = "",
        schoolShortName = "test",
        schoolSymbol = "",
        studentId = 0,
        studentName = "",
        symbol = "",
        userLoginId = 0,
        userName = ""
    )
}
