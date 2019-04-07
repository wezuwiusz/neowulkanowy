package io.github.wulkanowy.data.db

import android.annotation.SuppressLint
import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@SuppressLint("ApplySharedPref")
class SharedPrefHelper @Inject constructor(private val sharedPref: SharedPreferences) {

    fun putLong(key: String, value: Long, sync: Boolean = false) {
        sharedPref.edit().putLong(key, value).apply {
            if (sync) commit() else apply()
        }
    }

    fun getLong(key: String, defaultValue: Long) = sharedPref.getLong(key, defaultValue)

    fun delete(key: String) {
        sharedPref.edit().remove(key).apply()
    }
}
