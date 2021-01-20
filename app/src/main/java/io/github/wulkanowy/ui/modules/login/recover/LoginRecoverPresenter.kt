package io.github.wulkanowy.ui.modules.login.recover

import io.github.wulkanowy.data.Status
import io.github.wulkanowy.data.repositories.RecoverRepository
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.utils.AnalyticsHelper
import io.github.wulkanowy.utils.afterLoading
import io.github.wulkanowy.utils.flowWithResource
import io.github.wulkanowy.utils.ifNullOrBlank
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import javax.inject.Inject

class LoginRecoverPresenter @Inject constructor(
    studentRepository: StudentRepository,
    private val loginErrorHandler: RecoverErrorHandler,
    private val analytics: AnalyticsHelper,
    private val recoverRepository: RecoverRepository
) : BasePresenter<LoginRecoverView>(loginErrorHandler, studentRepository) {

    private lateinit var lastError: Throwable

    override fun onAttachView(view: LoginRecoverView) {
        super.onAttachView(view)
        view.initView()

        with(loginErrorHandler) {
            showErrorMessage = ::showErrorMessage
            onInvalidUsername = ::onInvalidUsername
            onInvalidCaptcha = ::onInvalidCaptcha
        }
    }

    fun onNameTextChanged() {
        view?.clearUsernameError()
    }

    fun onHostSelected() {
        view?.run {
            if ("fakelog" in recoverHostValue) setDefaultCredentials("jan@fakelog.cf")
            clearUsernameError()
            updateFields()
        }
    }

    fun updateFields() {
        view?.run {
            setUsernameHint(if ("standard" in recoverHostValue) emailHintString else loginPeselEmailHintString)
        }
    }

    fun onRecoverClick() {
        val username = view?.recoverNameValue.orEmpty()
        val host = view?.recoverHostValue.orEmpty()
        val symbol = view?.formHostSymbol.orEmpty()

        if (!validateInput(username, host)) return

        flowWithResource { recoverRepository.getReCaptchaSiteKey(host, symbol.ifBlank { "Default" }) }.onEach {
            when (it.status) {
                Status.LOADING -> view?.run {
                    hideSoftKeyboard()
                    showRecoverForm(false)
                    showProgress(true)
                    showErrorView(false)
                    showCaptcha(false)
                }
                Status.SUCCESS -> view?.run {
                    loadReCaptcha(url = it.data!!.first, siteKey = it.data.second)
                    showProgress(false)
                    showErrorView(false)
                    showCaptcha(true)
                }
                Status.ERROR -> {
                    Timber.i("Obtain captcha site key result: An exception occurred")
                    errorHandler.dispatch(it.error!!)
                }
            }
        }.launch("captcha")
    }

    private fun validateInput(username: String, host: String): Boolean {
        var isCorrect = true

        if (username.isEmpty()) {
            view?.setErrorNameRequired()
            isCorrect = false
        }

        if ("standard" in host && "@" !in username) {
            view?.setUsernameError(view?.invalidEmailString.orEmpty())
            isCorrect = false
        }

        return isCorrect
    }

    fun onReCaptchaVerified(reCaptchaResponse: String) {
        val username = view?.recoverNameValue.orEmpty()
        val host = view?.recoverHostValue.orEmpty()
        val symbol = view?.formHostSymbol.ifNullOrBlank { "Default" }

        flowWithResource { recoverRepository.sendRecoverRequest(host, symbol, username, reCaptchaResponse) }.onEach {
            when (it.status) {
                Status.LOADING -> view?.run {
                    showProgress(true)
                    showRecoverForm(false)
                    showCaptcha(false)
                }
                Status.SUCCESS -> view?.run {
                    showSuccessView(true)
                    setSuccessTitle(it.data!!.substringBefore(". "))
                    setSuccessMessage(it.data.substringAfter(". "))
                    analytics.logEvent("account_recover", "register" to host, "symbol" to symbol, "success" to true)
                }
                Status.ERROR -> {
                    Timber.i("Send recover request result: An exception occurred")
                    errorHandler.dispatch(it.error!!)
                    analytics.logEvent("account_recover", "register" to host, "symbol" to symbol, "success" to false)
                }
            }
        }.afterLoading {
            view?.showProgress(false)
        }.launch("verified")
    }

    fun onDetailsClick() {
        view?.showErrorDetailsDialog(lastError)
    }

    private fun showErrorMessage(message: String, error: Throwable) {
        view?.run {
            lastError = error
            showProgress(false)
            setErrorDetails(message)
            showErrorView(true)
        }
    }

    private fun onInvalidUsername(message: String) {
        view?.run {
            setUsernameError(message)
            showRecoverForm(true)
        }
    }

    private fun onInvalidCaptcha(message: String, error: Throwable) {
        view?.run {
            lastError = error
            setErrorDetails(message)
            showCaptcha(false)
            showRecoverForm(false)
            showErrorView(true)
        }
    }
}
