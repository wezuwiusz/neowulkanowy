package io.github.wulkanowy.data.db

import android.content.SharedPreferences
import androidx.core.content.edit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SharedPrefProvider @Inject constructor(private val sharedPref: SharedPreferences) {

    fun putLong(key: String, value: Long, sync: Boolean = false) {
        sharedPref.edit(sync) { putLong(key, value) }
    }

    fun getLong(key: String, defaultValue: Long) = sharedPref.getLong(key, defaultValue)

    fun delete(key: String) {
        sharedPref.edit().remove(key).apply()
    }
}
