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
                    } else if (it.isEmpty() && wasEmpty) {
                        showSymbolInput()
                        setErrorSymbolIncorrect()
                        analytics.logEvent(SIGN_UP, mapOf(SUCCESS to false, "students" to it.size, "endpoint" to endpoint, GROUP_ID to symbol.ifEmpty { "nil" }))
                    } else {
                        switchOptionsView()
                    }
                }
            }, {
                analytics.logEvent(SIGN_UP, mapOf(SUCCESS to true, "endpoint" to endpoint, GROUP_ID to symbol.ifEmpty { "nil" }))
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
