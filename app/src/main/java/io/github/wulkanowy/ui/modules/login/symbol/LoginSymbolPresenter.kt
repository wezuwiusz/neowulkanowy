package io.github.wulkanowy.ui.modules.login.symbol

import io.github.wulkanowy.data.repositories.student.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.modules.login.LoginErrorHandler
import io.github.wulkanowy.utils.FirebaseAnalyticsHelper
import io.github.wulkanowy.utils.SchedulersProvider
import io.reactivex.Single
import timber.log.Timber
import java.io.Serializable
import javax.inject.Inject

class LoginSymbolPresenter @Inject constructor(
    studentRepository: StudentRepository,
    schedulers: SchedulersProvider,
    private val loginErrorHandler: LoginErrorHandler,
    private val analytics: FirebaseAnalyticsHelper
) : BasePresenter<LoginSymbolView>(loginErrorHandler, studentRepository, schedulers) {

    var loginData: Triple<String, String, String>? = null

    @Suppress("UNCHECKED_CAST")
    fun onAttachView(view: LoginSymbolView, savedLoginData: Serializable?) {
        super.onAttachView(view)
        view.initView()
        if (savedLoginData is Triple<*, *, *>) {
            loginData = savedLoginData as Triple<String, String, String>
        }
    }

    fun onSymbolTextChanged() {
        view?.apply { if (symbolNameError != null) clearSymbolError() }
    }

    fun attemptLogin(symbol: String) {
        if (symbol.isBlank()) {
            view?.setErrorSymbolRequire()
            return
        }

        disposable.add(
            Single.fromCallable { if (loginData == null) throw IllegalArgumentException("Login data is null") else loginData }
                .flatMap { studentRepository.getStudents(it.first, it.second, it.third, symbol) }
                .subscribeOn(schedulers.backgroundThread)
                .observeOn(schedulers.mainThread)
                .doOnSubscribe {
                    view?.apply {
                        hideSoftKeyboard()
                        showProgress(true)
                        showContent(false)
                    }
                    Timber.i("Login with symbol started")
                }
                .doFinally {
                    view?.apply {
                        showProgress(false)
                        showContent(true)
                    }
                }
                .subscribe({
                    analytics.logEvent("registration_symbol", "success" to true, "students" to it.size, "endpoint" to loginData?.third, "symbol" to symbol, "error" to "No error")
                    view?.apply {
                        if (it.isEmpty()) {
                            Timber.i("Login with symbol result: Empty student list")
                            setErrorSymbolIncorrect()
                        } else {
                            Timber.i("Login with symbol result: Success")
                            notifyParentAccountLogged(it)
                        }
                    }
                }, {
                    Timber.i("Login with symbol result: An exception occurred")
                    analytics.logEvent("registration_symbol", "success" to false, "students" to -1, "endpoint" to loginData?.third, "symbol" to symbol, "error" to it.localizedMessage.ifEmpty { "No message" })
                    loginErrorHandler.dispatch(it)
                }))
    }

    fun onParentInitSymbolView(loginData: Triple<String, String, String>) {
        this.loginData = loginData
        view?.apply {
            clearAndFocusSymbol()
            showSoftKeyboard()
        }
    }
}
