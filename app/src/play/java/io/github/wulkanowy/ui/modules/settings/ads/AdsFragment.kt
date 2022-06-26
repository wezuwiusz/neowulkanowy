package io.github.wulkanowy.ui.modules.settings.ads

import android.os.Bundle
import android.view.View
import androidx.preference.CheckBoxPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import io.github.wulkanowy.R
import io.github.wulkanowy.databinding.DialogAdsConsentBinding
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

        findPreference<Preference>(getString(R.string.pref_key_ads_privacy_policy))?.setOnPreferenceClickListener {
            presenter.onPrivacySelected()
            true
        }

        findPreference<CheckBoxPreference>(getString(R.string.pref_key_ads_consent_data_processing))
            ?.setOnPreferenceChangeListener { _, newValue ->
                presenter.onConsentSelected(newValue as Boolean)
                true
            }
    }

    override fun showAd(ad: RewardedInterstitialAd) {
        if (isVisible) {
            ad.show(requireActivity()) {}
        }
    }

    override fun showPrivacyPolicyDialog() {
        val dialogAdsConsentBinding = DialogAdsConsentBinding.inflate(layoutInflater)

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.pref_ads_consent_title)
            .setMessage(R.string.pref_ads_consent_description)
            .setView(dialogAdsConsentBinding.root)
            .setOnCancelListener { presenter.onPrivacyDialogCanceled() }
            .show()

        dialogAdsConsentBinding.adsConsentOver.setOnCheckedChangeListener { _, isChecked ->
            dialogAdsConsentBinding.adsConsentPersonalised.isEnabled = isChecked
        }

        dialogAdsConsentBinding.adsConsentPersonalised.setOnClickListener {
            presenter.onPersonalizedAgree()
            dialog.dismiss()
        }

        dialogAdsConsentBinding.adsConsentNonPersonalised.setOnClickListener {
            presenter.onNonPersonalizedAgree()
            dialog.dismiss()
        }

        dialogAdsConsentBinding.adsConsentPrivacy.setOnClickListener { presenter.onPrivacySelected() }
        dialogAdsConsentBinding.adsConsentCancel.setOnClickListener { dialog.cancel() }
    }

    override fun showProcessingDataSummary(isPersonalized: Boolean?) {
        val summaryText = isPersonalized?.let {
            getString(if (it) R.string.pref_ads_summary_personalized else R.string.pref_ads_summary_non_personalized)
        }

        findPreference<CheckBoxPreference>(getString(R.string.pref_key_ads_consent_data_processing))
            ?.summary = summaryText
    }

    override fun setCheckedProcessingData(checked: Boolean) {
        findPreference<CheckBoxPreference>(getString(R.string.pref_key_ads_consent_data_processing))
            ?.isChecked = checked
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
