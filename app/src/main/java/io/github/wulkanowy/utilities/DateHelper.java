package io.github.wulkanowy.utilities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateHelper {

    private static final long TICKS_AT_EPOCH = 621355968000000000L;

    private static final long TICKS_PER_MILLISECOND = 10000;

    private DateHelper() {
        throw new IllegalStateException("Utility class");
    }

    public static long getTicks(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        return (calendar.getTimeInMillis() * TICKS_PER_MILLISECOND) + TICKS_AT_EPOCH;
    }

    public static long getTics(String dateString) throws ParseException {
        return getTics(dateString, "dd.MM.yyyy");
    }

    public static long getTics(String dateString, String dateFormat) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat(dateFormat, Locale.ROOT);
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date dateObject = format.parse(dateString);

        return getTicks(dateObject);
    }

    public static Date getDate(long ticks) {
        return new Date((ticks - TICKS_AT_EPOCH) / TICKS_PER_MILLISECOND);
    }
}
