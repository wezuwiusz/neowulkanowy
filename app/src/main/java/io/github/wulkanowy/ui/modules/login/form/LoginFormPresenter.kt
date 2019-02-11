package io.github.wulkanowy.ui.modules.login.form

import com.google.firebase.analytics.FirebaseAnalytics.Param.SUCCESS
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.modules.login.LoginErrorHandler
import io.github.wulkanowy.utils.FirebaseAnalyticsHelper
import io.github.wulkanowy.utils.SchedulersProvider
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

class LoginFormPresenter @Inject constructor(
    private val schedulers: SchedulersProvider,
    private val errorHandler: LoginErrorHandler,
    private val studentRepository: StudentRepository,
    private val analytics: FirebaseAnalyticsHelper,
    @param:Named("isDebug") private val isDebug: Boolean
) : BasePresenter<LoginFormView>(errorHandler) {

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

    fun attemptLogin(email: String, password: String, endpoint: String) {
        if (!validateCredentials(email, password)) return

        disposable.add(studentRepository.getStudents(email, password, endpoint)
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
                Timber.i("Login result: Success")
                analytics.logEvent("registration_form", SUCCESS to true, "students" to it.size, "endpoint" to endpoint, "error" to "No error")
                view?.notifyParentAccountLogged(it)
            }, {
                Timber.i("Login result: An exception occurred")
                analytics.logEvent("registration_form", SUCCESS to false, "students" to -1, "endpoint" to endpoint, "error" to it.localizedMessage)
                errorHandler.dispatch(it)
            }))
    }

    private fun validateCredentials(login: String, password: String): Boolean {
        var isCorrect = true

        if (login.isEmpty()) {
            view?.setErrorNameRequired()
            isCorrect = false
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
