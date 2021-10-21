package io.github.wulkanowy.ui.modules.settings.sync

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import dagger.hilt.android.AndroidEntryPoint
import io.github.wulkanowy.R
import io.github.wulkanowy.ui.base.BaseActivity
import io.github.wulkanowy.ui.base.ErrorDialog
import io.github.wulkanowy.ui.modules.main.MainView
import javax.inject.Inject

@AndroidEntryPoint
class SyncFragment : PreferenceFragmentCompat(),
    SharedPreferences.OnSharedPreferenceChangeListener,
    MainView.TitledView, SyncView {

    @Inject
    lateinit var presenter: SyncPresenter

    override val titleStringId get() = R.string.pref_settings_sync_title

    override val syncSuccessString get() = getString(R.string.pref_services_message_sync_success)

    override val syncFailedString get() = getString(R.string.pref_services_message_sync_failed)

    override fun initView() {
        findPreference<Preference>(getString(R.string.pref_key_services_force_sync))?.run {
            onPreferenceClickListener = Preference.OnPreferenceClickListener {
                presenter.onSyncNowClicked()
                true
            }
        }
    }

    override fun setLastSyncDate(lastSyncDate: String) {
        findPreference<Preference>(getString(R.string.pref_key_services_force_sync))?.run {
            summary = getString(R.string.pref_services_last_full_sync_date, lastSyncDate)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.onAttachView(this)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.scheme_preferences_sync, rootKey)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        presenter.onSharedPreferenceChanged(key)
    }

    override fun setServicesSuspended(serviceEnablesKey: String, isHolidays: Boolean) {
        findPreference<Preference>(serviceEnablesKey)?.run {
            summary = if (isHolidays) getString(R.string.pref_services_suspended) else ""
            isEnabled = !isHolidays
        }
    }

    override fun setSyncInProgress(inProgress: Boolean) {
        if (activity == null || !isAdded) return

        findPreference<Preference>(getString(R.string.pref_key_services_force_sync))?.run {
            isEnabled = !inProgress
            summary = if (inProgress) getString(R.string.pref_services_sync_in_progress) else ""
        }
    }

    override fun showError(text: String, error: Throwable) {
        (activity as? BaseActivity<*, *>)?.showError(text, error)
    }

    override fun showMessage(text: String) {
        (activity as? BaseActivity<*, *>)?.showMessage(text)
    }

    override fun showExpiredDialog() {
        (activity as? BaseActivity<*, *>)?.showExpiredDialog()
    }

    override fun showChangePasswordSnackbar(redirectUrl: String) {
        (activity as? BaseActivity<*, *>)?.showChangePasswordSnackbar(redirectUrl)
    }

    override fun openClearLoginView() {
        (activity as? BaseActivity<*, *>)?.openClearLoginView()
    }

    override fun showErrorDetailsDialog(error: Throwable) {
        ErrorDialog.newInstance(error).show(childFragmentManager, error.toString())
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
