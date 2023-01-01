package io.github.wulkanowy.ui.modules.settings.notifications

import com.chuckerteam.chucker.api.ChuckerCollector
import io.github.wulkanowy.data.repositories.PreferencesRepository
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.services.alarm.TimetableNotificationSchedulerHelper
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.utils.AnalyticsHelper
import io.github.wulkanowy.utils.AppInfo
import timber.log.Timber
import javax.inject.Inject

class NotificationsPresenter @Inject constructor(
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository,
    private val preferencesRepository: PreferencesRepository,
    private val timetableNotificationHelper: TimetableNotificationSchedulerHelper,
    private val appInfo: AppInfo,
    private val analytics: AnalyticsHelper,
    private val chuckerCollector: ChuckerCollector
) : BasePresenter<NotificationsView>(errorHandler, studentRepository) {

    override fun onAttachView(view: NotificationsView) {
        super.onAttachView(view)

        with(view) {
            enableNotification(
                notificationKey = preferencesRepository.notificationsEnableKey,
                enable = preferencesRepository.isServiceEnabled
            )
            initView(appInfo.isDebug)
        }

        checkNotificationsPermissionState()
        checkNotificationPiggybackState()

        Timber.i("Settings notifications view was initialized")
    }

    fun onSharedPreferenceChanged(key: String) {
        Timber.i("Change settings $key")

        preferencesRepository.apply {
            when (key) {
                isUpcomingLessonsNotificationsEnableKey, isUpcomingLessonsNotificationsPersistentKey -> {
                    if (!isUpcomingLessonsNotificationsEnable) {
                        timetableNotificationHelper.cancelNotification()
                    } else if (!timetableNotificationHelper.canScheduleExactAlarms()) {
                        view?.openNotificationExactAlarmSettings()
                    }
                }
                notificationsEnableKey -> {
                    if (isNotificationsEnable && view?.isNotificationPermissionGranted == false) {
                        view?.requestNotificationPermissions()
                    }
                }
                isDebugNotificationEnableKey -> {
                    chuckerCollector.showNotification = isDebugNotificationEnable
                }
                isNotificationPiggybackEnabledKey -> {
                    if (isNotificationPiggybackEnabled && view?.isNotificationPiggybackPermissionGranted == false) {
                        view?.openNotificationPiggyBackPermissionDialog()
                    }
                }
            }
        }
        analytics.logEvent("setting_changed", "name" to key)
    }

    fun onFixSyncIssuesClicked() {
        view?.showFixSyncDialog()
    }

    fun onOpenSystemSettingsClicked() {
        view?.openSystemSettings()
    }

    fun onNotificationsPermissionResult() {
        view?.run {
            setNotificationPreferencesChecked(isNotificationPermissionGranted)
        }
    }

    fun onNotificationPiggybackPermissionResult() {
        view?.run {
            setNotificationPiggybackPreferenceChecked(isNotificationPiggybackPermissionGranted)
        }
    }

    fun onNotificationExactAlarmPermissionResult() {
        view?.setUpcomingLessonsNotificationPreferenceChecked(timetableNotificationHelper.canScheduleExactAlarms())
    }

    private fun checkNotificationsPermissionState() {
        if (preferencesRepository.isNotificationsEnable) {
            view?.run {
                setNotificationPreferencesChecked(isNotificationPermissionGranted)
            }
        }
    }

    private fun checkNotificationPiggybackState() {
        if (preferencesRepository.isNotificationPiggybackEnabled) {
            view?.run {
                setNotificationPiggybackPreferenceChecked(isNotificationPiggybackPermissionGranted)
            }
        }
    }
}
