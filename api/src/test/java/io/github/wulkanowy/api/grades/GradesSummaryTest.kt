package io.github.wulkanowy.api.grades

import io.github.wulkanowy.api.StudentAndParentTestCase
import org.junit.Assert.assertEquals
import org.junit.Test

class GradesSummaryTest : StudentAndParentTestCase() {

    private val std by lazy { GradesSummary(getSnp("OcenyWszystkie-subjects.html")) }

    private val average by lazy { GradesSummary(getSnp("OcenyWszystkie-subjects-average.html")) }

    @Test fun getSummaryTest() {
        assertEquals(5, std.getSummary().size)
        assertEquals(5, average.getSummary().size)
    }

    @Test fun getNameTest() {
        assertEquals("Zachowanie", std.getSummary()[0].name)
        assertEquals("Praktyka zawodowa", std.getSummary()[1].name)
        assertEquals("Metodologia programowania", std.getSummary()[2].name)
        assertEquals("Podstawy przedsiębiorczości", std.getSummary()[3].name)
        assertEquals("Wychowanie do życia w rodzinie", std.getSummary()[4].name)

        assertEquals("Zachowanie", average.getSummary()[0].name)
        assertEquals("Język polski", average.getSummary()[1].name)
        assertEquals("Wychowanie fizyczne", average.getSummary()[2].name)
        assertEquals("Język angielski", average.getSummary()[3].name)
        assertEquals("Wiedza o społeczeństwie", average.getSummary()[4].name)
    }

    @Test fun getPredictedRatingTest() {
        assertEquals("bardzo dobre", std.getSummary()[0].predicted)
        assertEquals("-", std.getSummary()[1].predicted)
        assertEquals("bardzo dobry", std.getSummary()[2].predicted)
        assertEquals("3/4", std.getSummary()[3].predicted)
        assertEquals("-", std.getSummary()[4].predicted)

        assertEquals("bardzo dobre", average.getSummary()[0].predicted)
        assertEquals("-", average.getSummary()[1].predicted)
        assertEquals("bardzo dobry", average.getSummary()[2].predicted)
        assertEquals("4/5", average.getSummary()[3].predicted)
        assertEquals("-", average.getSummary()[4].predicted)
    }

    @Test fun getFinalRatingTest() {
        assertEquals("bardzo dobre", std.getSummary()[0].final)
        assertEquals("celujący", std.getSummary()[1].final)
        assertEquals("celujący", std.getSummary()[2].final)
        assertEquals("dostateczny", std.getSummary()[3].final)
        assertEquals("-", std.getSummary()[4].final)

        assertEquals("bardzo dobre", average.getSummary()[0].final)
        assertEquals("dobry", average.getSummary()[1].final)
        assertEquals("celujący", average.getSummary()[2].final)
        assertEquals("bardzo dobry", average.getSummary()[3].final)
        assertEquals("-", average.getSummary()[4].final)
    }
}
