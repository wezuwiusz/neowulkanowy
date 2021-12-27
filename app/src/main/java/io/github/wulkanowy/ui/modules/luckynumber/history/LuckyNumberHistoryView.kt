package io.github.wulkanowy.ui.modules.luckynumber.history

import io.github.wulkanowy.data.db.entities.LuckyNumber
import io.github.wulkanowy.ui.base.BaseView
import java.time.LocalDate

interface LuckyNumberHistoryView : BaseView {

    val isViewEmpty: Boolean

    fun initView()

    fun updateData(data: List<LuckyNumber>)

    fun clearData()

    fun showEmpty(show: Boolean)

    fun showErrorView(show: Boolean)

    fun setErrorDetails(message: String)

    fun updateNavigationWeek(date: String)

    fun showProgress(show: Boolean)

    fun showPreButton(show: Boolean)

    fun showNextButton(show: Boolean)

    fun showDatePickerDialog(selectedDate: LocalDate)

    fun showContent(show: Boolean)

    fun onDestroyView()
}
