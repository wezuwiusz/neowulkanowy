package io.github.wulkanowy.ui.modules.grade.futurecalculator

import io.github.wulkanowy.ui.base.BaseView

interface GradeFutureCalculatorView : BaseView {

    fun initView(keys: Array<String>)

    fun setErrorSubjectRequired()

    fun setErrorGradeRequired()

    fun setErrorWeightRequired()

    fun closeDialog()

}
