package io.github.wulkanowy.ui.modules.settings.ads

import android.os.Bundle
import android.view.View
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import io.github.wulkanowy.R
import io.github.wulkanowy.ui.base.BaseActivity
import io.github.wulkanowy.ui.base.ErrorDialog
import io.github.wulkanowy.ui.modules.main.MainView
import io.github.wulkanowy.utils.openInternetBrowser
import javax.inject.Inject

@AndroidEntryPoint
class AdsFragment : PreferenceFragmentCompat(), MainView.TitledView, AdsView {

    @Inject
    lateinit var presenter: AdsPresenter

    override val titleStringId = R.string.pref_settings_ads_title

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.scheme_preferences_ads, rootKey)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.onAttachView(this)
    }

    override fun initView() {
        findPreference<Preference>(getString(R.string.pref_key_ads_single_support))?.setOnPreferenceClickListener {
            presenter.onWatchSingleAdSelected()
            true
        }
    }

    override fun showAd(ad: RewardedInterstitialAd) {
        if (isVisible) {
            ad.show(requireActivity()) {}
        }
    }

    override fun showPrivacyPolicyDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.pref_ads_privacy_title))
            .setMessage(getString(R.string.pref_ads_privacy_description))
            .setPositiveButton(getString(R.string.pref_ads_privacy_agree)) { _, _ -> presenter.onAgreedPrivacy() }
            .setNegativeButton(android.R.string.cancel) { _, _ -> }
            .setNeutralButton(getString(R.string.pref_ads_privacy_link)) { _, _ -> presenter.onPrivacySelected() }
            .show()
    }

    override fun openPrivacyPolicy() {
        requireContext().openInternetBrowser(
            "https://wulkanowy.github.io/polityka-prywatnosci.html",
            ::showMessage
        )
    }

    override fun showLoadingSupportAd(show: Boolean) {
        findPreference<Preference>(getString(R.string.pref_key_ads_single_support))?.run {
            isEnabled = !show
            summary = if (show) getString(R.string.pref_ads_loading) else null
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
}