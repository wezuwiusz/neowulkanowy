package io.github.wulkanowy.api.homework

import io.github.wulkanowy.api.StudentAndParentTestCase
import org.junit.Assert.assertEquals
import org.junit.Test

class HomeworkListTest : StudentAndParentTestCase() {

    private val snp by lazy { getSnp("ZadaniaDomowe.html") }

    @Test fun getHomework() {
        assertEquals(2, HomeworkList(snp).getHomework().size)
    }

    @Test fun getDate() {
        assertEquals("2018-08-13", HomeworkList(snp).getHomework("2018-08-13")[0].date)
        assertEquals("1970-01-01", HomeworkList(snp).getHomework("1970-01-01")[1].date)
    }

    @Test fun getSubject() {
        assertEquals("Sieci komputerowe i administrowanie sieciami", HomeworkList(snp).getHomework()[0].subject)
        assertEquals("Naprawa komputera", HomeworkList(snp).getHomework()[1].subject)
    }

    @Test fun getContent() {
        assertEquals("Zadania egzaminacyjne", HomeworkList(snp).getHomework()[0].content)
        assertEquals("Test diagnozujący", HomeworkList(snp).getHomework()[1].content)
    }

    @Test fun getTeacher() {
        assertEquals("Słowacki Juliusz", HomeworkList(snp).getHomework()[0].teacher)
        assertEquals("Mickiewicz Adam", HomeworkList(snp).getHomework()[1].teacher)
    }

    @Test fun getTeacherSymbol() {
        assertEquals("SJ", HomeworkList(snp).getHomework()[0].teacherSymbol)
        assertEquals("MA", HomeworkList(snp).getHomework()[1].teacherSymbol)
    }

    @Test fun getEntryDate() {
        assertEquals("2017-10-16", HomeworkList(snp).getHomework()[0].entryDate)
        assertEquals("2017-10-25", HomeworkList(snp).getHomework()[1].entryDate)
    }
}
