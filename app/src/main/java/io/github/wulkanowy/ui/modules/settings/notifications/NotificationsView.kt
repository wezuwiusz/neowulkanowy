package io.github.wulkanowy.ui.modules.settings.notifications

import io.github.wulkanowy.ui.base.BaseView

interface NotificationsView : BaseView {

    val isNotificationPermissionGranted: Boolean

    val isNotificationPiggybackPermissionGranted: Boolean

    fun initView(showDebugNotificationSwitch: Boolean)

    fun showFixSyncDialog()

    fun openSystemSettings()

    fun enableNotification(notificationKey: String, enable: Boolean)

    fun requestNotificationPermissions()

    fun openNotificationsPermissionDialog()

    fun openNotificationPiggyBackPermissionDialog()

    fun openNotificationExactAlarmSettings()

    fun setNotificationPreferencesChecked(isChecked: Boolean)

    fun setNotificationPiggybackPreferenceChecked(isChecked: Boolean)

    fun setUpcomingLessonsNotificationPreferenceChecked(isChecked: Boolean)
}
