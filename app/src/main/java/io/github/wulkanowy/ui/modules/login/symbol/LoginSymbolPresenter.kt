package io.github.wulkanowy.ui.modules.login.symbol

import io.github.wulkanowy.data.Resource
import io.github.wulkanowy.data.dataOrNull
import io.github.wulkanowy.data.onResourceNotLoading
import io.github.wulkanowy.data.pojos.RegisterUser
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.data.resourceFlow
import io.github.wulkanowy.sdk.scrapper.getNormalizedSymbol
import io.github.wulkanowy.sdk.scrapper.login.InvalidSymbolException
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.modules.login.LoginData
import io.github.wulkanowy.ui.modules.login.LoginErrorHandler
import io.github.wulkanowy.utils.AnalyticsHelper
import io.github.wulkanowy.utils.ifNullOrBlank
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import javax.inject.Inject

class LoginSymbolPresenter @Inject constructor(
    studentRepository: StudentRepository,
    private val loginErrorHandler: LoginErrorHandler,
    private val analytics: AnalyticsHelper
) : BasePresenter<LoginSymbolView>(loginErrorHandler, studentRepository) {

    private var lastError: Throwable? = null

    lateinit var loginData: LoginData

    private var registerUser: RegisterUser? = null

    fun onAttachView(view: LoginSymbolView, loginData: LoginData) {
        super.onAttachView(view)
        this.loginData = loginData
        loginErrorHandler.onBadCredentials = {
            view.setErrorSymbol(it.orEmpty())
        }
        with(view) {
            initView()
            showContact(false)
            setLoginToHeading(loginData.login)
            clearAndFocusSymbol()
            showSoftKeyboard()
        }
    }

    fun onSymbolTextChanged() {
        view?.apply { if (symbolNameError != null) clearSymbolError() }
    }

    fun attemptLogin() {
        if (view?.symbolValue.isNullOrBlank()) {
            view?.setErrorSymbolRequire()
            return
        }

        loginData = loginData.copy(
            symbol = view?.symbolValue?.getNormalizedSymbol(),
        )
        resourceFlow {
            studentRepository.getUserSubjectsFromScrapper(
                email = loginData.login,
                password = loginData.password,
                scrapperBaseUrl = loginData.baseUrl,
                domainSuffix = loginData.domainSuffix,
                symbol = loginData.symbol.orEmpty(),
            )
        }.onEach { user ->
            registerUser = user.dataOrNull
            when (user) {
                is Resource.Loading -> view?.run {
                    Timber.i("Login with symbol started")
                    hideSoftKeyboard()
                    showProgress(true)
                    showContent(false)
                }
                is Resource.Success -> {
                    when (user.data.symbols.size) {
                        0 -> {
                            Timber.i("Login with symbol result: Empty student list")
                            view?.run {
                                setErrorSymbolIncorrect()
                                showContact(true)
                            }
                        }
                        else -> {
                            val enteredSymbolDetails = user.data.symbols
                                .firstOrNull()
                                ?.takeIf { it.symbol == loginData.symbol }

                            if (enteredSymbolDetails?.error is InvalidSymbolException) {
                                view?.run {
                                    setErrorSymbolInvalid()
                                    showContact(true)
                                }
                            } else {
                                Timber.i("Login with symbol result: Success")
                                view?.navigateToStudentSelect(loginData, requireNotNull(user.data))
                            }
                        }
                    }
                    analytics.logEvent(
                        "registration_symbol",
                        "success" to true,
                        "scrapperBaseUrl" to loginData.baseUrl,
                        "symbol" to view?.symbolValue,
                        "error" to "No error"
                    )
                }
                is Resource.Error -> {
                    Timber.i("Login with symbol result: An exception occurred")
                    analytics.logEvent(
                        "registration_symbol",
                        "success" to false,
                        "students" to -1,
                        "scrapperBaseUrl" to loginData.baseUrl,
                        "symbol" to view?.symbolValue,
                        "error" to user.error.message.ifNullOrBlank { "No message" }
                    )
                    loginErrorHandler.dispatch(user.error)
                    lastError = user.error
                    view?.showContact(true)
                }
            }
        }.onResourceNotLoading {
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
        view?.openEmail(loginData.baseUrl, lastError?.message.ifNullOrBlank {
            registerUser?.symbols?.flatMap { symbol ->
                symbol.schools.map { it.error?.message } + symbol.error?.message
            }?.filterNotNull()?.distinct()?.joinToString(";") {
                it.take(46) + "..."
            } ?: "blank"
        })
    }
}
