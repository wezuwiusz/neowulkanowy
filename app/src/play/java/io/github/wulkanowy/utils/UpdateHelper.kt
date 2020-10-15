package io.github.wulkanowy.utils

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Context
import android.view.View
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType.FLEXIBLE
import com.google.android.play.core.install.model.AppUpdateType.IMMEDIATE
import com.google.android.play.core.install.model.InstallStatus.DOWNLOADED
import com.google.android.play.core.install.model.InstallStatus.PENDING
import com.google.android.play.core.install.model.UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS
import com.google.android.play.core.install.model.UpdateAvailability.UPDATE_AVAILABLE
import com.google.android.play.core.ktx.isFlexibleUpdateAllowed
import com.google.android.play.core.ktx.isImmediateUpdateAllowed
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.wulkanowy.R
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UpdateHelper @Inject constructor(@ApplicationContext private val context: Context) {

    lateinit var messageContainer: View

    companion object {
        const val IN_APP_UPDATE_REQUEST_CODE = 1721

        const val DAYS_FOR_FLEXIBLE_UPDATE = 7
        const val HIGH_PRIORITY_UPDATE = 4
    }

    private val appUpdateManager by lazy { AppUpdateManagerFactory.create(context) }

    private val flexibleUpdateListener = InstallStateUpdatedListener { state ->
        when (state.installStatus()) {
            PENDING -> Toast.makeText(context, R.string.update_download_started, Toast.LENGTH_SHORT).show()
            DOWNLOADED -> popupSnackBarForCompleteUpdate()
            else -> Timber.d("Update state: ${state.installStatus()}")
        }
    }

    private inline val AppUpdateInfo.isImmediateUpdateAvailable: Boolean
        get() = updateAvailability() == UPDATE_AVAILABLE && isImmediateUpdateAllowed &&
            updatePriority() >= HIGH_PRIORITY_UPDATE

    private inline val AppUpdateInfo.isFlexibleUpdateAvailable: Boolean
        get() = updateAvailability() == UPDATE_AVAILABLE && isFlexibleUpdateAllowed &&
            clientVersionStalenessDays() ?: 0 >= DAYS_FOR_FLEXIBLE_UPDATE

    fun checkAndInstallUpdates(activity: Activity) {
        Timber.d("Checking for updates...")
        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
            when {
                appUpdateInfo.isImmediateUpdateAvailable -> startUpdate(activity, appUpdateInfo, IMMEDIATE)
                appUpdateInfo.isFlexibleUpdateAvailable -> {
                    appUpdateManager.registerListener(flexibleUpdateListener)
                    startUpdate(activity, appUpdateInfo, FLEXIBLE)
                }
                else -> Timber.d("No update available")
            }
        }
    }

    private fun startUpdate(activity: Activity, appUpdateInfo: AppUpdateInfo, updateType: Int) {
        appUpdateManager.startUpdateFlowForResult(
            appUpdateInfo, updateType, activity, IN_APP_UPDATE_REQUEST_CODE
        )
    }

    fun onActivityResult(requestCode: Int, resultCode: Int) {
        if (requestCode == IN_APP_UPDATE_REQUEST_CODE && resultCode != RESULT_OK) {
            Timber.e("Update failed! Result code: $resultCode")
            Toast.makeText(context, R.string.update_failed, Toast.LENGTH_LONG).show()
        }
    }

    fun onResume(activity: Activity) {
        appUpdateManager.appUpdateInfo.addOnSuccessListener { info ->
            Timber.d("InAppUpdate.onResume() listener: $info")

            when {
                DOWNLOADED == info.installStatus() -> popupSnackBarForCompleteUpdate()
                DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS == info.updateAvailability() -> {
                    startUpdate(activity, info, if (info.isImmediateUpdateAvailable) IMMEDIATE else FLEXIBLE)
                }
            }
        }
    }

    private fun popupSnackBarForCompleteUpdate() {
        Snackbar.make(messageContainer, R.string.update_download_success, Snackbar.LENGTH_INDEFINITE).apply {
            setAction(R.string.update_download_success_button) {
                appUpdateManager.completeUpdate()
                appUpdateManager.unregisterListener(flexibleUpdateListener)
            }
            show()
        }
    }
}
