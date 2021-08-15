package io.github.wulkanowy.ui.modules.settings.sync

import io.github.wulkanowy.ui.base.BaseView

interface SyncView : BaseView {

    val syncSuccessString: String

    val syncFailedString: String

    fun initView()

    fun setLastSyncDate(lastSyncDate: String)

    fun setServicesSuspended(serviceEnablesKey: String, isHolidays: Boolean)

    fun setSyncInProgress(inProgress: Boolean)
}
