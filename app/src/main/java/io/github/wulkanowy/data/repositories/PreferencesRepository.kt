package io.github.wulkanowy.data.repositories

import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesRepository @Inject constructor(private val sharedPref: SharedPreferences) {

    val startMenuIndex: Int
        get() = sharedPref.getString("start_menu", "0")?.toInt() ?: 0

    val showPresent: Boolean
        get() = sharedPref.getBoolean("attendance_present", true)

    val currentTheme: Int
        get() = sharedPref.getString("theme", "1")?.toInt() ?: 1
}

