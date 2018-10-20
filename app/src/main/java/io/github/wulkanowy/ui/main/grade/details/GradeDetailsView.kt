package io.github.wulkanowy.ui.main.grade.details

import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IExpandable
import eu.davidea.flexibleadapter.items.IFlexible
import io.github.wulkanowy.data.db.entities.Grade
import io.github.wulkanowy.ui.base.BaseView

interface GradeDetailsView : BaseView {

    fun initView()

    fun updateData(data: List<GradeDetailsHeader>)

    fun updateItem(item: AbstractFlexibleItem<*>)

    fun getHeaderOfItem(item: AbstractFlexibleItem<*>): IExpandable<*, out IFlexible<*>>?

    fun resetView()

    fun clearView()

    fun isViewEmpty(): Boolean

    fun showGradeDialog(grade: Grade)

    fun showContent(show: Boolean)

    fun showEmpty(show: Boolean)

    fun showProgress(show: Boolean)

    fun showRefresh(show: Boolean)

    fun emptyAverageString(): String

    fun averageString(): String

    fun gradeNumberString(number: Int): String

    fun weightString(): String

    fun notifyParentDataLoaded(semesterId: Int)

    fun notifyParentRefresh()
}
