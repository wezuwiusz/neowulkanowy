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
                Grade("", "", "", "", 5, 0.33
                        , "", "", "", "", "",
                        6, localDate, ""),
                Grade("", "", "", "", 5, -0.33
                        , "", "", "", "", "",
                        5, localDate, ""),
                Grade("", "", "", "", 4, 0.0
                        , "", "", "", "", "",
                        1, localDate, ""),
                Grade("", "", "", "", 1, 0.5
                        , "", "", "", "", "",
                        9, localDate, ""),
                Grade("", "", "", "", 0, 0.0
                        , "", "", "", "", "",
                        0, localDate, "")
        ).calcAverage(), 0.005)
    }

    @Test
    fun calcSummaryAverage() {
        assertEquals(2.5, listOf(
                GradeSummary(0, "", "", "", "",
                        "5"),
                GradeSummary(0, "", "", "", "",
                        "-5"),
                GradeSummary(0, "", "", "", "",
                        "test"),
                GradeSummary(0, "", "", "", "",
                        "0")
        ).calcAverage(), 0.005)
    }
}
