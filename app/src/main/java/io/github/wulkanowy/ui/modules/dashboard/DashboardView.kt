package io.github.wulkanowy.ui.modules.dashboard

import io.github.wulkanowy.ui.base.BaseView

interface DashboardView : BaseView {

    val tileWidth: Int

    fun initView()

    fun updateData(data: List<DashboardItem>)

    fun showDashboardTileSettings(selectedItems: List<DashboardItem.Tile>)

    fun showProgress(show: Boolean)

    fun showContent(show: Boolean)

    fun showRefresh(show: Boolean)

    fun showErrorView(show: Boolean)

    fun setErrorDetails(error: Throwable)

    fun resetView()

    fun popViewToRoot()

    fun openNotificationsCenterView()

    fun openInternetBrowser(url: String)
}
