package io.github.wulkanowy.data.db

import android.arch.persistence.room.TypeConverter
import org.threeten.bp.*
import java.util.*

class Converters {

    @TypeConverter
    fun fromTimestamp(value: Long?): LocalDate? = value?.run {
        DateTimeUtils.toInstant(Date(value)).atZone(ZoneOffset.UTC).toLocalDate()
    }

    @TypeConverter
    fun dateToTimestamp(date: LocalDate?): Long? {
        return date?.atStartOfDay()?.toInstant(ZoneOffset.UTC)?.toEpochMilli()
    }
}
