package io.github.wulkanowy.ui.modules.login.form

import androidx.core.net.toUri
import io.github.wulkanowy.data.*
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.modules.login.LoginData
import io.github.wulkanowy.ui.modules.login.LoginErrorHandler
import io.github.wulkanowy.utils.AnalyticsHelper
import io.github.wulkanowy.utils.ifNullOrBlank
import timber.log.Timber
import java.net.URL
import javax.inject.Inject

class LoginFormPresenter @Inject constructor(
    studentRepository: StudentRepository,
    private val loginErrorHandler: LoginErrorHandler,
    private val analytics: AnalyticsHelper
) : BasePresenter<LoginFormView>(loginErrorHandler, studentRepository) {

    private var lastError: Throwable? = null

    override fun onAttachView(view: LoginFormView) {
        super.onAttachView(view)
        view.run {
            initView()
            showContact(false)
            showVersion()

            loginErrorHandler.onBadCredentials = {
                setErrorPassIncorrect(it.takeIf { !it.isNullOrBlank() })
                showSoftKeyboard()
                Timber.i("Entered wrong username or password")
            }
        }
    }

    fun onPrivacyLinkClick() {
        view?.openPrivacyPolicyPage()
    }

    fun onAdvancedLoginClick() {
        view?.openAdvancedLogin()
    }

    fun onHostSelected() {
        view?.apply {
            clearPassError()
            clearUsernameError()
            clearHostError()
            if (formHostValue.contains("fakelog")) {
                setCredentials("jan@fakelog.cf", "jan123")
            } else if (formUsernameValue == "jan@fakelog.cf" && formPassValue == "jan123") {
                setCredentials("", "")
            }
            updateUsernameLabel()
        }
    }

    fun updateUsernameLabel() {
        view?.run {
            setUsernameLabel(if ("email" !in formHostValue) nicknameLabel else emailLabel)
        }
    }

    fun onPassTextChanged() {
        view?.clearPassError()
    }

    fun onUsernameTextChanged() {
        view?.clearUsernameError()

        val username = view?.formUsernameValue.orEmpty().trim()
        if ("@" in username && "@vulcan" !in username) {
            val hosts = view?.getHostsValues().orEmpty().associateBy { it.toUri().host }
            val usernameHost = username.substringAfter("@")

            hosts[usernameHost]?.let {
                view?.run {
                    setHost(it)
                    clearHostError()
                }
            }
        }
    }

    fun onSignInClick() {
        val email = view?.formUsernameValue.orEmpty().trim()
        val password = view?.formPassValue.orEmpty().trim()
        val host = view?.formHostValue.orEmpty().trim()
        val symbol = view?.formHostSymbol.orEmpty().trim()

        if (!validateCredentials(email, password, host)) return

        resourceFlow {
            studentRepository.getUserSubjectsFromScrapper(
                email = email,
                password = password,
                scrapperBaseUrl = host,
                symbol = symbol
            )
        }
            .logResourceStatus("login")
            .onResourceLoading {
                view?.run {
                    hideSoftKeyboard()
                    showProgress(true)
                    showContent(false)
                }
            }
            .onResourceSuccess {
                val loginData = LoginData(email, password, host, symbol)
                when (it.symbols.size) {
                    0 -> view?.navigateToSymbol(loginData)
                    else -> view?.navigateToStudentSelect(loginData, it)
                }
                analytics.logEvent(
                    "registration_form",
                    "success" to true,
                    "scrapperBaseUrl" to host,
                    "error" to "No error"
                )
            }
            .onResourceNotLoading {
                view?.apply {
                    showProgress(false)
                    showContent(true)
                }
            }
            .onResourceError {
                loginErrorHandler.dispatch(it)
                lastError = it
                view?.showContact(true)
                analytics.logEvent(
                    "registration_form",
                    "success" to false,
                    "scrapperBaseUrl" to host,
                    "error" to it.message.ifNullOrBlank { "No message" }
                )
            }
            .launch("login")
    }

    fun onFaqClick() {
        view?.openFaqPage()
    }

    fun onEmailClick() {
        view?.openEmail(lastError?.message.ifNullOrBlank { "none" })
    }

    fun onRecoverClick() {
        view?.onRecoverClick()
    }

    private fun validateCredentials(login: String, password: String, host: String): Boolean {
        var isCorrect = true

        if (login.isEmpty()) {
            view?.setErrorUsernameRequired()
            isCorrect = false
        } else {
            if ("@" in login && "login" in host) {
                view?.setErrorLoginRequired()
                isCorrect = false
            }
            if ("@" !in login && "email" in host) {
                view?.setErrorEmailRequired()
                isCorrect = false
            }
            if ("@" in login && "||" !in login && "login" !in host && "email" !in host) {
                val emailHost = login.substringAfter("@")
                val emailDomain = URL(host).host
                if (!emailHost.equals(emailDomain, true)) {
                    view?.setErrorEmailInvalid(domain = emailDomain)
                    isCorrect = false
                }
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

        return isCorrect
    }
}
