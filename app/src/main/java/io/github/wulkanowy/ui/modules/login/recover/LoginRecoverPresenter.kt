package io.github.wulkanowy.ui.modules.login.recover

import io.github.wulkanowy.data.repositories.recover.RecoverRepository
import io.github.wulkanowy.data.repositories.student.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.utils.FirebaseAnalyticsHelper
import io.github.wulkanowy.utils.SchedulersProvider
import io.github.wulkanowy.utils.ifNullOrBlank
import timber.log.Timber
import javax.inject.Inject

class LoginRecoverPresenter @Inject constructor(
    schedulers: SchedulersProvider,
    studentRepository: StudentRepository,
    private val loginErrorHandler: RecoverErrorHandler,
    private val analytics: FirebaseAnalyticsHelper,
    private val recoverRepository: RecoverRepository
) : BasePresenter<LoginRecoverView>(loginErrorHandler, studentRepository, schedulers) {

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

    fun onSymbolTextChanged() {
        view?.clearSymbolError()
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
            showSymbol("adfs" in recoverHostValue)
            setUsernameHint(if ("standard" in recoverHostValue) emailHintString else loginPeselEmailHintString)
        }
    }

    fun onRecoverClick() {
        val username = view?.recoverNameValue.orEmpty()
        val host = view?.recoverHostValue.orEmpty()
        val symbol = view?.recoverSymbolValue.orEmpty()

        if (!validateInput(username, host, symbol)) return

        disposable.add(recoverRepository.getReCaptchaSiteKey(host, symbol.ifBlank { "Default" })
            .subscribeOn(schedulers.backgroundThread)
            .observeOn(schedulers.mainThread)
            .doOnSubscribe {
                view?.run {
                    hideSoftKeyboard()
                    showRecoverForm(false)
                    showProgress(true)
                    showErrorView(false)
                    showCaptcha(false)
                }
            }
            .subscribe({ (resetUrl, siteKey) ->
                view?.loadReCaptcha(siteKey, resetUrl)
            }) {
                Timber.e("Obtain captcha site key result: An exception occurred")
                errorHandler.dispatch(it)
            })
    }

    private fun validateInput(username: String, host: String, symbol: String): Boolean {
        var isCorrect = true

        if (username.isEmpty()) {
            view?.setErrorNameRequired()
            isCorrect = false
        }

        if ("standard" in host && "@" !in username) {
            view?.setUsernameError(view?.invalidEmailString.orEmpty())
            isCorrect = false
        }

        if ("adfs" in host && symbol.isBlank()) {
            view?.setSymbolError(focus = isCorrect)
            isCorrect = false
        }

        return isCorrect
    }

    fun onReCaptchaVerified(reCaptchaResponse: String) {
        val username = view?.recoverNameValue.orEmpty()
        val host = view?.recoverHostValue.orEmpty()
        val symbol = view?.recoverSymbolValue.ifNullOrBlank { "Default" }

        with(disposable) {
            clear()
            add(recoverRepository.sendRecoverRequest(host, symbol, username, reCaptchaResponse)
                .subscribeOn(schedulers.backgroundThread)
                .observeOn(schedulers.mainThread)
                .doOnSubscribe {
                    view?.run {
                        showProgress(true)
                        showRecoverForm(false)
                        showCaptcha(false)
                    }
                }
                .doFinally {
                    view?.showProgress(false)
                }
                .subscribe({
                    view?.run {
                        showSuccessView(true)
                        setSuccessTitle(it.substringBefore(". "))
                        setSuccessMessage(it.substringAfter(". "))
                    }

                    analytics.logEvent("account_recover", "register" to host, "symbol" to symbol, "success" to true)
                }) {
                    Timber.e("Send recover request result: An exception occurred")
                    errorHandler.dispatch(it)
                    analytics.logEvent("account_recover", "register" to host, "symbol" to symbol, "success" to false)
                })
        }
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
