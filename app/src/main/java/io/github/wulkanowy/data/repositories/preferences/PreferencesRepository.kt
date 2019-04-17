package io.github.wulkanowy.data.repositories.preferences

import android.content.Context
import android.content.SharedPreferences
import io.github.wulkanowy.R
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PreferencesRepository @Inject constructor(
    private val sharedPref: SharedPreferences,
    val context: Context
) {
    val startMenuIndex: Int
        get() = sharedPref.getString(context.getString(R.string.pref_key_start_menu), "0")?.toIntOrNull() ?: 0

    val isShowPresent: Boolean
        get() = sharedPref.getBoolean(context.getString(R.string.pref_key_attendance_present), true)

    val gradeAverageMode: String
        get() = sharedPref.getString(context.getString(R.string.pref_key_grade_average_mode), "only_one_semester") ?: "only_one_semester"

    val isGradeExpandable: Boolean
        get() = !sharedPref.getBoolean(context.getString(R.string.pref_key_expand_grade), false)

    val currentThemeKey: String = context.getString(R.string.pref_key_theme)
    val currentTheme: Int
        get() = sharedPref.getString(currentThemeKey, "1")?.toIntOrNull() ?: 1

    val gradeColorTheme: String
        get() = sharedPref.getString(context.getString(R.string.pref_key_grade_color_scheme), "vulcan") ?: "vulcan"

    val serviceEnableKey: String = context.getString(R.string.pref_key_services_enable)
    val isServiceEnabled: Boolean
        get() = sharedPref.getBoolean(serviceEnableKey, true)

    val servicesIntervalKey: String = context.getString(R.string.pref_key_services_interval)
    val servicesInterval: Long
        get() = sharedPref.getString(servicesIntervalKey, "60")?.toLongOrNull() ?: 60

    val servicesOnlyWifiKey: String = context.getString(R.string.pref_key_services_wifi_only)
    val isServicesOnlyWifi: Boolean
        get() = sharedPref.getBoolean(servicesOnlyWifiKey, true)

    val isNotificationsEnable: Boolean
        get() = sharedPref.getBoolean(context.getString(R.string.pref_key_notifications_enable), true)

    val isDebugNotificationEnableKey: String = context.getString(R.string.pref_key_notification_debug)
    val isDebugNotificationEnable: Boolean
        get() = sharedPref.getBoolean(isDebugNotificationEnableKey, false)

    val gradePlusModifier: Double
        get() = sharedPref.getString(context.getString(R.string.pref_key_grade_modifier_plus), "0.0")?.toDouble() ?: 0.0

    val gradeMinusModifier: Double
        get() = sharedPref.getString(context.getString(R.string.pref_key_grade_modifier_minus), "0.0")?.toDouble() ?: 0.0

    val fillMessageContent: Boolean
        get() = sharedPref.getBoolean(context.getString(R.string.pref_key_fill_message_content), true)
}
