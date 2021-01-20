package io.github.wulkanowy.ui.modules.login.form

import androidx.core.net.toUri
import io.github.wulkanowy.data.Status
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.modules.login.LoginErrorHandler
import io.github.wulkanowy.utils.AnalyticsHelper
import io.github.wulkanowy.utils.afterLoading
import io.github.wulkanowy.utils.flowWithResource
import io.github.wulkanowy.utils.ifNullOrBlank
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
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
                setErrorPassIncorrect()
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
            if (formHostValue.contains("fakelog")) {
                setCredentials("jan@fakelog.cf", "jan123")
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
            val hosts = view?.getHostsValues().orEmpty().map { it.toUri().host to it }.toMap()
            val usernameHost = username.substringAfter("@")

            hosts[usernameHost]?.let {
                view?.setHost(it)
            }
        }
    }

    fun onSignInClick() {
        val email = view?.formUsernameValue.orEmpty().trim()
        val password = view?.formPassValue.orEmpty().trim()
        val host = view?.formHostValue.orEmpty().trim()
        val symbol = view?.formHostSymbol.orEmpty().trim()

        if (!validateCredentials(email, password, host)) return

        flowWithResource { studentRepository.getStudentsScrapper(email, password, host, symbol) }.onEach {
            when (it.status) {
                Status.LOADING -> view?.run {
                    Timber.i("Login started")
                    hideSoftKeyboard()
                    showProgress(true)
                    showContent(false)
                }
                Status.SUCCESS -> {
                    Timber.i("Login result: Success")
                    analytics.logEvent(
                        "registration_form",
                        "success" to true,
                        "students" to it.data!!.size,
                        "scrapperBaseUrl" to host,
                        "error" to "No error"
                    )
                    view?.notifyParentAccountLogged(it.data, Triple(email, password, host))
                }
                Status.ERROR -> {
                    Timber.i("Login result: An exception occurred")
                    analytics.logEvent(
                        "registration_form",
                        "success" to false,
                        "students" to -1,
                        "scrapperBaseUrl" to host,
                        "error" to it.error!!.message.ifNullOrBlank { "No message" })
                    loginErrorHandler.dispatch(it.error)
                    lastError = it.error
                    view?.showContact(true)
                }
            }
        }.afterLoading {
            view?.apply {
                showProgress(false)
                showContent(true)
            }
        }.launch("login")
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
