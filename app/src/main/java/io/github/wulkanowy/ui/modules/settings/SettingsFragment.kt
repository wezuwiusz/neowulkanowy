package io.github.wulkanowy.ui.modules.settings

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import com.takisoft.preferencex.PreferenceFragmentCompat
import io.github.wulkanowy.R
import io.github.wulkanowy.ui.modules.main.MainView

class SettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener,
    MainView.TitledView {

    companion object {
        fun newInstance() = SettingsFragment()
    }

    override val titleStringId: Int
        get() = R.string.settings_title

    override fun onCreatePreferencesFix(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.scheme_preferences)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String) {
        when(key) {
            "theme" -> {
                AppCompatDelegate.setDefaultNightMode(sharedPreferences?.getString("theme", "1")?.toInt() ?: 1)
                activity?.recreate()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
    }
}
