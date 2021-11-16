package io.github.wulkanowy.ui.modules.settings.advanced

import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import androidx.preference.PreferenceFragmentCompat
import com.yariksoffice.lingver.Lingver
import dagger.hilt.android.AndroidEntryPoint
import io.github.wulkanowy.R
import io.github.wulkanowy.ui.base.BaseActivity
import io.github.wulkanowy.ui.base.ErrorDialog
import io.github.wulkanowy.ui.modules.main.MainView
import io.github.wulkanowy.utils.AppInfo
import javax.inject.Inject

@AndroidEntryPoint
class AdvancedFragment : PreferenceFragmentCompat(),
    SharedPreferences.OnSharedPreferenceChangeListener,
    MainView.TitledView, AdvancedView {

    @Inject
    lateinit var presenter: AdvancedPresenter

    @Inject
    lateinit var appInfo: AppInfo

    @Inject
    lateinit var lingver: Lingver

    override val titleStringId get() = R.string.pref_settings_advanced_title

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.onAttachView(this)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.scheme_preferences_advanced, rootKey)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        presenter.onSharedPreferenceChanged(key)
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
