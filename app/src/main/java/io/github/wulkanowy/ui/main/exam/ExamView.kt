package io.github.wulkanowy.ui.main.exam

import io.github.wulkanowy.data.db.entities.Exam
import io.github.wulkanowy.ui.base.BaseView
import org.threeten.bp.LocalDate

interface ExamView : BaseView {

    fun initView()

    fun updateData(data: List<ExamItem>)

    fun showEmpty(show: Boolean)

    fun showProgress(show: Boolean)

    fun showContent(show: Boolean)

    fun showRefresh(show: Boolean)

    fun showNextButton(show: Boolean)

    fun showPreButton(show: Boolean)

    fun showExamDialog(exam: Exam)

    fun updateNavigationWeek(date: String)
}
