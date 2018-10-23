package io.github.wulkanowy.ui.modules.about

import io.github.wulkanowy.ui.base.BaseView

interface AboutView : BaseView {

    fun openSourceWebView()

    fun openIssuesWebView()
}