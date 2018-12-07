package io.github.wulkanowy.data.db

import androidx.room.TypeConverter
import org.threeten.bp.DateTimeUtils
import org.threeten.bp.Instant
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import org.threeten.bp.Month
import org.threeten.bp.ZoneOffset
import java.util.Date

class Converters {

    @TypeConverter
    fun timestampToDate(value: Long?): LocalDate? = value?.run {
        DateTimeUtils.toInstant(Date(value)).atZone(ZoneOffset.UTC).toLocalDate()
    }

    @TypeConverter
    fun dateToTimestamp(date: LocalDate?): Long? {
        return date?.atStartOfDay()?.toInstant(ZoneOffset.UTC)?.toEpochMilli()
    }

    @TypeConverter
    fun timestampToTime(value: Long?): LocalDateTime? = value?.let {
        LocalDateTime.ofInstant(Instant.ofEpochMilli(value), ZoneOffset.UTC)
    }

    @TypeConverter
    fun timeToTimestamp(date: LocalDateTime?): Long? {
        return date?.atZone(ZoneOffset.UTC)?.toInstant()?.toEpochMilli()
    }

    @TypeConverter
    fun monthToInt(month: Month?) = month?.value

    @TypeConverter
    fun intToMonth(value: Int?) = value?.let { Month.of(it) }
}
