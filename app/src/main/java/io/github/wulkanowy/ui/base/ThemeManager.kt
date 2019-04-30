package io.github.wulkanowy.ui.base

import android.content.pm.PackageManager.GET_ACTIVITIES
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import io.github.wulkanowy.R
import io.github.wulkanowy.data.repositories.preferences.PreferencesRepository
import javax.inject.Inject

class ThemeManager @Inject constructor(private val preferencesRepository: PreferencesRepository) {

    fun applyTheme(activity: AppCompatActivity) {
        if (isThemeApplicable(activity)) {
            activity.delegate.apply {
                when (preferencesRepository.appTheme) {
                    "light" -> setLocalNightMode(MODE_NIGHT_NO)
                    "dark" -> setLocalNightMode(MODE_NIGHT_YES)
                    "black" -> {
                        setLocalNightMode(MODE_NIGHT_YES)
                        activity.setTheme(R.style.WulkanowyTheme_Black)
                    }
                }
            }
        }
    }

    private fun isThemeApplicable(activity: AppCompatActivity): Boolean {
        return activity.packageManager.getPackageInfo(activity.packageName, GET_ACTIVITIES)
            .activities.singleOrNull { it.name == activity::class.java.canonicalName }?.theme
            .let { it == R.style.WulkanowyTheme_Black || it == R.style.WulkanowyTheme_NoActionBar }
    }
}
