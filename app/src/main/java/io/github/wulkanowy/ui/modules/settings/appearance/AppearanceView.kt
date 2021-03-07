package io.github.wulkanowy.ui.modules.settings.appearance

import io.github.wulkanowy.ui.base.BaseView

interface AppearanceView : BaseView {

    fun recreateView()

    fun updateLanguage(langCode: String)

    fun updateLanguageToFollowSystem()
}
