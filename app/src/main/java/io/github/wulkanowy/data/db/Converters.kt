package io.github.wulkanowy.data.db

import androidx.room.TypeConverter
import io.github.wulkanowy.data.enums.MessageType
import io.github.wulkanowy.ui.modules.Destination
import io.github.wulkanowy.utils.toTimestamp
import kotlinx.serialization.SerializationException
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.time.*
import java.util.*
import java.time.Instant
import java.time.LocalDate
import java.time.Month
import java.time.ZoneOffset
import java.util.*

class Converters {

    private val json = Json

    @TypeConverter
    fun timestampToLocalDate(value: Long?): LocalDate? =
        value?.let(::Date)?.toInstant()?.atZone(ZoneOffset.UTC)?.toLocalDate()

    @TypeConverter
    fun dateToTimestamp(date: LocalDate?): Long? = date?.toTimestamp()

    @TypeConverter
    fun instantToTimestamp(instant: Instant?): Long? = instant?.toEpochMilli()

    @TypeConverter
    fun timestampToInstant(timestamp: Long?): Instant? = timestamp?.let(Instant::ofEpochMilli)

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

    @TypeConverter
    fun destinationToString(destination: Destination) = json.encodeToString(destination)

    @TypeConverter
    fun stringToDestination(destination: String): Destination = json.decodeFromString(destination)

    @TypeConverter
    fun messageTypesToString(types: List<MessageType>): String = json.encodeToString(types)

    @TypeConverter
    fun stringToMessageTypes(text: String): List<MessageType> = json.decodeFromString(text)
}
