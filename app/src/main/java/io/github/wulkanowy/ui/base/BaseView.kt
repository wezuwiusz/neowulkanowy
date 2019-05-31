package io.github.wulkanowy.ui.base

interface BaseView {

    fun showError(text: String, error: Throwable)

    fun showMessage(text: String)

    fun showExpiredDialog()

    fun openClearLoginView()
}
