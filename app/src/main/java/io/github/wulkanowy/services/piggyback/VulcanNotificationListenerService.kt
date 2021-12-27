package io.github.wulkanowy.services.piggyback

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import dagger.hilt.android.AndroidEntryPoint
import io.github.wulkanowy.data.repositories.PreferencesRepository
import io.github.wulkanowy.services.sync.SyncManager
import javax.inject.Inject

@AndroidEntryPoint
class VulcanNotificationListenerService : NotificationListenerService() {

    @Inject
    lateinit var syncManager: SyncManager

    @Inject
    lateinit var preferenceRepository: PreferencesRepository

    override fun onNotificationPosted(statusBarNotification: StatusBarNotification?) {
        if (statusBarNotification?.packageName == "pl.edu.vulcan.hebe" && preferenceRepository.isNotificationPiggybackEnabled) {
            syncManager.startOneTimeSyncWorker()
            if (preferenceRepository.isNotificationPiggybackRemoveOriginalEnabled) {
                cancelNotification(statusBarNotification.key)
            }
        }
    }
}