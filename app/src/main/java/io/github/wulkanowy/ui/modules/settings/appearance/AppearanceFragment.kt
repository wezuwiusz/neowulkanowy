package io.github.wulkanowy.ui.modules.settings.appearance

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SeekBarPreference
import com.yariksoffice.lingver.Lingver
import dagger.hilt.android.AndroidEntryPoint
import io.github.wulkanowy.R
import io.github.wulkanowy.ui.base.BaseActivity
import io.github.wulkanowy.ui.base.ErrorDialog
import io.github.wulkanowy.ui.modules.main.MainView
import io.github.wulkanowy.utils.AppInfo
import javax.inject.Inject

@AndroidEntryPoint
class AppearanceFragment : PreferenceFragmentCompat(),
    SharedPreferences.OnSharedPreferenceChangeListener,
    MainView.TitledView, AppearanceView {

    @Inject
    lateinit var presenter: AppearancePresenter

    @Inject
    lateinit var appInfo: AppInfo

    @Inject
    lateinit var lingver: Lingver

    override val titleStringId get() = R.string.pref_settings_appearance_title

    companion object {
        fun withFocusedPreference(key: String) = AppearanceFragment().apply {
            arguments = bundleOf(FOCUSED_KEY to key)
        }

        private const val FOCUSED_KEY = "focusedKey"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.onAttachView(this)
        arguments?.getString(FOCUSED_KEY)?.let { scrollToPreference(it) }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.scheme_preferences_appearance, rootKey)
        val attendanceTargetPref =
            findPreference<SeekBarPreference>(requireContext().getString(R.string.pref_key_attendance_target))!!
        attendanceTargetPref.setOnPreferenceChangeListener { _, newValueObj ->
            val newValue = (((newValueObj as Int).toDouble() + 2.5) / 5).toInt() * 5
            attendanceTargetPref.value =
                newValue.coerceIn(attendanceTargetPref.min, attendanceTargetPref.max)

            false
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        presenter.onSharedPreferenceChanged(key)
    }

    override fun recreateView() {
        activity?.recreate()
    }

    override fun updateLanguage(langCode: String) {
        lingver.setLocale(requireContext(), langCode)
    }

    override fun updateLanguageToFollowSystem() {
        lingver.setFollowSystemLocale(requireContext())
    }

    override fun showError(text: String, error: Throwable) {
        (activity as? BaseActivity<*, *>)?.showError(text, error)
    }

    override fun showMessage(text: String) {
        (activity as? BaseActivity<*, *>)?.showMessage(text)
    }

    override fun showExpiredCredentialsDialog() {
        (activity as? BaseActivity<*, *>)?.showExpiredCredentialsDialog()
    }

    override fun onCaptchaVerificationRequired(url: String?) {
        (activity as? BaseActivity<*, *>)?.onCaptchaVerificationRequired(url)
    }

    override fun showDecryptionFailedDialog() {
        (activity as? BaseActivity<*, *>)?.showDecryptionFailedDialog()
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

    override fun showAuthDialog() {
        (activity as? BaseActivity<*, *>)?.showAuthDialog()
    }

    override fun onResume() {
        super.onResume()
        preferenceScreen.sharedPreferences?.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onPause() {
        super.onPause()
        preferenceScreen.sharedPreferences?.unregisterOnSharedPreferenceChangeListener(this)
    }
}
