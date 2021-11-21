package io.github.wulkanowy.utils

import android.content.Context
import android.os.Bundle
import com.google.ads.mediation.admob.AdMobAdapter
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.wulkanowy.BuildConfig
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class AdsHelper @Inject constructor(@ApplicationContext private val context: Context) {

    suspend fun getSupportAd(): RewardedInterstitialAd? {
        MobileAds.initialize(context)

        val extra = Bundle().apply { putString("npa", "1") }
        val adRequest = AdRequest.Builder()
            .addNetworkExtrasBundle(AdMobAdapter::class.java, extra)
            .build()

        return suspendCoroutine {
            RewardedInterstitialAd.load(
                context,
                BuildConfig.SINGLE_SUPPORT_AD_ID,
                adRequest,
                object : RewardedInterstitialAdLoadCallback() {
                    override fun onAdLoaded(rewardedInterstitialAd: RewardedInterstitialAd) {
                        it.resume(rewardedInterstitialAd)
                    }

                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        it.resumeWithException(IllegalArgumentException(loadAdError.message))
                    }
                })
        }
    }
}