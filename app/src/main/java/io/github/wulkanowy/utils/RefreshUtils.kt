package io.github.wulkanowy.utils

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.SharedPrefProvider
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.enums.MessageFolder
import timber.log.Timber
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject

fun getRefreshKey(name: String, semester: Semester, start: LocalDate, end: LocalDate): String {
    return "${name}_${semester.studentId}_${semester.semesterId}_${start.monday}_${end.sunday}"
}

fun getRefreshKey(name: String, semester: Semester): String {
    return "${name}_${semester.studentId}_${semester.semesterId}"
}

fun getRefreshKey(name: String, student: Student): String {
    return "${name}_${student.userLoginId}"
}

fun getRefreshKey(name: String, student: Student, folder: MessageFolder): String {
    return "${name}_${student.id}_${folder.id}"
}

class AutoRefreshHelper @Inject constructor(
    @ApplicationContext private val context: Context,
    private val sharedPref: SharedPrefProvider
) {

    fun shouldBeRefreshed(key: String): Boolean {
        val timestamp = sharedPref.getLong(key, 0).toLocalDateTime()
        val servicesInterval = sharedPref.getString(context.getString(R.string.pref_key_services_interval), context.getString(R.string.pref_default_services_interval)).toLong()

        val shouldBeRefreshed = timestamp < LocalDateTime.now().minusMinutes(servicesInterval)

        Timber.d("Check if $key need to be refreshed: $shouldBeRefreshed (last refresh: $timestamp, interval: $servicesInterval min)")

        return shouldBeRefreshed
    }

    fun updateLastRefreshTimestamp(key: String) {
        sharedPref.putLong(key, LocalDateTime.now().toTimestamp())
    }
}
