package io.github.wulkanowy.ui.modules.conference

import io.github.wulkanowy.data.db.entities.Conference
import io.github.wulkanowy.ui.base.BaseView

interface ConferenceView : BaseView {

    val isViewEmpty: Boolean

    fun initView()

    fun updateData(data: List<Conference>)

    fun clearData()

    fun showRefresh(show: Boolean)

    fun showEmpty(show: Boolean)

    fun showErrorView(show: Boolean)

    fun setErrorDetails(message: String)

    fun showProgress(show: Boolean)

    fun enableSwipe(enable: Boolean)

    fun showContent(show: Boolean)
}
