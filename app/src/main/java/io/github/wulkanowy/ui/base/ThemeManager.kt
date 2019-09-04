package io.github.wulkanowy.ui.base

import android.content.pm.PackageManager.GET_ACTIVITIES
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import io.github.wulkanowy.R
import io.github.wulkanowy.data.repositories.preferences.PreferencesRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ThemeManager @Inject constructor(private val preferencesRepository: PreferencesRepository) {

    fun applyActivityTheme(activity: AppCompatActivity) {
        if (isThemeApplicable(activity)) {
            applyDefaultTheme()
            if (preferencesRepository.appTheme == "black") activity.setTheme(R.style.WulkanowyTheme_Black)
        }
    }

    fun applyDefaultTheme() {
        AppCompatDelegate.setDefaultNightMode(
            if (preferencesRepository.appTheme == "light") MODE_NIGHT_NO
            else MODE_NIGHT_YES
        )
    }

    private fun isThemeApplicable(activity: AppCompatActivity): Boolean {
        return activity.packageManager.getPackageInfo(activity.packageName, GET_ACTIVITIES)
            .activities.singleOrNull { it.name == activity::class.java.canonicalName }?.theme
            .let { it == R.style.WulkanowyTheme_Black || it == R.style.WulkanowyTheme_NoActionBar }
    }
}
