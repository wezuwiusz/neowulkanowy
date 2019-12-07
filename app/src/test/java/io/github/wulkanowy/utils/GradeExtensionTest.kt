package io.github.wulkanowy.utils

import io.github.wulkanowy.data.db.entities.Grade
import io.github.wulkanowy.data.db.entities.GradeSummary
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.threeten.bp.LocalDate

class GradeExtensionTest {

    @Mock
    lateinit var date: LocalDate

    @Before
    fun before() {
        MockitoAnnotations.initMocks(this)
    }

    @Test
    fun calcWeightedAverage() {
        assertEquals(3.47, listOf(
            createGrade(5, 6.0, 0.33),
            createGrade(5, 5.0, -0.33),
            createGrade(4, 1.0, 0.0),
            createGrade(1, 9.0, 0.5),
            createGrade(0, .0, 0.0)
        ).calcAverage(), 0.005)
    }

    @Test
    fun calcSummaryAverage() {
        assertEquals(2.5, listOf(
            createGradeSummary("5"),
            createGradeSummary("-5"),
            createGradeSummary("test"),
            createGradeSummary("0")
        ).calcAverage(), 0.005)
    }

    @Test
    fun changeModifier_zero() {
        assertEquals(.0, createGrade(5, .0, .5).changeModifier(.0, .0).modifier, .0)
        assertEquals(.0, createGrade(5, .0, -.5).changeModifier(.0, .0).modifier, .0)
    }

    @Test
    fun changeModifier_plus() {
        assertEquals(.33, createGrade(5, .0, .25).changeModifier(.33, .50).modifier, .0)
        assertEquals(.25, createGrade(5, .0, .33).changeModifier(.25, .0).modifier, .0)
    }

    @Test
    fun changeModifier_minus() {
        assertEquals(-.33, createGrade(5, .0, -.25).changeModifier(.25, .33).modifier, .0)
        assertEquals(-.25, createGrade(5, .0, -.33).changeModifier(.0, .25).modifier, .0)
    }

    private fun createGrade(value: Int, weightValue: Double = .0, modifier: Double = 0.25): Grade {
        return Grade(
            semesterId = 1,
            studentId = 1,
            subject = "",
            entry = "",
            value = value,
            modifier = modifier,
            comment = "",
            color = "",
            gradeSymbol = "",
            description = "",
            weight = "",
            weightValue = weightValue,
            date = date,
            teacher = ""
        )
    }

    private fun createGradeSummary(finalGrade: String): GradeSummary {
        return GradeSummary(
            semesterId = 1,
            studentId = 1,
            position = 0,
            subject = "",
            predictedGrade = "",
            finalGrade = finalGrade,
            proposedPoints = "",
            finalPoints = "",
            pointsSum = "",
            average = .0
        )
    }
}
