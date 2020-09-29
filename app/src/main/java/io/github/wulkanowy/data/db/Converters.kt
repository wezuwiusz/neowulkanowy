package io.github.wulkanowy.data.db

import androidx.room.TypeConverter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import io.github.wulkanowy.data.db.adapters.PairAdapterFactory
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Month
import java.time.ZoneOffset
import java.util.Date

class Converters {

    private val moshi by lazy { Moshi.Builder().add(PairAdapterFactory).build() }

    private val integerListAdapter by lazy {
        moshi.adapter<List<Int>>(Types.newParameterizedType(List::class.java, Integer::class.java))
    }

    private val stringListPairAdapter by lazy {
        moshi.adapter<List<Pair<String, String>>>(Types.newParameterizedType(List::class.java, Pair::class.java, String::class.java, String::class.java))
    }

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
        return integerListAdapter.toJson(list)
    }

    @TypeConverter
    fun jsonToIntList(value: String): List<Int> {
        return integerListAdapter.fromJson(value).orEmpty()
    }

    @TypeConverter
    fun stringPairListToJson(list: List<Pair<String, String>>): String {
        return stringListPairAdapter.toJson(list)
    }

    @TypeConverter
    fun jsonToStringPairList(value: String): List<Pair<String, String>> {
        return stringListPairAdapter.fromJson(value).orEmpty()
    }
}
