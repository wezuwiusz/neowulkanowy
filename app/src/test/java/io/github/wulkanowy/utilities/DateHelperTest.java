package io.github.wulkanowy.utilities;

import org.junit.Assert;
import org.junit.Test;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateHelperTest {

    @Test
    public void getTicksDateObjectTest() throws Exception {
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy", Locale.ROOT);
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = format.parse("31.07.2017");

        Assert.assertEquals(636370560000000000L, DateHelper.getTicks(date));

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_YEAR, -14);
        Date dateTwoWeekBefore = calendar.getTime();

        Assert.assertEquals(636358464000000000L, DateHelper.getTicks(dateTwoWeekBefore));
    }

    @Test(expected = ParseException.class)
    public void getTicsStringInvalidFormatTest() throws Exception {
        Assert.assertEquals(636370560000000000L, DateHelper.getTics("31.07.2017", "dd.MMM.yyyy"));
    }

    @Test
    public void getTicsStringFormatTest() throws Exception {
        Assert.assertEquals(636370560000000000L, DateHelper.getTics("31.07.2017", "dd.MM.yyyy"));
    }

    @Test
    public void getTicsStringTest() throws Exception {
        Assert.assertEquals(636370560000000000L, DateHelper.getTics("31.07.2017"));
        Assert.assertEquals(636334272000000000L, DateHelper.getTics("19.06.2017"));
        Assert.assertEquals(636189120000000000L, DateHelper.getTics("02.01.2017"));
        Assert.assertEquals(636080256000000000L, DateHelper.getTics("29.08.2016"));
    }

    @Test
    public void getDateTest() throws Exception {
        DateFormat format = new SimpleDateFormat("dd.MM.yyyy", Locale.ROOT);
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = format.parse("31.07.2017");

        Assert.assertEquals(date, DateHelper.getDate(636370560000000000L));
    }
}
