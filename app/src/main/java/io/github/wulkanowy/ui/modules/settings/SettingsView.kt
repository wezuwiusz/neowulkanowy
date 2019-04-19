package io.github.wulkanowy.ui.modules.settings

import io.github.wulkanowy.ui.base.BaseView

interface SettingsView : BaseView {

    fun recreateView()

    fun setServicesSuspended(serviceEnablesKey: String, isHolidays: Boolean)
}
