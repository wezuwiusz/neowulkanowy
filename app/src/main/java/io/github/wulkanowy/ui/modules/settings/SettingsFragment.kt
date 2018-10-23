package io.github.wulkanowy.ui.modules.settings

import android.os.Bundle
import com.takisoft.preferencex.PreferenceFragmentCompat
import io.github.wulkanowy.R
import io.github.wulkanowy.ui.modules.main.MainView

class SettingsFragment : PreferenceFragmentCompat(), MainView.TitledView {

    companion object {
        fun newInstance() = SettingsFragment()
    }

    override val titleStringId: Int
        get() = R.string.settings_title

    override fun onCreatePreferencesFix(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.scheme_preferences)
    }
}
