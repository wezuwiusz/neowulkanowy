package io.github.wulkanowy.api

import org.junit.Assert
import org.junit.Test
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class DateTimeUtilsTest {

    @Test
    fun getTicksDateObjectTest() {
        val format = SimpleDateFormat("dd.MM.yyyy", Locale.ROOT)
        format.timeZone = TimeZone.getTimeZone("UTC")
        val date = format.parse("31.07.2017")

        Assert.assertEquals(636370560000000000L, getDateAsTick(date))

        val calendar = Calendar.getInstance()
        calendar.time = date
        calendar.add(Calendar.DAY_OF_YEAR, -14)
        val dateTwoWeekBefore = calendar.time

        Assert.assertEquals(636358464000000000L, getDateAsTick(dateTwoWeekBefore))
    }

    @Test(expected = ParseException::class)
    fun getTicsStringInvalidFormatTest() {
        Assert.assertEquals(636370560000000000L, getDateAsTick("31.07.2017", "dd.MMM.yyyy"))
    }

    @Test
    fun getTicsStringFormatTest() {
        Assert.assertEquals(636370560000000000L, getDateAsTick("31.07.2017", "dd.MM.yyyy"))
    }

    @Test
    fun getTicsStringTest() {
        Assert.assertEquals("636370560000000000", getDateAsTick("2017-07-31"))
        Assert.assertEquals("636334272000000000", getDateAsTick("2017-06-19"))
        Assert.assertEquals("636189120000000000", getDateAsTick("2017-01-02"))
        Assert.assertEquals("636080256000000000", getDateAsTick("2016-08-29"))
    }

    @Test
    fun getDateTest() {
        val format = SimpleDateFormat("dd.MM.yyyy", Locale.ROOT)
        format.timeZone = TimeZone.getTimeZone("UTC")
        val date = format.parse("31.07.2017")

        Assert.assertEquals(date, getDate(636370560000000000L))
    }
}
