package io.github.wulkanowy.data.db

import android.annotation.SuppressLint
import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedPrefHelper @Inject constructor(private val sharedPref: SharedPreferences) {

    @SuppressLint("ApplySharedPref")
    fun putLong(key: String, value: Long, sync: Boolean = false) {
        sharedPref.edit().putLong(key, value).apply {
            if (sync) commit() else apply()
        }
    }

    fun getLong(key: String, defaultValue: Long): Long {
        return sharedPref.getLong(key, defaultValue)
    }

    fun putBoolean(key: String, value: Boolean) {
        sharedPref.edit().putBoolean(key, value).apply()
    }

    fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return sharedPref.getBoolean(key, defaultValue)
    }

    fun delete(key: String) {
        sharedPref.edit().remove(key).apply()
    }
}
