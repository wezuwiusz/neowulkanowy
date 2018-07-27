package io.github.wulkanowy.api.grades

import io.github.wulkanowy.api.StudentAndParentTestCase
import org.junit.Assert
import org.junit.Test

import org.junit.Assert.*

class GradesTest : StudentAndParentTestCase() {

    private val filled by lazy { Grades(getSnp("OcenyWszystkie-filled.html")) }

    @Test fun getAllTest() {
        Assert.assertEquals(7, filled.getGrades().size) // 2 items are skipped
    }

    @Test fun getSubjectTest() {
        assertEquals("Zajęcia z wychowawcą", filled.getGrades()[0].subject)
        assertEquals("Język angielski", filled.getGrades()[3].subject)
        assertEquals("Wychowanie fizyczne", filled.getGrades()[4].subject)
        assertEquals("Język polski", filled.getGrades()[5].subject)
    }

    @Test fun getValueTest() {
        assertEquals("5", filled.getGrades()[0].value)
        assertEquals("5", filled.getGrades()[3].value)
        assertEquals("1", filled.getGrades()[4].value)
        assertEquals("1", filled.getGrades()[5].value)
    }

    @Test fun getColorTest() {
        assertEquals("000000", filled.getGrades()[0].color)
        assertEquals("1289F7", filled.getGrades()[3].color)
        assertEquals("6ECD07", filled.getGrades()[4].color)
        assertEquals("6ECD07", filled.getGrades()[5].color)
    }

    @Test fun getSymbolTest() {
        assertEquals("A1", filled.getGrades()[0].symbol)
        assertEquals("BW3", filled.getGrades()[3].symbol)
        assertEquals("STR", filled.getGrades()[4].symbol)
        assertEquals("K", filled.getGrades()[5].symbol)
        assertEquals("+Odp", filled.getGrades()[6].symbol)
    }

    @Test fun getDescriptionTest() {
        assertEquals("Dzień Kobiet w naszej klasie", filled.getGrades()[0].description)
        assertEquals("Writing", filled.getGrades()[3].description)
        assertEquals("", filled.getGrades()[4].description)
        assertEquals("Kordian", filled.getGrades()[5].description)
        assertEquals("Kordian", filled.getGrades()[6].description)
    }

    @Test fun getWeightTest() {
        assertEquals("1,00", filled.getGrades()[0].weight)
        assertEquals("3,00", filled.getGrades()[3].weight)
        assertEquals("8,00", filled.getGrades()[4].weight)
        assertEquals("5,00", filled.getGrades()[5].weight)
    }

    @Test fun getDateTest() {
        assertEquals("2017-03-21", filled.getGrades()[0].date)
        assertEquals("2017-06-02", filled.getGrades()[3].date)
        assertEquals("2017-04-02", filled.getGrades()[4].date)
        assertEquals("2017-02-06", filled.getGrades()[5].date)
    }

    @Test fun getTeacherTest() {
        assertEquals("Patryk Maciejewski", filled.getGrades()[0].teacher)
        assertEquals("Oliwia Woźniak", filled.getGrades()[3].teacher)
        assertEquals("Klaudia Dziedzic", filled.getGrades()[4].teacher)
        assertEquals("Amelia Stępień", filled.getGrades()[5].teacher)
    }
}
