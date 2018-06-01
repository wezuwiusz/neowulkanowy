package io.github.wulkanowy.utils;

import org.junit.Assert;
import org.junit.Test;
import org.threeten.bp.LocalDate;

public class TimeUtilsTest {

    @Test
    public void getParsedDateTest() {
        Assert.assertEquals(LocalDate.of(1970, 1, 1),
                TimeUtils.getParsedDate("1970-01-01", "yyyy-MM-dd"));
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
