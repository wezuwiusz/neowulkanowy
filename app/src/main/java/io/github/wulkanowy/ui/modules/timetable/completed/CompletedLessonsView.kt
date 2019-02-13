package io.github.wulkanowy.ui.modules.timetable.completed

import io.github.wulkanowy.data.db.entities.CompletedLesson
import io.github.wulkanowy.ui.base.session.BaseSessionView

interface CompletedLessonsView : BaseSessionView {

    val isViewEmpty: Boolean

    fun initView()

    fun updateData(data: List<CompletedLessonItem>)

    fun clearData()

    fun updateNavigationDay(date: String)

    fun hideRefresh()

    fun showEmpty(show: Boolean)

    fun showFeatureDisabled()

    fun showProgress(show: Boolean)

    fun showContent(show: Boolean)

    fun showPreButton(show: Boolean)

    fun showNextButton(show: Boolean)

    fun showCompletedLessonDialog(completedLesson: CompletedLesson)
}
