package io.github.wulkanowy.ui.main.exam

import io.github.wulkanowy.data.db.entities.Exam
import io.github.wulkanowy.ui.base.BaseView

interface ExamView : BaseView {

    val isViewEmpty: Boolean

    fun initView()

    fun updateData(data: List<ExamItem>)

    fun updateNavigationWeek(date: String)

    fun clearData()

    fun hideRefresh()

    fun showEmpty(show: Boolean)

    fun showProgress(show: Boolean)

    fun showContent(show: Boolean)

    fun showNextButton(show: Boolean)

    fun showPreButton(show: Boolean)

    fun showExamDialog(exam: Exam)
}
