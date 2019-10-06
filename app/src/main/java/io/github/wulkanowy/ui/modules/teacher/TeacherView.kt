package io.github.wulkanowy.ui.modules.teacher

import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import io.github.wulkanowy.ui.base.BaseView

interface TeacherView : BaseView {

    val isViewEmpty: Boolean

    val noSubjectString: String

    fun initView()

    fun updateData(data: List<TeacherItem>)

    fun updateItem(item: AbstractFlexibleItem<*>)

    fun hideRefresh()

    fun clearData()

    fun showProgress(show: Boolean)

    fun enableSwipe(enable: Boolean)

    fun showContent(show: Boolean)

    fun showEmpty(show: Boolean)
}
