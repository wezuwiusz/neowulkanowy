package io.github.wulkanowy.ui.modules.settings.notifications

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.recyclerview.widget.RecyclerView
import com.thelittlefireman.appkillermanager.AppKillerManager
import com.thelittlefireman.appkillermanager.exceptions.NoActionFoundException
import dagger.hilt.android.AndroidEntryPoint
import io.github.wulkanowy.R
import io.github.wulkanowy.ui.base.BaseActivity
import io.github.wulkanowy.ui.base.ErrorDialog
import io.github.wulkanowy.ui.modules.main.MainView
import io.github.wulkanowy.utils.openInternetBrowser
import javax.inject.Inject

@AndroidEntryPoint
class NotificationsFragment : PreferenceFragmentCompat(),
    SharedPreferences.OnSharedPreferenceChangeListener,
    MainView.TitledView, NotificationsView {

    @Inject
    lateinit var presenter: NotificationsPresenter

    companion object {
        fun newInstance() = NotificationsFragment()
    }

    override val titleStringId get() = R.string.pref_settings_notifications_title

    override fun initView(showDebugNotificationSwitch: Boolean) {
        findPreference<Preference>(getString(R.string.pref_key_notification_debug))?.isVisible =
            showDebugNotificationSwitch

        findPreference<Preference>(getString(R.string.pref_key_notifications_fix_issues))?.run {
            isVisible = AppKillerManager.isDeviceSupported()
                && AppKillerManager.isAnyActionAvailable(requireContext())

            setOnPreferenceClickListener {
                presenter.onFixSyncIssuesClicked()
                true
            }
        }
    }

    override fun onCreateRecyclerView(
        inflater: LayoutInflater?,
        parent: ViewGroup?,
        state: Bundle?
    ): RecyclerView? = super.onCreateRecyclerView(inflater, parent, state)
        .also {
            it.itemAnimator = null
            it.layoutAnimation = null
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.onAttachView(this)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.scheme_preferences_notifications, rootKey)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        presenter.onSharedPreferenceChanged(key)
    }

    override fun enableNotification(notificationKey: String, enable: Boolean) {
        findPreference<Preference>(notificationKey)?.run {
            isEnabled = enable
            summary = if (enable) null else getString(R.string.pref_notify_disabled_summary)
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

    override fun openClearLoginView() {
        (activity as? BaseActivity<*, *>)?.openClearLoginView()
    }

    override fun showErrorDetailsDialog(error: Throwable) {
        ErrorDialog.newInstance(error).show(childFragmentManager, error.toString())
    }

    override fun showFixSyncDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.pref_notify_fix_sync_issues)
            .setMessage(R.string.pref_notify_fix_sync_issues_message)
            .setNegativeButton(android.R.string.cancel) { _, _ -> }
            .setPositiveButton(R.string.pref_notify_fix_sync_issues_settings_button) { _, _ ->
                try {
                    AppKillerManager.doActionPowerSaving(requireContext())
                    AppKillerManager.doActionAutoStart(requireContext())
                    AppKillerManager.doActionNotification(requireContext())
                } catch (e: NoActionFoundException) {
                    requireContext().openInternetBrowser(
                        "https://dontkillmyapp.com/${AppKillerManager.getDevice()?.manufacturer}",
                        ::showMessage
                    )
                }
            }
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
