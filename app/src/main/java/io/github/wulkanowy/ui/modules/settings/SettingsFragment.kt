package io.github.wulkanowy.ui.modules.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import io.github.wulkanowy.R
import io.github.wulkanowy.ui.modules.main.MainView
import timber.log.Timber

class SettingsFragment : PreferenceFragmentCompat(), MainView.TitledView {

    companion object {

        fun newInstance() = SettingsFragment()
    }

    override val titleStringId get() = R.string.settings_title

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.scheme_preferences, rootKey)
        Timber.i("Settings view was initialized")
    }
}
