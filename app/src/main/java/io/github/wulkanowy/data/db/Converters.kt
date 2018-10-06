package io.github.wulkanowy.data.db

import android.arch.persistence.room.TypeConverter
import org.threeten.bp.*
import java.util.*

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
}
