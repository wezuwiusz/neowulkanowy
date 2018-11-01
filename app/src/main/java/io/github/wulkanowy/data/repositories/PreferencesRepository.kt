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

    val showPresent: Boolean
        get() = sharedPref.getBoolean(context.getString(R.string.pref_key_attendance_present), true)

    val currentThemeKey: String = context.getString(R.string.pref_key_theme)
    val currentTheme: Int
        get() = sharedPref.getString(currentThemeKey, "1")?.toInt() ?: 1

    val serviceEnablesKey: String = context.getString(R.string.pref_key_services_enable)
    val serviceEnabled: Boolean
        get() = sharedPref.getBoolean(serviceEnablesKey, true)

    val servicesIntervalKey: String = context.getString(R.string.pref_key_services_interval)
    val servicesInterval: Int
        get() = sharedPref.getString(servicesIntervalKey, "60")?.toInt() ?: 60

    val servicesOnlyWifiKey: String = context.getString(R.string.pref_key_services_wifi_only)
    val servicesOnlyWifi: Boolean
        get() = sharedPref.getBoolean(servicesOnlyWifiKey, true)

    val notificationsEnable: Boolean
        get() = sharedPref.getBoolean(context.getString(R.string.pref_key_notifications_enable), true)
}
