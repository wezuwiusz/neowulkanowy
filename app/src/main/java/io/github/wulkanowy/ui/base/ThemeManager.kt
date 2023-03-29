package io.github.wulkanowy.ui.base

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.content.pm.PackageManager.GET_ACTIVITIES
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.material.color.DynamicColors
import io.github.wulkanowy.R
import io.github.wulkanowy.data.enums.AppTheme
import io.github.wulkanowy.data.repositories.PreferencesRepository
import io.github.wulkanowy.ui.modules.login.LoginActivity
import io.github.wulkanowy.ui.modules.luckynumberwidget.LuckyNumberWidgetConfigureActivity
import io.github.wulkanowy.ui.modules.main.MainActivity
import io.github.wulkanowy.ui.modules.timetablewidget.TimetableWidgetConfigureActivity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ThemeManager @Inject constructor(private val preferencesRepository: PreferencesRepository) {

    fun applyActivityTheme(activity: AppCompatActivity) {
        if (isThemeApplicable(activity)) {
            applyDefaultTheme()
            if (preferencesRepository.appTheme == AppTheme.BLACK) {
                when (activity) {
                    is MainActivity -> activity.setTheme(R.style.WulkanowyTheme_Black)
                    is LoginActivity -> activity.setTheme(R.style.WulkanowyTheme_Login_Black)
                }
            }
        } else if (activity is TimetableWidgetConfigureActivity || activity is LuckyNumberWidgetConfigureActivity) {
            DynamicColors.applyToActivityIfAvailable(activity)
        }
    }

    fun applyDefaultTheme() {
        AppCompatDelegate.setDefaultNightMode(
            when (preferencesRepository.appTheme) {
                AppTheme.LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
                AppTheme.DARK, AppTheme.BLACK -> AppCompatDelegate.MODE_NIGHT_YES
                AppTheme.SYSTEM -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            }
        )
    }

    private fun isThemeApplicable(activity: AppCompatActivity): Boolean =
        getPackageInfo(activity)
            .activities
            .singleOrNull { it.name == activity::class.java.canonicalName }
            ?.theme
            .let {
                it == R.style.WulkanowyTheme_Black || it == R.style.WulkanowyTheme_NoActionBar
                    || it == R.style.WulkanowyTheme_Login || it == R.style.WulkanowyTheme_Login_Black
            }

    @Suppress("DEPRECATION")
    private fun getPackageInfo(activity: AppCompatActivity): PackageInfo {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            activity.packageManager.getPackageInfo(
                activity.packageName,
                PackageManager.PackageInfoFlags.of(GET_ACTIVITIES.toLong())
            )
        } else activity.packageManager.getPackageInfo(activity.packageName, GET_ACTIVITIES)
    }
}
