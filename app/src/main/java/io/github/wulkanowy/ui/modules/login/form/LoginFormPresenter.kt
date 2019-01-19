package io.github.wulkanowy.ui.modules.login.form

import com.google.firebase.analytics.FirebaseAnalytics.Event.SIGN_UP
import com.google.firebase.analytics.FirebaseAnalytics.Param.GROUP_ID
import com.google.firebase.analytics.FirebaseAnalytics.Param.SUCCESS
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.modules.login.LoginErrorHandler
import io.github.wulkanowy.utils.FirebaseAnalyticsHelper
import io.github.wulkanowy.utils.SchedulersProvider
import timber.log.Timber
import javax.inject.Inject

class LoginFormPresenter @Inject constructor(
    private val schedulers: SchedulersProvider,
    private val errorHandler: LoginErrorHandler,
    private val studentRepository: StudentRepository,
    private val analytics: FirebaseAnalyticsHelper
) : BasePresenter<LoginFormView>(errorHandler) {

    private var wasEmpty = false

    override fun onAttachView(view: LoginFormView) {
        super.onAttachView(view)
        view.run {
            initView()
            if (isDebug) showVersion()
            errorHandler.onBadCredentials = {
                setErrorPassIncorrect()
                showSoftKeyboard()
                Timber.i("Entered wrong username or password")
            }
        }
    }

    fun attemptLogin(email: String, password: String, symbol: String, endpoint: String) {
        if (!validateCredentials(email, password, symbol)) return

        disposable.add(studentRepository.getStudents(email, password, symbol, endpoint)
            .subscribeOn(schedulers.backgroundThread)
            .observeOn(schedulers.mainThread)
            .doOnSubscribe {
                view?.apply {
                    hideSoftKeyboard()
                    showProgress(true)
                    showContent(false)
                }
                Timber.i("Login started")
            }
            .doFinally {
                view?.apply {
                    showProgress(false)
                    showContent(true)
                }
            }
            .subscribe({
                view?.run {
                    if (it.isEmpty() && !wasEmpty) {
                        showSymbolInput()
                        wasEmpty = true
                        analytics.logEvent("sign_up_send", mapOf(SUCCESS to false, "students" to 0, "endpoint" to endpoint, GROUP_ID to symbol.ifEmpty { "null" }))
                        Timber.i("Login result: Empty student list")
                    } else if (it.isEmpty() && wasEmpty) {
                        showSymbolInput()
                        setErrorSymbolIncorrect()
                        analytics.logEvent("sign_up_send", mapOf(SUCCESS to false, "students" to it.size, "endpoint" to endpoint, GROUP_ID to symbol.ifEmpty { "null" }))
                        Timber.i("Login result: Wrong symbol")
                    } else {
                        analytics.logEvent("sign_up_send", mapOf(SUCCESS to true, "students" to it.size, "endpoint" to endpoint, GROUP_ID to symbol))
                        Timber.i("Login result: Success")
                        switchOptionsView()
                    }
                }
            }, {
                analytics.logEvent(SIGN_UP, mapOf(SUCCESS to false, "endpoint" to endpoint, "message" to it.localizedMessage, GROUP_ID to symbol.ifEmpty { "null" }))
                Timber.i("Login result: An exception occurred")
                errorHandler.dispatch(it)
            }))
    }

    private fun validateCredentials(login: String, password: String, symbol: String): Boolean {
        var isCorrect = true

        if (login.isEmpty()) {
            view?.setErrorNicknameRequired()
            isCorrect = false
        }

        if (password.isEmpty()) {
            view?.setErrorPassRequired(focus = isCorrect)
            isCorrect = false
        }

        if (symbol.isEmpty() && wasEmpty) {
            view?.setErrorSymbolRequire()
            isCorrect = false
        }

        if (password.length < 6 && password.isNotEmpty()) {
            view?.setErrorPassInvalid(focus = isCorrect)
            isCorrect = false
        }
        return isCorrect
    }
}
