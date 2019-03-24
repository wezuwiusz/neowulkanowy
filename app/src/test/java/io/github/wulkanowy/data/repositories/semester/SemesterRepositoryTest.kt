package io.github.wulkanowy.data.repositories.semester

import com.github.pwittchen.reactivenetwork.library.rx2.internet.observing.InternetObservingSettings
import io.github.wulkanowy.data.ApiHelper
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.repositories.UnitTestInternetObservingStrategy
import io.reactivex.Maybe
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.doNothing
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations

class SemesterRepositoryTest {

    @Mock
    private lateinit var semesterRemote: SemesterRemote

    @Mock
    private lateinit var semesterLocal: SemesterLocal

    @Mock
    private lateinit var apiHelper: ApiHelper

    @Mock
    private lateinit var student: Student

    private lateinit var semesterRepository: SemesterRepository

    private val settings = InternetObservingSettings.builder()
        .strategy(UnitTestInternetObservingStrategy())
        .build()

    @Before
    fun initTest() {
        MockitoAnnotations.initMocks(this)
        semesterRepository = SemesterRepository(semesterRemote, semesterLocal, settings, apiHelper)
    }

    @Test
    fun singleCurrentSemesterTest() {
        val semesters = listOf(
            createSemesterEntity(false),
            createSemesterEntity(true)
        )

        doNothing().`when`(apiHelper).initApi(student)
        doReturn(Maybe.empty<Semester>()).`when`(semesterLocal).getSemesters(student)
        doReturn(Single.just(semesters)).`when`(semesterRemote).getSemesters(student)

        semesterRepository.getSemesters(student).blockingGet()

        verify(semesterLocal).deleteSemesters(emptyList())
        verify(semesterLocal).saveSemesters(semesters)
    }

    @Test(expected = IllegalArgumentException::class)
    fun twoCurrentSemesterTest() {
        val semesters = listOf(
            createSemesterEntity(true),
            createSemesterEntity(true)
        )

        doNothing().`when`(apiHelper).initApi(student)
        doReturn(Maybe.empty<Semester>()).`when`(semesterLocal).getSemesters(student)
        doReturn(Single.just(semesters)).`when`(semesterRemote).getSemesters(student)

        semesterRepository.getSemesters(student).blockingGet()
    }
}
