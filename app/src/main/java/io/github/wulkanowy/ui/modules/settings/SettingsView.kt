package io.github.wulkanowy.ui.modules.settings

import io.github.wulkanowy.ui.base.BaseView

interface SettingsView : BaseView {

    fun recreateView()

    fun updateLanguage(langCode: String)

    fun setServicesSuspended(serviceEnablesKey: String, isHolidays: Boolean)
}
