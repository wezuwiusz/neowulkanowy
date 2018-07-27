package io.github.wulkanowy.api.exams

import io.github.wulkanowy.api.StudentAndParentTestCase
import org.junit.Assert.assertEquals
import org.junit.Test

class ExamsTest : StudentAndParentTestCase() {

    private val onePerDay by lazy { Exams(getSnp("Sprawdziany-one-per-day.html")) }

    private val empty by lazy { Exams(getSnp("Sprawdziany-empty.html")) }

    @Test fun getExamsSizeTest() {
        assertEquals(6, onePerDay.getExams().size)
        assertEquals(0, empty.getExams().size)
    }

    @Test fun getExamsDateTest() {
        assertEquals("2017-10-23", onePerDay.getExams()[0].date)
        assertEquals("2017-10-24", onePerDay.getExams()[1].date)
        assertEquals("2017-10-25", onePerDay.getExams()[2].date)
        assertEquals("2017-10-25", onePerDay.getExams()[3].date)
        assertEquals("2017-10-26", onePerDay.getExams()[4].date)
        assertEquals("2017-10-27", onePerDay.getExams()[5].date)
    }

    @Test fun getExamSubjectTest() {
        assertEquals("Sieci komputerowe", onePerDay.getExams()[0].subject)
        assertEquals("Język angielski", onePerDay.getExams()[1].subject)
        assertEquals("Język polski", onePerDay.getExams()[4].subject)
        assertEquals("Metodologia programowania", onePerDay.getExams()[5].subject)
    }

    @Test fun getExamGroupTest() {
        assertEquals("zaw2", onePerDay.getExams()[0].group)
        assertEquals("J1", onePerDay.getExams()[1].group)
        assertEquals("", onePerDay.getExams()[4].group)
    }

    @Test fun getExamTypeTest() {
        assertEquals("Sprawdzian", onePerDay.getExams()[0].type)
        assertEquals("Sprawdzian", onePerDay.getExams()[1].type)
        assertEquals("Sprawdzian", onePerDay.getExams()[2].type)
        assertEquals("Kartkówka", onePerDay.getExams()[3].type)
    }

    @Test fun getExamDescriptionTest()  {
        assertEquals("Łącza danych", onePerDay.getExams()[0].description)
        assertEquals("Czasy teraźniejsze", onePerDay.getExams()[1].description)
        assertEquals("", onePerDay.getExams()[5].description)
    }

    @Test fun getExamTeacherTest() {
        assertEquals("Adam Wiśniewski", onePerDay.getExams()[0].teacher)
        assertEquals("Natalia Nowak", onePerDay.getExams()[1].teacher)
        assertEquals("Małgorzata Nowacka", onePerDay.getExams()[5].teacher)
    }

    @Test fun getExamTeacherSymbolTest() {
        assertEquals("AW", onePerDay.getExams()[0].teacherSymbol)
        assertEquals("NN", onePerDay.getExams()[1].teacherSymbol)
        assertEquals("MN", onePerDay.getExams()[5].teacherSymbol)
    }

    @Test fun getExamEntryDateTest() {
        assertEquals("2017-10-16", onePerDay.getExams()[0].entryDate)
        assertEquals("2017-10-17", onePerDay.getExams()[1].entryDate)
        assertEquals("2017-10-16", onePerDay.getExams()[5].entryDate)
    }
}
