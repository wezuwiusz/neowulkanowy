package io.github.wulkanowy.api

import java.text.SimpleDateFormat
import java.util.*

const val LOG_DATE_PATTERN = "dd.MM.yyyy"
const val API_DATE_PATTERN = "yyyy-MM-dd"

const val TICKS_AT_EPOCH = 621355968000000000L
const val TICKS_PER_MILLISECOND = 10000

fun getFormattedDate(date: String?): String {
    return getFormattedDate(date, API_DATE_PATTERN)
}

fun getFormattedDate(date: String?, format: String): String {
    return getFormattedDate(date, LOG_DATE_PATTERN, format)
}

fun getFormattedDate(date: String?, fromFormat: String, toFormat: String): String {
    if (date.isNullOrEmpty()) return ""
    val sdf = SimpleDateFormat(fromFormat, Locale.ROOT)
    val d = sdf.parse(date)
    sdf.applyPattern(toFormat)

    return sdf.format(d)
}

fun getDateAsTick(dateString: String?): String {
    if (dateString.isNullOrEmpty()) {
        return ""
    }

    return getDateAsTick(dateString as String, API_DATE_PATTERN).toString()
}

fun getDateAsTick(dateString: String, dateFormat: String): Long {
    val format = SimpleDateFormat(dateFormat, Locale.ROOT)
    format.timeZone = TimeZone.getTimeZone("UTC")
    val dateObject = format.parse(dateString)

    return getDateAsTick(dateObject)
}

fun getDateAsTick(date: Date): Long {
    val calendar = Calendar.getInstance()
    calendar.time = date

    return calendar.timeInMillis * TICKS_PER_MILLISECOND + TICKS_AT_EPOCH
}

fun getDate(netTicks: Long): Date {
    return Date((netTicks - TICKS_AT_EPOCH) / TICKS_PER_MILLISECOND)
}
