package io.github.wulkanowy.data.db

import android.arch.persistence.room.TypeConverter
import java.util.*

class Converters {

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? = value?.run { Date(value) }


    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? = date?.time
}
