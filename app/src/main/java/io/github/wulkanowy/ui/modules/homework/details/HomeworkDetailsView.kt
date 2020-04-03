package io.github.wulkanowy.ui.modules.homework.details

import io.github.wulkanowy.ui.base.BaseView

interface HomeworkDetailsView : BaseView {

    fun initView()

    fun updateMarkAsDoneLabel(isDone: Boolean)
}
