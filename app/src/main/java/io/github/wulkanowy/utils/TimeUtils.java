package io.github.wulkanowy.utils;

import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public final class TimeUtils {

    private static final long TICKS_AT_EPOCH = 621355968000000000L;

    private static final long TICKS_PER_MILLISECOND = 10000;

    private TimeUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static long getNetTicks(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        return (calendar.getTimeInMillis() * TICKS_PER_MILLISECOND) + TICKS_AT_EPOCH;
    }

    public static long getNetTicks(String dateString) throws ParseException {
        return getNetTicks(dateString, "dd.MM.yyyy");
    }

    public static long getNetTicks(String dateString, String dateFormat) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat(dateFormat, Locale.ROOT);
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date dateObject = format.parse(dateString);

        return getNetTicks(dateObject);
    }

    public static Date getDate(long netTicks) {
        return new Date((netTicks - TICKS_AT_EPOCH) / TICKS_PER_MILLISECOND);
    }

    public static List<String> getMondaysFromCurrentSchoolYear(String dateFormat) {
        LocalDate startDate = new LocalDate(getCurrentSchoolYear(), 9, 1);
        LocalDate endDate = new LocalDate(getCurrentSchoolYear() + 1, 8, 31);

        List<String> dateList = new ArrayList<>();

        LocalDate thisMonday = startDate.withDayOfWeek(DateTimeConstants.MONDAY);

        if (startDate.isAfter(thisMonday)) {
            startDate = thisMonday.plusWeeks(1);
        } else {
            startDate = thisMonday;
        }

        while (startDate.isBefore(endDate)) {
            dateList.add(startDate.toString(dateFormat));
            startDate = startDate.plusWeeks(1);
        }
        return dateList;
    }

    public static int getCurrentSchoolYear() {
        DateTime dateTime = new DateTime();
        return dateTime.getMonthOfYear() <= 8 ? dateTime.getYear() - 1 : dateTime.getYear();
    }
}
