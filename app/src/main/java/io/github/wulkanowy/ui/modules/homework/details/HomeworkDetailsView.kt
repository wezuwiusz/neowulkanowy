package io.github.wulkanowy.ui.modules.homework.details

import io.github.wulkanowy.ui.base.BaseView

interface HomeworkDetailsView : BaseView {

    val homeworkDeleteSuccess: String

    fun initView()

    fun closeDialog()

    fun updateMarkAsDoneLabel(isDone: Boolean)
}
