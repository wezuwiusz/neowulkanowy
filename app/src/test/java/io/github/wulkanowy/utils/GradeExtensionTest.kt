package io.github.wulkanowy.utils

import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Grade
import io.github.wulkanowy.data.db.entities.GradeSummary
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class GradeExtensionTest {

    @MockK
    lateinit var date: LocalDate

    @Before
    fun before() {
        MockKAnnotations.init(this)
    }

    @Test
    fun calcWeightedAverage() {
        assertEquals(3.47, listOf(
            createGrade(5.0, 6.0, 0.33),
            createGrade(5.0, 5.0, -0.33),
            createGrade(4.0, 1.0, 0.0),
            createGrade(1.0, 9.0, 0.5),
            createGrade(0.0, .0, 0.0)
        ).calcAverage(false), 0.005)
    }

    @Test
    fun calcSummaryAverage() {
        assertEquals(3.5, listOf(
            createGradeSummary("4"),
            createGradeSummary("5+"),
            createGradeSummary("5-"),
            createGradeSummary("test"),
            createGradeSummary("0")
        ).calcAverage(0.5, 0.5), 0.005)
    }

    @Test
    fun getBackgroundColor() {
        assertEquals(R.color.grade_material_five, createGrade(5.0).getBackgroundColor("material"))
        assertEquals(R.color.grade_material_five, createGrade(5.5).getBackgroundColor("material"))
        assertEquals(R.color.grade_material_five, createGrade(5.9).getBackgroundColor("material"))
        assertEquals(R.color.grade_vulcan_five, createGrade(5.9).getBackgroundColor("whatever"))
    }

    @Test
    fun changeModifier_zero() {
        assertEquals(.0, createGrade(5.0, .0, .5).changeModifier(.0, .0).modifier, .0)
        assertEquals(.0, createGrade(5.0, .0, -.5).changeModifier(.0, .0).modifier, .0)
    }

    @Test
    fun changeModifier_plus() {
        assertEquals(.33, createGrade(5.0, .0, .25).changeModifier(.33, .50).modifier, .0)
        assertEquals(.25, createGrade(5.0, .0, .33).changeModifier(.25, .0).modifier, .0)
    }

    @Test
    fun changeModifier_minus() {
        assertEquals(-.33, createGrade(5.0, .0, -.25).changeModifier(.25, .33).modifier, .0)
        assertEquals(-.25, createGrade(5.0, .0, -.33).changeModifier(.0, .25).modifier, .0)
    }

    private fun createGrade(value: Double, weightValue: Double = .0, modifier: Double = 0.25): Grade {
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
