package io.github.wulkanowy.data.repositories.semester

import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.InternetObservingSettings
import io.github.wulkanowy.data.SdkHelper
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.repositories.UnitTestInternetObservingStrategy
import io.reactivex.Maybe
import io.reactivex.Single
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.doNothing
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import org.threeten.bp.LocalDate.now

class SemesterRepositoryTest {

    @Mock
    private lateinit var semesterRemote: SemesterRemote

    @Mock
    private lateinit var semesterLocal: SemesterLocal

    @Mock
    private lateinit var sdkHelper: SdkHelper

    @Mock
    private lateinit var student: Student

    private lateinit var semesterRepository: SemesterRepository

    private val settings = InternetObservingSettings.builder()
        .strategy(UnitTestInternetObservingStrategy())
        .build()

    @Before
    fun initTest() {
        MockitoAnnotations.initMocks(this)
        semesterRepository = SemesterRepository(semesterRemote, semesterLocal, settings, sdkHelper)
    }

    @Test
    fun getSemesters_noSemesters() {
        val semesters = listOf(
            createSemesterEntity(0, 0, now().minusMonths(6), now().minusMonths(3)),
            createSemesterEntity(0, 0, now().minusMonths(3), now())
        )

        doNothing().`when`(sdkHelper).init(student)
        doReturn(Maybe.empty<Semester>()).`when`(semesterLocal).getSemesters(student)
        doReturn(Single.just(semesters)).`when`(semesterRemote).getSemesters(student)

        semesterRepository.getSemesters(student).blockingGet()

        verify(semesterLocal).deleteSemesters(emptyList())
        verify(semesterLocal).saveSemesters(semesters)
    }

    @Test
    fun getSemesters_noCurrent() {
        val semesters = listOf(
            createSemesterEntity(0, 0, now().minusMonths(12), now().minusMonths(6)),
            createSemesterEntity(0, 0, now().minusMonths( 6), now().minusMonths(1))
        )

        doNothing().`when`(sdkHelper).init(student)
        doReturn(Maybe.just(semesters)).`when`(semesterLocal).getSemesters(student)

        val items = semesterRepository.getSemesters(student).blockingGet()
        assertEquals(2, items.size)
    }

    @Test
    fun getSemesters_oneCurrent() {
        val semesters = listOf(
            createSemesterEntity(0, 0, now().minusMonths(6), now().minusMonths(3)),
            createSemesterEntity(0, 0, now().minusMonths(3), now())
        )

        doNothing().`when`(sdkHelper).init(student)
        doReturn(Maybe.just(semesters)).`when`(semesterLocal).getSemesters(student)

        val items = semesterRepository.getSemesters(student).blockingGet()
        assertEquals(2, items.size)
    }

    @Test
    fun getSemesters_doubleCurrent() {
        val semesters = listOf(
            createSemesterEntity(0, 0, now(), now()),
            createSemesterEntity(0, 0, now(), now())
        )

        doNothing().`when`(sdkHelper).init(student)
        doReturn(Maybe.just(semesters)).`when`(semesterLocal).getSemesters(student)

        val items = semesterRepository.getSemesters(student).blockingGet()
        assertEquals(2, items.size)
    }

    @Test
    fun getSemesters_noSemesters_refreshOnNoCurrent() {
        val semesters = listOf(
            createSemesterEntity(0, 0, now().minusMonths(6), now().minusMonths(3)),
            createSemesterEntity(0, 0, now().minusMonths(3), now())
        )

        doNothing().`when`(sdkHelper).init(student)
        doReturn(Maybe.empty<Semester>()).`when`(semesterLocal).getSemesters(student)
        doReturn(Single.just(semesters)).`when`(semesterRemote).getSemesters(student)

        semesterRepository.getSemesters(student, refreshOnNoCurrent = true).blockingGet()

        verify(semesterLocal).deleteSemesters(emptyList())
        verify(semesterLocal).saveSemesters(semesters)
    }

    @Test
    fun getSemesters_noCurrent_refreshOnNoCurrent() {
        val semesters = listOf(
            createSemesterEntity(0, 0, now().minusMonths(12), now().minusMonths(6)),
            createSemesterEntity(0, 0, now().minusMonths( 6), now().minusMonths(1))
        )

        doNothing().`when`(sdkHelper).init(student)
        doReturn(Maybe.just(semesters)).`when`(semesterLocal).getSemesters(student)
        doReturn(Single.just(semesters)).`when`(semesterRemote).getSemesters(student)

        val items = semesterRepository.getSemesters(student, refreshOnNoCurrent = true).blockingGet()
        assertEquals(2, items.size)
    }

    @Test
    fun getSemesters_doubleCurrent_refreshOnNoCurrent() {
        val semesters = listOf(
            createSemesterEntity(0, 0, now(), now()),
            createSemesterEntity(0, 0, now(), now())
        )

        doNothing().`when`(sdkHelper).init(student)
        doReturn(Maybe.just(semesters)).`when`(semesterLocal).getSemesters(student)

        val items = semesterRepository.getSemesters(student, refreshOnNoCurrent = true).blockingGet()
        assertEquals(2, items.size)
    }

    @Test(expected = IllegalArgumentException::class)
    fun getCurrentSemester_doubleCurrent() {
        val semesters = listOf(
            createSemesterEntity(0, 0, now(), now()),
            createSemesterEntity(0, 0, now(), now())
        )

        doNothing().`when`(sdkHelper).init(student)
        doReturn(Maybe.just(semesters)).`when`(semesterLocal).getSemesters(student)

        semesterRepository.getCurrentSemester(student).blockingGet()
    }

    @Test(expected = RuntimeException::class)
    fun getCurrentSemester_emptyList() {
        doNothing().`when`(sdkHelper).init(student)
        doReturn(Maybe.just(emptyList<Semester>())).`when`(semesterLocal).getSemesters(student)

        semesterRepository.getCurrentSemester(student).blockingGet()
    }
}
