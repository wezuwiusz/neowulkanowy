package io.github.wulkanowy.ui.modules.login.symbol

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
import java.io.Serializable
import javax.inject.Inject

class LoginSymbolPresenter @Inject constructor(
    studentRepository: StudentRepository,
    private val loginErrorHandler: LoginErrorHandler,
    private val analytics: AnalyticsHelper
) : BasePresenter<LoginSymbolView>(loginErrorHandler, studentRepository) {

    private var lastError: Throwable? = null

    var loginData: Triple<String, String, String>? = null

    @Suppress("UNCHECKED_CAST")
    fun onAttachView(view: LoginSymbolView, savedLoginData: Serializable?) {
        super.onAttachView(view)
        view.run {
            initView()
            showContact(false)
        }
        if (savedLoginData is Triple<*, *, *>) {
            loginData = savedLoginData as Triple<String, String, String>
        }
    }

    fun onSymbolTextChanged() {
        view?.apply { if (symbolNameError != null) clearSymbolError() }
    }

    fun attemptLogin(symbol: String) {
        if (loginData == null) throw IllegalArgumentException("Login data is null")

        if (symbol.isBlank()) {
            view?.setErrorSymbolRequire()
            return
        }

        flowWithResource { studentRepository.getStudentsScrapper(loginData!!.first, loginData!!.second, loginData!!.third, symbol) }.onEach {
            when (it.status) {
                Status.LOADING -> view?.run {
                    Timber.i("Login with symbol started")
                    hideSoftKeyboard()
                    showProgress(true)
                    showContent(false)
                }
                Status.SUCCESS -> {
                    view?.run {
                        if (it.data!!.isEmpty()) {
                            Timber.i("Login with symbol result: Empty student list")
                            setErrorSymbolIncorrect()
                            view?.showContact(true)
                        } else {
                            Timber.i("Login with symbol result: Success")
                            notifyParentAccountLogged(it.data)
                        }
                    }
                    analytics.logEvent(
                        "registration_symbol",
                        "success" to true,
                        "students" to it.data!!.size,
                        "scrapperBaseUrl" to loginData?.third,
                        "symbol" to symbol,
                        "error" to "No error"
                    )
                }
                Status.ERROR -> {
                    Timber.i("Login with symbol result: An exception occurred")
                    analytics.logEvent(
                        "registration_symbol",
                        "success" to false,
                        "students" to -1,
                        "scrapperBaseUrl" to loginData?.third,
                        "symbol" to symbol,
                        "error" to it.error!!.message.ifNullOrBlank { "No message" }
                    )
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

    fun onParentInitSymbolView(loginData: Triple<String, String, String>) {
        this.loginData = loginData
        view?.apply {
            clearAndFocusSymbol()
            showSoftKeyboard()
        }
    }

    fun onFaqClick() {
        view?.openFaqPage()
    }

    fun onEmailClick() {
        view?.openEmail(loginData?.third.orEmpty(), lastError?.message.ifNullOrBlank { "empty" })
    }
}
