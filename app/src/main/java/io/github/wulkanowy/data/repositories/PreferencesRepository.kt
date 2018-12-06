package io.github.wulkanowy.data.repositories

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
        get() = sharedPref.getString(context.getString(R.string.pref_key_start_menu), "0")?.toInt() ?: 0

    val isShowPresent: Boolean
        get() = sharedPref.getBoolean(context.getString(R.string.pref_key_attendance_present), true)

    val isGradeExpandable: Boolean
        get() = !sharedPref.getBoolean(context.getString(R.string.pref_key_expand_grade), false)

    val currentThemeKey: String = context.getString(R.string.pref_key_theme)
    val currentTheme: Int
        get() = sharedPref.getString(currentThemeKey, "1")?.toInt() ?: 1

    val gradePlusModifier: Double
        get() = sharedPref.getString(context.getString(R.string.pref_key_grade_modifier_plus), "0.0")?.toDouble() ?: 0.0

    val gradeMinusModifier: Double
        get() = sharedPref.getString(context.getString(R.string.pref_key_grade_modifier_minus), "0.0")?.toDouble() ?: 0.0

    val serviceEnablesKey: String = context.getString(R.string.pref_key_services_enable)
    val isServiceEnabled: Boolean
        get() = sharedPref.getBoolean(serviceEnablesKey, true)

    val servicesIntervalKey: String = context.getString(R.string.pref_key_services_interval)
    val servicesInterval: Int
        get() = sharedPref.getString(servicesIntervalKey, "60")?.toInt() ?: 60

    val servicesOnlyWifiKey: String = context.getString(R.string.pref_key_services_wifi_only)
    val isServicesOnlyWifi: Boolean
        get() = sharedPref.getBoolean(servicesOnlyWifiKey, true)

    val isNotificationsEnable: Boolean
        get() = sharedPref.getBoolean(context.getString(R.string.pref_key_notifications_enable), true)
}
