package io.github.wulkanowy.ui.base.session

import io.github.wulkanowy.ui.base.BaseView

interface BaseSessionView : BaseView {

    fun showExpiredDialog()

    fun openLoginView()
}
