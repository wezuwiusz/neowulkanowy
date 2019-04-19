package io.github.wulkanowy.ui.base

import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import io.github.wulkanowy.R
import io.github.wulkanowy.data.repositories.preferences.PreferencesRepository
import javax.inject.Inject

class ThemeManager @Inject constructor(private val preferencesRepository: PreferencesRepository) {

    fun applyTheme(activity: AppCompatActivity) {
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
