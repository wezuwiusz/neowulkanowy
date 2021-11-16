package io.github.wulkanowy.data.db

import androidx.room.TypeConverter
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Month
import java.time.ZoneOffset
import java.util.Date

class Converters {

    private val json = Json

    @TypeConverter
    fun timestampToDate(value: Long?): LocalDate? = value?.run {
        Date(value).toInstant().atZone(ZoneOffset.UTC).toLocalDate()
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

    @TypeConverter
    fun intListToJson(list: List<Int>): String {
        return json.encodeToString(list)
    }

    @TypeConverter
    fun jsonToIntList(value: String): List<Int> {
        return json.decodeFromString(value)
    }

    @TypeConverter
    fun stringPairListToJson(list: List<Pair<String, String>>): String {
        return json.encodeToString(list)
    }

    @TypeConverter
    fun jsonToStringPairList(value: String): List<Pair<String, String>> {
        return try {
            json.decodeFromString(value)
        } catch (e: SerializationException) {
            emptyList() // handle errors from old gson Pair serialized data
        }
    }
}
