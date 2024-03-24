package io.github.wulkanowy.utils

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.view.View
import androidx.core.content.getSystemService
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityScoped
import io.github.wulkanowy.BuildConfig
import io.github.wulkanowy.data.repositories.PreferencesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import timber.log.Timber
import java.net.UnknownHostException
import java.util.concurrent.atomic.AtomicBoolean
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

@ActivityScoped
class AdsHelper @Inject constructor(
    private val activity: Activity,
    @ApplicationContext private val context: Context,
    preferencesRepository: PreferencesRepository
) {

    private var isMobileAdsInitializeCalled = AtomicBoolean(false)
    private var consentInformation: ConsentInformation? = null

    private val canRequestAd get() = consentInformation?.canRequestAds() == true
    val isMobileAdsSdkInitialized = MutableStateFlow(false)
    val canShowAd get() = isMobileAdsSdkInitialized.value && canRequestAd

    init {
        if (preferencesRepository.isAdsEnabled) {
            initialize()
        }
    }

    fun initialize() {
        val consentRequestParameters = ConsentRequestParameters.Builder()
            .build()

        consentInformation = UserMessagingPlatform.getConsentInformation(context)
        consentInformation?.requestConsentInfoUpdate(
            activity,
            consentRequestParameters,
            {
                UserMessagingPlatform.loadAndShowConsentFormIfRequired(
                    activity
                ) { loadAndShowError ->

                    if (loadAndShowError != null) {
                        Timber.e(IllegalStateException("${loadAndShowError.errorCode}: ${loadAndShowError.message}"))
                    }

                    if (canRequestAd) {
                        initializeMobileAds()
                    }
                }
            },
            { requestConsentError ->
                Timber.e(IllegalStateException("${requestConsentError.errorCode}: ${requestConsentError.message}"))
            })

        if (canRequestAd) {
            initializeMobileAds()
        }
    }

    fun openAdsUmpAgreements() {
        UserMessagingPlatform.showPrivacyOptionsForm(activity) {
            if (it != null) {
                Timber.e(IllegalStateException("${it.errorCode}: ${it.message}"))
            }
        }
    }

    private fun initializeMobileAds() {
        if (isMobileAdsInitializeCalled.getAndSet(true)) return

        try {
            MobileAds.initialize(context) {
                isMobileAdsSdkInitialized.value = true
            }
        } catch (e: Exception) {
            Timber.e(e)
        }
    }

    suspend fun getSupportAd(): RewardedInterstitialAd? {
        if (!canRequestAd) return null
        if (!context.isInternetConnected()) {
            throw UnknownHostException()
        }

        val adRequest = AdRequest.Builder()
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
        if (!canShowAd) throw IllegalStateException("Cannot show ad")
        val adRequest = AdRequest.Builder()
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

@Suppress("DEPRECATION")
private fun Context.isInternetConnected(): Boolean {
    val connectivityManager = getSystemService<ConnectivityManager>() ?: return false

    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val currentNetwork = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(currentNetwork)

        networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) == true
    } else {
        connectivityManager.activeNetworkInfo?.isConnected == true
    }
}

data class AdBanner(val view: View)
