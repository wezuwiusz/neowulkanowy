package io.github.wulkanowy.utils;

import org.junit.Assert;
import org.junit.Test;
import org.threeten.bp.LocalDate;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class TimeUtilsTest {

    @Test
    public void getTicksDateObjectTest() throws Exception {
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy", Locale.ROOT);
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = format.parse("31.07.2017");

        Assert.assertEquals(636370560000000000L, TimeUtils.getNetTicks(date));

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_YEAR, -14);
        Date dateTwoWeekBefore = calendar.getTime();

        Assert.assertEquals(636358464000000000L, TimeUtils.getNetTicks(dateTwoWeekBefore));
    }

    @Test(expected = ParseException.class)
    public void getTicsStringInvalidFormatTest() throws Exception {
        Assert.assertEquals(636370560000000000L, TimeUtils.getNetTicks("31.07.2017", "dd.MMM.yyyy"));
    }

    @Test
    public void getTicsStringFormatTest() throws Exception {
        Assert.assertEquals(636370560000000000L, TimeUtils.getNetTicks("31.07.2017", "dd.MM.yyyy"));
    }

    @Test
    public void getTicsStringTest() throws Exception {
        Assert.assertEquals(636370560000000000L, TimeUtils.getNetTicks("2017-07-31"));
        Assert.assertEquals(636334272000000000L, TimeUtils.getNetTicks("2017-06-19"));
        Assert.assertEquals(636189120000000000L, TimeUtils.getNetTicks("2017-01-02"));
        Assert.assertEquals(636080256000000000L, TimeUtils.getNetTicks("2016-08-29"));
    }

    @Test
    public void getParsedDateTest() {
        Assert.assertEquals(LocalDate.of(1970, 1, 1), TimeUtils.getParsedDate("1970-01-01", "yyyy-MM-dd"));
    }

    @Test
    public void getDateTest() throws Exception {
        DateFormat format = new SimpleDateFormat("dd.MM.yyyy", Locale.ROOT);
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = format.parse("31.07.2017");

        Assert.assertEquals(date, TimeUtils.getDate(636370560000000000L));
    }

    @Test
    public void isDateInWeekInsideTest() {
        Assert.assertTrue(TimeUtils.isDateInWeek(
                LocalDate.of(2018, 5, 28),
                LocalDate.of(2018, 5, 31)
        ));
    }

    @Test
    public void isDateInWeekExtremesTest() {
        Assert.assertTrue(TimeUtils.isDateInWeek(
                LocalDate.of(2018, 5, 28),
                LocalDate.of(2018, 5, 28)
        ));

        Assert.assertTrue(TimeUtils.isDateInWeek(
                LocalDate.of(2018, 5, 28),
                LocalDate.of(2018, 6, 1)
        ));
    }

    @Test
    public void isDateInWeekOutOfTest() {
        Assert.assertFalse(TimeUtils.isDateInWeek(
                LocalDate.of(2018, 5, 28),
                LocalDate.of(2018, 6, 2)
        ));

        Assert.assertFalse(TimeUtils.isDateInWeek(
                LocalDate.of(2018, 5, 28),
                LocalDate.of(2018, 5, 27)
        ));
    }
}
