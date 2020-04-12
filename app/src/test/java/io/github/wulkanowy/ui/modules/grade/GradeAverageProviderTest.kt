package io.github.wulkanowy.ui.modules.grade

import io.github.wulkanowy.data.db.entities.Grade
import io.github.wulkanowy.data.db.entities.GradeSummary
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.repositories.grade.GradeRepository
import io.github.wulkanowy.data.repositories.gradessummary.GradeSummaryRepository
import io.github.wulkanowy.data.repositories.preferences.PreferencesRepository
import io.github.wulkanowy.data.repositories.createSemesterEntity
import io.github.wulkanowy.sdk.Sdk
import io.reactivex.Single
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.doReturn
import org.mockito.MockitoAnnotations
import org.threeten.bp.LocalDate.now
import org.threeten.bp.LocalDate.of
import org.threeten.bp.LocalDateTime

class GradeAverageProviderTest {

    @Mock
    lateinit var preferencesRepository: PreferencesRepository

    @Mock
    lateinit var gradeRepository: GradeRepository

    @Mock
    lateinit var gradeSummaryRepository: GradeSummaryRepository

    private lateinit var gradeAverageProvider: GradeAverageProvider

    private val student = Student("", "", "", "SCRAPPER", "", "", false, "", "", "", 101, 0, "", "", "", "", "", 1, true, LocalDateTime.now())

    private val semesters = mutableListOf(
        createSemesterEntity(10, 21, of(2019, 1, 31), of(2019, 6, 23)),
        createSemesterEntity(11, 22, of(2019, 9, 1), of(2020, 1, 31)),
        createSemesterEntity(11, 23, of(2020, 2, 1), now(), semesterName = 2)
    )

    private val firstGrades = listOf(
        getGrade(22, "Matematyka", 4.0),
        getGrade(22, "Matematyka", 3.0),
        getGrade(22, "Fizyka", 6.0),
        getGrade(22, "Fizyka", 1.0)
    )

    private val secondGrade = listOf(
        getGrade(23, "Matematyka", 2.0),
        getGrade(23, "Matematyka", 3.0),
        getGrade(23, "Fizyka", 4.0),
        getGrade(23, "Fizyka", 2.0)
    )

    private val secondGradeWithModifier = listOf(
        getGrade(24, "Język polski", 3.0, -0.50),
        getGrade(24, "Język polski", 4.0, 0.25)
    )

    @Before
    fun initTest() {
        MockitoAnnotations.initMocks(this)
        gradeAverageProvider = GradeAverageProvider(preferencesRepository, gradeRepository, gradeSummaryRepository)

        doReturn(.33).`when`(preferencesRepository).gradeMinusModifier
        doReturn(.33).`when`(preferencesRepository).gradePlusModifier
        doReturn(false).`when`(preferencesRepository).gradeAverageForceCalc

        doReturn(Single.just(firstGrades)).`when`(gradeRepository).getGrades(student, semesters[1], true)
        doReturn(Single.just(secondGrade)).`when`(gradeRepository).getGrades(student, semesters[2], true)
    }

    @Test
    fun onlyOneSemesterTest() {
        doReturn("only_one_semester").`when`(preferencesRepository).gradeAverageMode
        doReturn(Single.just(emptyList<GradeSummary>())).`when`(gradeSummaryRepository).getGradesSummary(student, semesters[2], true)

        val averages = gradeAverageProvider.getGradeAverage(student, semesters, semesters[2].semesterId, true)
            .blockingGet()

        assertEquals(2, averages.size)
        assertEquals(2.5, averages.single { it.first == "Matematyka" }.second, .0)
        assertEquals(3.0, averages.single { it.first == "Fizyka" }.second, .0)
    }

    @Test
    fun onlyOneSemester_gradesWithModifiers_default() {
        doReturn("only_one_semester").`when`(preferencesRepository).gradeAverageMode
        doReturn(Single.just(emptyList<GradeSummary>())).`when`(gradeSummaryRepository).getGradesSummary(student, semesters[2], true)
        doReturn(Single.just(secondGradeWithModifier)).`when`(gradeRepository).getGrades(student, semesters[2], true)

        val averages = gradeAverageProvider.getGradeAverage(student, semesters, semesters[2].semesterId, true)
            .blockingGet()

        assertEquals(3.5, averages.single { it.first == "Język polski" }.second, .0)
    }

    @Test
    fun onlyOneSemester_gradesWithModifiers_api() {
        doReturn("only_one_semester").`when`(preferencesRepository).gradeAverageMode
        doReturn(Single.just(emptyList<GradeSummary>())).`when`(gradeSummaryRepository).getGradesSummary(student.copy(loginMode = Sdk.Mode.API.name), semesters[2], true)
        doReturn(Single.just(secondGradeWithModifier)).`when`(gradeRepository).getGrades(student.copy(loginMode = Sdk.Mode.API.name), semesters[2], true)

        val averages = gradeAverageProvider.getGradeAverage(student.copy(loginMode = Sdk.Mode.API.name), semesters, semesters[2].semesterId, true)
            .blockingGet()

        assertEquals(3.375, averages.single { it.first == "Język polski" }.second, .0)
    }

    @Test
    fun onlyOneSemester_gradesWithModifiers_scrapper() {
        doReturn("only_one_semester").`when`(preferencesRepository).gradeAverageMode
        doReturn(Single.just(emptyList<GradeSummary>())).`when`(gradeSummaryRepository).getGradesSummary(student, semesters[2], true)
        doReturn(Single.just(secondGradeWithModifier)).`when`(gradeRepository).getGrades(student.copy(loginMode = Sdk.Mode.SCRAPPER.name), semesters[2], true)

        val averages = gradeAverageProvider.getGradeAverage(student.copy(loginMode = Sdk.Mode.SCRAPPER.name), semesters, semesters[2].semesterId, true)
            .blockingGet()

        assertEquals(3.5, averages.single { it.first == "Język polski" }.second, .0)
    }

    @Test
    fun onlyOneSemester_gradesWithModifiers_hybrid() {
        doReturn("only_one_semester").`when`(preferencesRepository).gradeAverageMode
        doReturn(Single.just(emptyList<GradeSummary>())).`when`(gradeSummaryRepository).getGradesSummary(student.copy(loginMode = Sdk.Mode.HYBRID.name), semesters[2], true)
        doReturn(Single.just(secondGradeWithModifier)).`when`(gradeRepository).getGrades(student.copy(loginMode = Sdk.Mode.HYBRID.name), semesters[2], true)

        val averages = gradeAverageProvider.getGradeAverage(student.copy(loginMode = Sdk.Mode.HYBRID.name), semesters, semesters[2].semesterId, true)
            .blockingGet()

        assertEquals(3.375, averages.single { it.first == "Język polski" }.second, .0)
    }

    @Test
    fun allYearFirstSemesterTest() {
        doReturn("all_year").`when`(preferencesRepository).gradeAverageMode
        doReturn(Single.just(emptyList<GradeSummary>())).`when`(gradeSummaryRepository).getGradesSummary(student, semesters[1], true)

        val averages = gradeAverageProvider.getGradeAverage(student, semesters, semesters[1].semesterId, true)
            .blockingGet()

        assertEquals(2, averages.size)
        assertEquals(3.5, averages.single { it.first == "Matematyka" }.second, .0)
        assertEquals(3.5, averages.single { it.first == "Fizyka" }.second, .0)
    }

    @Test
    fun allYearSecondSemesterTest() {
        doReturn("all_year").`when`(preferencesRepository).gradeAverageMode
        doReturn(Single.just(firstGrades)).`when`(gradeRepository).getGrades(student, semesters[1], false)
        doReturn(Single.just(emptyList<GradeSummary>())).`when`(gradeSummaryRepository).getGradesSummary(student, semesters[2], true)

        val averages = gradeAverageProvider.getGradeAverage(student, semesters, semesters[2].semesterId, true)
            .blockingGet()

        assertEquals(2, averages.size)
        assertEquals(3.0, averages.single { it.first == "Matematyka" }.second, .0)
        assertEquals(3.25, averages.single { it.first == "Fizyka" }.second, .0)
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
        ))).`when`(gradeSummaryRepository).getGradesSummary(student, semesters[2], true)

        val averages = gradeAverageProvider.getGradeAverage(student, semesters, semesters[2].semesterId, true)
            .blockingGet()

        assertEquals(2, averages.size)
        assertEquals(3.1, averages.single { it.first == "Matematyka" }.second, .0)
        assertEquals(3.26, averages.single { it.first == "Fizyka" }.second, .0)
    }

    @Test
    fun onlyOneSemester_averageFromSummary_forceCalc() {
        doReturn(true).`when`(preferencesRepository).gradeAverageForceCalc
        doReturn("all_year").`when`(preferencesRepository).gradeAverageMode
        doReturn(Single.just(firstGrades)).`when`(gradeRepository).getGrades(student, semesters[1], false)
        doReturn(Single.just(listOf(
            getSummary(22, "Matematyka", 3.1),
            getSummary(22, "Fizyka", 3.26)
        ))).`when`(gradeSummaryRepository).getGradesSummary(student, semesters[2], true)

        val averages = gradeAverageProvider.getGradeAverage(student, semesters, semesters[2].semesterId, true)
            .blockingGet()

        assertEquals(2, averages.size)
        assertEquals(3.0, averages.single { it.first == "Matematyka" }.second, .0)
        assertEquals(3.25, averages.single { it.first == "Fizyka" }.second, .0)
    }

    private fun getGrade(semesterId: Int, subject: String, value: Double, modifier: Double = 0.0): Grade {
        return Grade(
            studentId = 101,
            semesterId = semesterId,
            subject = subject,
            value = value,
            modifier = modifier,
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
