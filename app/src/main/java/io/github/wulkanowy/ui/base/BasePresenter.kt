package io.github.wulkanowy.ui.base

import io.github.wulkanowy.data.repositories.StudentRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch
import timber.log.Timber

open class BasePresenter<T : BaseView>(
    protected val errorHandler: ErrorHandler,
    protected val studentRepository: StudentRepository
) {
    private val job = SupervisorJob()

    protected val presenterScope = CoroutineScope(job + Dispatchers.Main)

    private val childrenJobs = mutableMapOf<String, Job>()

    var view: T? = null

    open fun onAttachView(view: T) {
        this.view = view
        errorHandler.apply {
            showErrorMessage = view::showError
            onExpiredCredentials = view::showExpiredCredentialsDialog
            onCaptchaVerificationRequired = view::onCaptchaVerificationRequired
            onDecryptionFailed = view::showDecryptionFailedDialog
            onNoCurrentStudent = view::openClearLoginView
            onPasswordChangeRequired = view::showChangePasswordSnackbar
            onAuthorizationRequired = view::showAuthDialog
        }
    }

    fun onConfirmDecryptionFailedSelected() {
        Timber.i("Attempt to clear all data")

        presenterScope.launch {
            runCatching { studentRepository.clearAll() }
                .onFailure {
                    Timber.i("Clear data result: An exception occurred")
                    errorHandler.dispatch(it)
                }
                .onSuccess {
                    Timber.i("Clear data result: Open login view")
                    view?.openClearLoginView()
                }
        }
    }

    fun onConfirmExpiredCredentialsSelected() {
        Timber.i("Attempt to delete students associated with the account and switch to new student")

        presenterScope.launch {
            runCatching {
                val student = studentRepository.getCurrentStudent(false)
                studentRepository.deleteStudentsAssociatedWithAccount(student)

                val students = studentRepository.getSavedStudents(false)
                if (students.isNotEmpty()) {
                    Timber.i("Switching current student")
                    studentRepository.switchStudent(students[0])
                }
            }
                .onFailure {
                    Timber.i("Delete students result: An exception occurred")
                    errorHandler.dispatch(it)
                }
                .onSuccess {
                    Timber.i("Delete students result: Open login view")
                    view?.openClearLoginView()
                }
        }
    }

    fun <T> Flow<T>.launch(individualJobTag: String = "load"): Job {
        childrenJobs[individualJobTag]?.cancel()
        val job = catch { errorHandler.dispatch(it) }.launchIn(presenterScope)
        childrenJobs[individualJobTag] = job
        Timber.d("Job $individualJobTag launched in ${this@BasePresenter.javaClass.simpleName}: $job")
        return job
    }

    fun cancelJobs(vararg names: String) {
        names.forEach {
            childrenJobs[it]?.cancel()
        }
    }

    open fun onDetachView() {
        job.cancelChildren()
        errorHandler.clear()
        view = null
    }
}
