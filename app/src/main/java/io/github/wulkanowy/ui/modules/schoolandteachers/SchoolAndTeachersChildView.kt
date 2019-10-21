package io.github.wulkanowy.ui.modules.schoolandteachers

interface SchoolAndTeachersChildView {

    fun notifyParentDataLoaded()

    fun onParentLoadData(forceRefresh: Boolean)
}
