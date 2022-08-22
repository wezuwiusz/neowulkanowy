package io.github.wulkanowy.utils

import android.content.Context
import android.os.Bundle
import android.view.View
import com.google.ads.mediation.admob.AdMobAdapter
import com.google.android.gms.ads.*
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.wulkanowy.BuildConfig
import io.github.wulkanowy.data.repositories.PreferencesRepository
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


class AdsHelper @Inject constructor(
    @ApplicationContext private val context: Context,
    private val preferencesRepository: PreferencesRepository
) {

    fun initialize() {
        if (preferencesRepository.isAgreeToProcessData) {
            MobileAds.initialize(context)
        }
    }

    suspend fun getSupportAd(): RewardedInterstitialAd? {
        val extra = Bundle().apply { putString("npa", "1") }
        val adRequest = AdRequest.Builder()
            .apply {
                if (!preferencesRepository.isPersonalizedAdsEnabled) {
                    addNetworkExtrasBundle(AdMobAdapter::class.java, extra)
                }
            }
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

    suspend fun getDashboardTileAdBanner(width: Int): AdBanner {
        val extra = Bundle().apply { putString("npa", "1") }
        val adRequest = AdRequest.Builder()
            .apply {
                if (!preferencesRepository.isPersonalizedAdsEnabled) {
                    addNetworkExtrasBundle(AdMobAdapter::class.java, extra)
                }
            }
            .build()

        return suspendCoroutine {
            val adView = AdView(context).apply {
                setAdSize(AdSize.getPortraitAnchoredAdaptiveBannerAdSize(context, width))
                adUnitId = BuildConfig.DASHBOARD_TILE_AD_ID
                adListener = object : AdListener() {
                    override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                        it.resumeWithException(IllegalArgumentException(loadAdError.message))
                    }

                    override fun onAdLoaded() {
                        it.resume(AdBanner(this@apply))
                    }
                }
            }

            adView.loadAd(adRequest)
        }
    }
}

data class AdBanner(val view: View)
