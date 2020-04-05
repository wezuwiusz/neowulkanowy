package io.github.wulkanowy.ui.modules.settings

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.yariksoffice.lingver.Lingver
import dagger.android.support.AndroidSupportInjection
import io.github.wulkanowy.R
import io.github.wulkanowy.ui.base.BaseActivity
import io.github.wulkanowy.ui.base.ErrorDialog
import io.github.wulkanowy.ui.modules.main.MainView
import io.github.wulkanowy.utils.AppInfo
import javax.inject.Inject

class SettingsFragment : PreferenceFragmentCompat(),
    SharedPreferences.OnSharedPreferenceChangeListener,
    MainView.TitledView, SettingsView {

    @Inject
    lateinit var presenter: SettingsPresenter

    @Inject
    lateinit var appInfo: AppInfo

    @Inject
    lateinit var lingver: Lingver

    companion object {
        fun newInstance() = SettingsFragment()
    }

    override val titleStringId get() = R.string.settings_title

    override val syncSuccessString get() = getString(R.string.pref_services_message_sync_success)

    override val syncFailedString get() = getString(R.string.pref_services_message_sync_failed)

    override fun onAttach(context: Context) {
        AndroidSupportInjection.inject(this)
        super.onAttach(context)
    }

    override fun initView() {
        findPreference<Preference>(getString(R.string.pref_key_services_force_sync))?.run {
            onPreferenceClickListener = Preference.OnPreferenceClickListener {
                presenter.onSyncNowClicked()
                true
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        presenter.onAttachView(this)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.scheme_preferences, rootKey)
        findPreference<Preference>(getString(R.string.pref_key_notification_debug))?.isVisible = appInfo.isDebug
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        presenter.onSharedPreferenceChanged(key)
    }

    override fun recreateView() {
        activity?.recreate()
    }

    override fun updateLanguage(langCode: String) {
        lingver.setLocale(requireContext(), langCode)
    }

    override fun setServicesSuspended(serviceEnablesKey: String, isHolidays: Boolean) {
        findPreference<Preference>(serviceEnablesKey)?.run {
            summary = if (isHolidays) getString(R.string.pref_services_suspended) else ""
            isEnabled = !isHolidays
        }
    }

    override fun setSyncInProgress(inProgress: Boolean) {
        findPreference<Preference>(getString(R.string.pref_key_services_force_sync))?.run {
            isEnabled = !inProgress
            summary = if (inProgress) getString(R.string.pref_services_sync_in_progress) else ""
        }
    }

    override fun showError(text: String, error: Throwable) {
        (activity as? BaseActivity<*>)?.showError(text, error)
    }

    override fun showMessage(text: String) {
        (activity as? BaseActivity<*>)?.showMessage(text)
    }

    override fun showExpiredDialog() {
        (activity as? BaseActivity<*>)?.showExpiredDialog()
    }

    override fun openClearLoginView() {
        (activity as? BaseActivity<*>)?.openClearLoginView()
    }

    override fun showErrorDetailsDialog(error: Throwable) {
        ErrorDialog.newInstance(error).show(childFragmentManager, error.toString())
    }

    override fun showForceSyncDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.pref_services_dialog_force_sync_title)
            .setMessage(R.string.pref_services_dialog_force_sync_summary)
            .setPositiveButton(android.R.string.ok) { _, _ -> presenter.onForceSyncDialogSubmit() }
            .setNegativeButton(android.R.string.cancel) { _, _ -> }
            .show()
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
