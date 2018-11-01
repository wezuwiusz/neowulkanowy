package io.github.wulkanowy.ui.modules.settings

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.takisoft.preferencex.PreferenceFragmentCompat
import dagger.android.support.AndroidSupportInjection
import io.github.wulkanowy.R
import io.github.wulkanowy.ui.modules.main.MainView
import javax.inject.Inject

class SettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener,
    MainView.TitledView, SettingsView {

    @Inject
    lateinit var presenter: SettingsPresenter

    companion object {
        fun newInstance() = SettingsFragment()
    }

    override val titleStringId: Int
        get() = R.string.settings_title

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        presenter.onAttachView(this)
    }

    override fun onCreatePreferencesFix(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.scheme_preferences)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        presenter.onSharedPreferenceChanged(key)
    }

    override fun setTheme(theme: Int) {
        AppCompatDelegate.setDefaultNightMode(theme)
        activity?.recreate()
    }

    override fun setServicesSuspended(serviceEnablesKey: String, isHolidays: Boolean) {
        findPreference(serviceEnablesKey).run {
            summary = if (isHolidays) getString(R.string.pref_services_suspended) else ""
            isEnabled = !isHolidays
        }
    }

    override fun showMessage(text: String) {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show()
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
