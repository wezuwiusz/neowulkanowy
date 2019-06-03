package io.github.wulkanowy.ui.modules.grade

import io.github.wulkanowy.data.db.entities.Grade
import io.github.wulkanowy.data.db.entities.GradeSummary
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.repositories.grade.GradeRepository
import io.github.wulkanowy.data.repositories.gradessummary.GradeSummaryRepository
import io.github.wulkanowy.data.repositories.preferences.PreferencesRepository
import io.reactivex.Single
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.doReturn
import org.mockito.MockitoAnnotations
import org.threeten.bp.LocalDate.now
import org.threeten.bp.LocalDateTime

class GradeAverageProviderTest {

    @Mock
    lateinit var preferencesRepository: PreferencesRepository

    @Mock
    lateinit var gradeRepository: GradeRepository

    @Mock
    lateinit var gradeSummaryRepository: GradeSummaryRepository

    private lateinit var gradeAverageProvider: GradeAverageProvider

    private val student = Student("", "", "", "", "", 101, "", "", "", "", 1, true, LocalDateTime.now())

    private val semesters = mutableListOf(
        Semester(101, 10, "", 1, 21, 1, false, now(), now(), 1, 1),
        Semester(101, 11, "", 1, 22, 1, false, now(), now(), 1, 1),
        Semester(101, 11, "", 1, 23, 2, true, now(), now(), 1, 1)
    )

    private val firstGrades = listOf(
        getGrade(22, "Matematyka", 4),
        getGrade(22, "Matematyka", 3),
        getGrade(22, "Fizyka", 6),
        getGrade(22, "Fizyka", 1)
    )

    private val secondGrade = listOf(
        getGrade(23, "Matematyka", 2),
        getGrade(23, "Matematyka", 3),
        getGrade(23, "Fizyka", 4),
        getGrade(23, "Fizyka", 2)
    )

    @Before
    fun initTest() {
        MockitoAnnotations.initMocks(this)
        gradeAverageProvider = GradeAverageProvider(preferencesRepository, gradeRepository, gradeSummaryRepository)

        doReturn(.33).`when`(preferencesRepository).gradeMinusModifier
        doReturn(.33).`when`(preferencesRepository).gradePlusModifier

        doReturn(Single.just(firstGrades)).`when`(gradeRepository).getGrades(student, semesters[1], true)
        doReturn(Single.just(secondGrade)).`when`(gradeRepository).getGrades(student, semesters[2], true)
    }

    @Test
    fun onlyOneSemesterTest() {
        doReturn("only_one_semester").`when`(preferencesRepository).gradeAverageMode
        doReturn(Single.just(emptyList<GradeSummary>())).`when`(gradeSummaryRepository).getGradesSummary(semesters[2], true)

        val averages = gradeAverageProvider.getGradeAverage(student, semesters, semesters[2].semesterId, true)
            .blockingGet()

        assertEquals(2, averages.size)
        assertEquals(2.5, averages["Matematyka"])
        assertEquals(3.0, averages["Fizyka"])
    }

    @Test
    fun allYearFirstSemesterTest() {
        doReturn("all_year").`when`(preferencesRepository).gradeAverageMode
        doReturn(Single.just(emptyList<GradeSummary>())).`when`(gradeSummaryRepository).getGradesSummary(semesters[1], true)

        val averages = gradeAverageProvider.getGradeAverage(student, semesters, semesters[1].semesterId, true)
            .blockingGet()

        assertEquals(2, averages.size)
        assertEquals(3.5, averages["Matematyka"])
        assertEquals(3.5, averages["Fizyka"])
    }

    @Test
    fun allYearSecondSemesterTest() {
        doReturn("all_year").`when`(preferencesRepository).gradeAverageMode
        doReturn(Single.just(firstGrades)).`when`(gradeRepository).getGrades(student, semesters[1], false)
        doReturn(Single.just(emptyList<GradeSummary>())).`when`(gradeSummaryRepository).getGradesSummary(semesters[2], true)

        val averages = gradeAverageProvider.getGradeAverage(student, semesters, semesters[2].semesterId, true)
            .blockingGet()

        assertEquals(2, averages.size)
        assertEquals(3.0, averages["Matematyka"])
        assertEquals(3.25, averages["Fizyka"])
    }

    @Test(expected = IllegalArgumentException::class)
    fun incorrectAverageModeTest() {
        doReturn("test_mode").`when`(preferencesRepository).gradeAverageMode

        gradeAverageProvider.getGradeAverage(student, semesters, semesters[2].semesterId, true).blockingGet()
    }

    @Test
    fun onlyOneSemester_averageFromSummary() {
        doReturn("all_year").`when`(preferencesRepository).gradeAverageMode
        doReturn(Single.just(firstGrades)).`when`(gradeRepository).getGrades(student, semesters[1], false)
        doReturn(Single.just(listOf(
            getSummary(22, "Matematyka", 3.1),
            getSummary(22, "Fizyka", 3.26)
        ))).`when`(gradeSummaryRepository).getGradesSummary(semesters[2], true)

        val averages = gradeAverageProvider.getGradeAverage(student, semesters, semesters[2].semesterId, true)
            .blockingGet()

        assertEquals(2, averages.size)
        assertEquals(3.1, averages["Matematyka"])
        assertEquals(3.26, averages["Fizyka"])
    }

    private fun getGrade(semesterId: Int, subject: String, value: Int): Grade {
        return Grade(
            studentId = 101,
            semesterId = semesterId,
            subject = subject,
            value = value,
            modifier = .0,
            weightValue = 1.0,
            teacher = "",
            date = now(),
            weight = "",
            gradeSymbol = "",
            entry = "",
            description = "",
            comment = "",
            color = ""
        )
    }

    private fun getSummary(semesterId: Int, subject: String, value: Double): GradeSummary {
        return GradeSummary(
            studentId = 101,
            semesterId = semesterId,
            subject = subject,
            average = value,
            pointsSum = "",
            proposedPoints = "",
            finalPoints = "",
            finalGrade = "",
            predictedGrade = "",
            position = 0
        )
    }
}
