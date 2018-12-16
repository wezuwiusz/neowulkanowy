package io.github.wulkanowy.ui.modules.exam

import io.github.wulkanowy.data.db.entities.Exam
import io.github.wulkanowy.ui.base.session.BaseSessionView

interface ExamView : BaseSessionView {

    val isViewEmpty: Boolean

    fun initView()

    fun updateData(data: List<ExamItem>)

    fun updateNavigationWeek(date: String)

    fun clearData()

    fun hideRefresh()

    fun resetView()

    fun showEmpty(show: Boolean)

    fun showProgress(show: Boolean)

    fun showContent(show: Boolean)

    fun showNextButton(show: Boolean)

    fun showPreButton(show: Boolean)

    fun showExamDialog(exam: Exam)
}
