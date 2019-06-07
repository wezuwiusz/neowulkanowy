package io.github.wulkanowy.ui.modules.mobiledevice

import io.github.wulkanowy.data.db.entities.MobileDevice
import io.github.wulkanowy.ui.base.BaseView

interface MobileDeviceView : BaseView {

    val isViewEmpty: Boolean

    fun initView()

    fun updateData(data: List<MobileDeviceItem>)

    fun restoreDeleteItem()

    fun hideRefresh()

    fun clearData()

    fun showProgress(show: Boolean)

    fun enableSwipe(enable: Boolean)

    fun showContent(show: Boolean)

    fun showEmpty(show: Boolean)

    fun showUndo(position: Int, device: MobileDevice)

    fun showTokenDialog()
}
