package io.github.wulkanowy.ui.modules.login.advanced

import io.github.wulkanowy.data.Status
import io.github.wulkanowy.data.db.entities.StudentWithSemesters
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.sdk.Sdk
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.modules.login.LoginErrorHandler
import io.github.wulkanowy.utils.AnalyticsHelper
import io.github.wulkanowy.utils.afterLoading
import io.github.wulkanowy.utils.flowWithResource
import io.github.wulkanowy.utils.ifNullOrBlank
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import javax.inject.Inject

class LoginAdvancedPresenter @Inject constructor(
    studentRepository: StudentRepository,
    private val loginErrorHandler: LoginErrorHandler,
    private val analytics: AnalyticsHelper
) : BasePresenter<LoginAdvancedView>(loginErrorHandler, studentRepository) {

    override fun onAttachView(view: LoginAdvancedView) {
        super.onAttachView(view)
        view.run {
            initView()
            showOnlyScrapperModeInputs()
            with(loginErrorHandler) {
                onBadCredentials = ::onBadCredentials
                onInvalidToken = ::onInvalidToken
                onInvalidSymbol = ::onInvalidSymbol
                onInvalidPin = ::onInvalidPin
            }
        }
    }

    private fun onBadCredentials() {
        view?.run {
            setErrorPassIncorrect()
            showSoftKeyboard()
            Timber.i("Entered wrong username or password")
        }
    }

    private fun onInvalidToken(message: String) {
        view?.run {
            setErrorTokenInvalid(message)
            showSoftKeyboard()
            Timber.i("Entered invalid token")
        }
    }

    private fun onInvalidSymbol(message: String) {
        view?.run {
            setErrorSymbolInvalid(message)
            showSoftKeyboard()
            Timber.i("Entered invalid symbol")
        }
    }

    private fun onInvalidPin(message: String) {
        view?.run {
            setErrorPinInvalid(message)
            showSoftKeyboard()
            Timber.i("Entered invalid PIN")
        }
    }

    fun updateUsernameLabel() {
        view?.apply {
            setUsernameLabel(if ("vulcan" in formHostValue || "fakelog" in formHostValue) emailLabel else nicknameLabel)
        }
    }

    fun onHostSelected() {
        view?.apply {
            clearPassError()
            clearUsernameError()
            if (formHostValue.contains("fakelog")) {
                setDefaultCredentials("jan@fakelog.cf", "jan123", "powiatwulkanowy", "FK100000", "999999")
            }
            setSymbol(formHostSymbol)
            updateUsernameLabel()
        }
    }

    fun onLoginModeSelected(type: Sdk.Mode) {
        view?.run {
            when (type) {
                Sdk.Mode.API -> {
                    showOnlyMobileApiModeInputs()
                    showMobileApiWarningMessage()
                }
                Sdk.Mode.SCRAPPER -> {
                    showOnlyScrapperModeInputs()
                    showScraperWarningMessage()
                }
                Sdk.Mode.HYBRID -> {
                    showOnlyHybridModeInputs()
                    showHybridWarningMessage()
                }
            }
        }
    }

    fun onPassTextChanged() {
        view?.clearPassError()
    }

    fun onUsernameTextChanged() {
        view?.clearUsernameError()
    }

    fun onPinTextChanged() {
        view?.clearPinKeyError()
    }

    fun onSymbolTextChanged() {
        view?.clearSymbolError()
    }

    fun onTokenTextChanged() {
        view?.clearTokenError()
    }

    fun onSignInClick() {
        if (!validateCredentials()) return

        flowWithResource { getStudentsAppropriatesToLoginType() }.onEach {
            when (it.status) {
                Status.LOADING -> view?.run {
                    Timber.i("Login started")
                    hideSoftKeyboard()
                    showProgress(true)
                    showContent(false)
                }
                Status.SUCCESS -> {
                    Timber.i("Login result: Success")
                    analytics.logEvent("registration_form",
                        "success" to true,
                        "students" to it.data!!.size,
                        "error" to "No error"
                    )
                    view?.notifyParentAccountLogged(it.data)
                }
                Status.ERROR -> {
                    Timber.i("Login result: An exception occurred")
                    analytics.logEvent(
                        "registration_form",
                        "success" to false, "students" to -1,
                        "error" to it.error!!.message.ifNullOrBlank { "No message" }
                    )
                    loginErrorHandler.dispatch(it.error)
                }
            }
        }.afterLoading {
            view?.apply {
                showProgress(false)
                showContent(true)
            }
        }.launch("login")
    }

    private suspend fun getStudentsAppropriatesToLoginType(): List<StudentWithSemesters> {
        val email = view?.formUsernameValue.orEmpty()
        val password = view?.formPassValue.orEmpty()
        val endpoint = view?.formHostValue.orEmpty()

        val pin = view?.formPinValue.orEmpty()
        val symbol = view?.formSymbolValue.orEmpty()
        val token = view?.formTokenValue.orEmpty()

        return when (Sdk.Mode.valueOf(view?.formLoginType.orEmpty())) {
            Sdk.Mode.API -> studentRepository.getStudentsApi(pin, symbol, token)
            Sdk.Mode.SCRAPPER -> studentRepository.getStudentsScrapper(email, password, endpoint, symbol)
            Sdk.Mode.HYBRID -> studentRepository.getStudentsHybrid(email, password, endpoint, symbol)
        }
    }

    private fun validateCredentials(): Boolean {
        val login = view?.formUsernameValue.orEmpty()
        val password = view?.formPassValue.orEmpty()

        val host = view?.formHostValue.orEmpty()

        val pin = view?.formPinValue.orEmpty()
        val symbol = view?.formSymbolValue.orEmpty()
        val token = view?.formTokenValue.orEmpty()

        var isCorrect = true

        when (Sdk.Mode.valueOf(view?.formLoginType ?: "")) {
            Sdk.Mode.API -> {
                if (pin.isEmpty()) {
                    view?.setErrorPinRequired()
                    isCorrect = false
                }

                if (symbol.isEmpty()) {
                    view?.setErrorSymbolRequired()
                    isCorrect = false
                }

                if (token.isEmpty()) {
                    view?.setErrorTokenRequired()
                    isCorrect = false
                }
            }
            Sdk.Mode.HYBRID, Sdk.Mode.SCRAPPER -> {
                if (login.isEmpty()) {
                    view?.setErrorUsernameRequired()
                    isCorrect = false
                } else {
                    if ("@" in login && "standard" !in host) {
                        view?.setErrorLoginRequired()
                        isCorrect = false
                    }

                    if ("@" !in login && "standard" in host) {
                        view?.setErrorEmailRequired()
                        isCorrect = false
                    }
                }

                if (password.isEmpty()) {
                    view?.setErrorPassRequired(focus = isCorrect)
                    isCorrect = false
                }

                if (password.length < 6 && password.isNotEmpty()) {
                    view?.setErrorPassInvalid(focus = isCorrect)
                    isCorrect = false
                }
            }
        }

        return isCorrect
    }
}
