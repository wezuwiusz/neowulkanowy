package io.github.wulkanowy.utils

import io.github.wulkanowy.data.db.entities.Grade
import io.github.wulkanowy.data.db.entities.GradeSummary
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.Mockito.mock
import org.threeten.bp.LocalDate

class GradeExtensionTest {

    @Test
    fun calcWeightedAverage() {
        val localDate = mock(LocalDate::class.java)
        assertEquals(3.47, listOf(
            Grade(1, 1, "", "", 5, 0.33
                , "", "", "", "", "",
                6, localDate, ""),
            Grade(1, 1, "", "", 5, -0.33
                , "", "", "", "", "",
                5, localDate, ""),
            Grade(1, 1, "", "", 4, 0.0
                , "", "", "", "", "",
                1, localDate, ""),
            Grade(1, 1, "", "", 1, 0.5
                , "", "", "", "", "",
                9, localDate, ""),
            Grade(1, 1, "", "", 0, 0.0
                , "", "", "", "", "",
                0, localDate, "")
        ).calcAverage(), 0.005)
    }

    @Test
    fun calcSummaryAverage() {
        assertEquals(2.5, listOf(
            GradeSummary(1, 1, "", "", "5"),
            GradeSummary(1, 1, "", "", "-5"),
            GradeSummary(1, 1, "", "", "test"),
            GradeSummary(1, 1, "", "", "0")
        ).calcAverage(), 0.005)
    }

    @Test
    fun changeModifierTest() {
        val localDate = mock(LocalDate::class.java)
        assertEquals(0.33, Grade(1, 1, "", "", 5, 0.25
            , "", "", "", "", "",
            6, localDate, "").changeModifier(0.33).modifier, 0.0)
        assertEquals(-0.33, Grade(1, 1, "", "", 5, -0.25
            , "", "", "", "", "",
            6, localDate, "").changeModifier(0.33).modifier, 0.0)
        assertEquals(0.25, Grade(1, 1, "", "", 5, 0.25
            , "", "", "", "", "",
            6, localDate, "").changeModifier(0.0).modifier, 0.0)
    }
}
