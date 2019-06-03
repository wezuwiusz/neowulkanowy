package io.github.wulkanowy.ui.modules.mobiledevice.token

import io.github.wulkanowy.data.pojos.MobileDeviceToken
import io.github.wulkanowy.ui.base.BaseView

interface MobileDeviceTokenVIew : BaseView {

    fun initView()

    fun hideLoading()

    fun showContent()

    fun closeDialog()

    fun updateData(token: MobileDeviceToken)
}
