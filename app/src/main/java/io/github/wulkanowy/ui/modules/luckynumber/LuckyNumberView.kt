package io.github.wulkanowy.ui.modules.luckynumber

import io.github.wulkanowy.data.db.entities.LuckyNumber
import io.github.wulkanowy.ui.base.BaseView

interface LuckyNumberView : BaseView {

    val isViewEmpty: Boolean

    fun initView()

    fun updateData(data: LuckyNumber)

    fun hideRefresh()

    fun showEmpty(show: Boolean)

    fun showErrorView(show: Boolean)

    fun setErrorDetails(message: String)

    fun showProgress(show: Boolean)

    fun enableSwipe(enable: Boolean)

    fun showContent(show: Boolean)

    fun openLuckyNumberHistory()

    fun popView()
}
