package io.github.wulkanowy.ui.modules.login.form

import com.google.firebase.analytics.FirebaseAnalytics.Param.SUCCESS
import io.github.wulkanowy.data.repositories.student.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.modules.login.LoginErrorHandler
import io.github.wulkanowy.utils.FirebaseAnalyticsHelper
import io.github.wulkanowy.utils.SchedulersProvider
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

class LoginFormPresenter @Inject constructor(
    schedulers: SchedulersProvider,
    studentRepository: StudentRepository,
    private val loginErrorHandler: LoginErrorHandler,
    private val analytics: FirebaseAnalyticsHelper,
    @param:Named("isDebug") private val isDebug: Boolean
) : BasePresenter<LoginFormView>(loginErrorHandler, studentRepository, schedulers) {

    override fun onAttachView(view: LoginFormView) {
        super.onAttachView(view)
        view.run {
            initView()
            if (isDebug) showVersion() else showPrivacyPolicy()

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

    fun onHostSelected() {
        view?.apply {
            clearPassError()
            clearNameError()
            if (formHostValue?.contains("fakelog") == true) setDefaultCredentials("jan@fakelog.cf", "jan123")
        }
    }

    fun onPassTextChanged() {
        view?.clearPassError()
    }

    fun onNameTextChanged() {
        view?.clearNameError()
    }

    fun onSignInClick() {
        val email = view?.formNameValue.orEmpty()
        val password = view?.formPassValue.orEmpty()
        val endpoint = view?.formHostValue.orEmpty()

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
                loginErrorHandler.dispatch(it)
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
