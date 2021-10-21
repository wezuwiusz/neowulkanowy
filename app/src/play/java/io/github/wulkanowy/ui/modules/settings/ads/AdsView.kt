package io.github.wulkanowy.ui.modules.settings.ads

import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd
import io.github.wulkanowy.ui.base.BaseView

interface AdsView : BaseView {

    fun initView()

    fun showAd(ad: RewardedInterstitialAd)

    fun showPrivacyPolicyDialog()

    fun openPrivacyPolicy()

    fun showLoadingSupportAd(show: Boolean)
}