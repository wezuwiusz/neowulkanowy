package io.github.wulkanowy.ui.modules.dashboard

import io.github.wulkanowy.ui.base.BaseView

interface DashboardView : BaseView {

    val tileWidth: Int

    val isViewEmpty: Boolean

    fun initView()

    fun updateData(data: List<DashboardItem>)

    fun showDashboardTileSettings(selectedItems: List<DashboardItem.Tile>)

    fun showProgress(show: Boolean)

    fun showContent(show: Boolean)

    fun showRefresh(show: Boolean)

    fun showErrorView(show: Boolean, adminMessageItem: DashboardItem.AdminMessages? = null)

    fun setErrorDetails(error: Throwable)

    fun resetView()

    fun popViewToRoot()

    fun openNotificationsCenterView()

    fun openInternetBrowser(url: String)

    fun openPanicWebView(url: String)
}
