package io.github.wulkanowy.data.db

import android.content.SharedPreferences
import androidx.core.content.edit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedPrefProvider @Inject constructor(
    private val sharedPref: SharedPreferences
) {

    companion object {
        const val APP_VERSION_CODE_KEY = "app_version_code"
    }

    fun putLong(key: String, value: Long, sync: Boolean = false) {
        sharedPref.edit(sync) { putLong(key, value) }
    }

    fun getLong(key: String, defaultValue: Long) = sharedPref.getLong(key, defaultValue)

    fun getString(key: String, defaultValue: String): String = sharedPref.getString(key, defaultValue) ?: defaultValue

    fun putString(key: String, value: String, sync: Boolean = false) {
        sharedPref.edit(sync) { putString(key, value) }
    }

    fun delete(key: String) {
        sharedPref.edit().remove(key).apply()
    }
}
