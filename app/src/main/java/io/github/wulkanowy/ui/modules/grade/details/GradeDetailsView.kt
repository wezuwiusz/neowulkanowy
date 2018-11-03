package io.github.wulkanowy.ui.modules.grade.details

import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IExpandable
import eu.davidea.flexibleadapter.items.IFlexible
import io.github.wulkanowy.data.db.entities.Grade
import io.github.wulkanowy.ui.base.BaseView

interface GradeDetailsView : BaseView {

    val isViewEmpty: Boolean

    val emptyAverageString: String

    val averageString: String

    val weightString: String

    fun initView()

    fun updateData(data: List<GradeDetailsHeader>)

    fun updateItem(item: AbstractFlexibleItem<*>)

    fun resetView()

    fun clearView()

    fun showGradeDialog(grade: Grade)

    fun showContent(show: Boolean)

    fun showEmpty(show: Boolean)

    fun showProgress(show: Boolean)

    fun showRefresh(show: Boolean)

    fun notifyParentDataLoaded(semesterId: Int)

    fun notifyParentRefresh()

    fun getGradeNumberString(number: Int): String

    fun getHeaderOfItem(item: AbstractFlexibleItem<*>): IExpandable<*, out IFlexible<*>>?
}
