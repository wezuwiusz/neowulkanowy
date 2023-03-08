package io.github.wulkanowy.utils

import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteConfigHelper @Inject constructor(
    @ApplicationContext private val context: Context,
    private val appInfo: AppInfo,
) : BaseRemoteConfigHelper() {

    override fun initialize() {
        FirebaseApp.initializeApp(context)

        Firebase.remoteConfig.setConfigSettingsAsync(remoteConfigSettings {
            fetchTimeoutInSeconds = 3
            if (appInfo.isDebug) {
                minimumFetchIntervalInSeconds = 0
            }
        })
        Firebase.remoteConfig.setDefaultsAsync(RemoteConfigDefaults.values().associate {
            it.key to it.value
        })
        Firebase.remoteConfig.fetchAndActivate()
    }

    override val userAgentTemplate: String
        get() = Firebase.remoteConfig.getString(RemoteConfigDefaults.USER_AGENT_TEMPLATE.key)
}
