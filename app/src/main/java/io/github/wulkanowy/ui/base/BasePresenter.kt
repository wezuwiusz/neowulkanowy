package io.github.wulkanowy.ui.base

import io.github.wulkanowy.data.Status
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.utils.flowWithResource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
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
            onSessionExpired = view::showExpiredDialog
            onNoCurrentStudent = view::openClearLoginView
            onPasswordChangeRequired = view::showChangePasswordSnackbar
        }
    }

    fun onExpiredLoginSelected() {
        flowWithResource {
            val student = studentRepository.getCurrentStudent(false)
            studentRepository.logoutStudent(student)

            val students = studentRepository.getSavedStudents(false)
            if (students.isNotEmpty()) {
                Timber.i("Switching current student")
                studentRepository.switchStudent(students[0])
            }
        }.onEach {
            when (it.status) {
                Status.LOADING -> Timber.i("Attempt to switch the student after the session expires")
                Status.SUCCESS -> {
                    Timber.i("Switch student result: Open login view")
                    view?.openClearLoginView()
                }
                Status.ERROR -> {
                    Timber.i("Switch student result: An exception occurred")
                    errorHandler.dispatch(it.error!!)
                }
            }
        }.launch("expired")
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
