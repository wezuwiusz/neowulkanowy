package io.github.wulkanowy.ui.base

interface BaseView {

    fun showError(text: String, error: Throwable)

    fun showMessage(text: String)

    fun showExpiredCredentialsDialog()

    fun onCaptchaVerificationRequired(url: String?)

    fun showDecryptionFailedDialog()

    fun showAuthDialog()

    fun openClearLoginView()

    fun showErrorDetailsDialog(error: Throwable)

    fun showChangePasswordSnackbar(redirectUrl: String)
}
