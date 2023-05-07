package io.github.wulkanowy.ui.modules.more

import io.github.wulkanowy.ui.base.BaseView
import io.github.wulkanowy.ui.modules.Destination

interface MoreView : BaseView {

    fun initView()

    fun updateData(data: List<MoreItem>)

    fun popView(depth: Int)

    fun openView(destination: Destination)
}
