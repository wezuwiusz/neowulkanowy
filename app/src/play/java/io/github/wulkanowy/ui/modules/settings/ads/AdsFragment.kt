package io.github.wulkanowy.ui.modules.settings.ads

import android.os.Bundle
import android.view.View
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd
import dagger.hilt.android.AndroidEntryPoint
import io.github.wulkanowy.R
import io.github.wulkanowy.ui.base.BaseActivity
import io.github.wulkanowy.ui.base.ErrorDialog
import io.github.wulkanowy.ui.modules.auth.AuthDialog
import io.github.wulkanowy.ui.modules.main.MainView
import io.github.wulkanowy.utils.AdsHelper
import io.github.wulkanowy.utils.openInternetBrowser
import javax.inject.Inject

@AndroidEntryPoint
class AdsFragment : PreferenceFragmentCompat(), MainView.TitledView, AdsView {

    @Inject
    lateinit var presenter: AdsPresenter

    @Inject
    lateinit var adsHelper: AdsHelper

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

        findPreference<Preference>(getString(R.string.pref_key_ads_privacy_policy))?.setOnPreferenceClickListener {
            presenter.onPrivacySelected()
            true
        }

        findPreference<Preference>(getString(R.string.pref_key_ads_ump_agreements))?.setOnPreferenceClickListener {
            presenter.onUmpAgreementsSelected()
            true
        }

        findPreference<Preference>(getString(R.string.pref_key_ads_single_support))
            ?.isEnabled = adsHelper.canShowAd

        findPreference<SwitchPreferenceCompat>(getString(R.string.pref_key_ads_enabled))?.setOnPreferenceChangeListener { _, newValue ->
            presenter.onAdsEnabledSelected(newValue as Boolean)
            true
        }
    }

    override fun showAd(ad: RewardedInterstitialAd) {
        if (isVisible) {
            ad.show(requireActivity()) {}
        }
    }

    override fun setCheckedAdsEnabled(checked: Boolean) {
        findPreference<SwitchPreferenceCompat>(getString(R.string.pref_key_ads_enabled))
            ?.isChecked = checked
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

    override fun showWatchAdOncePerVisit(show: Boolean) {
        findPreference<Preference>(getString(R.string.pref_key_ads_single_support))?.run {
            isEnabled = !show
            summary = if (show) getString(R.string.pref_ads_once_per_visit) else null
        }
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

    override fun showAuthDialog() {
        AuthDialog.newInstance().show(childFragmentManager, "auth_dialog")
    }

    override fun showErrorDetailsDialog(error: Throwable) {
        ErrorDialog.newInstance(error).show(childFragmentManager, error.toString())
    }
}
