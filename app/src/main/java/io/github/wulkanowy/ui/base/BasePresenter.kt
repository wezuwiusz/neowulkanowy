package io.github.wulkanowy.ui.base

import io.github.wulkanowy.data.repositories.student.StudentRepository
import io.github.wulkanowy.utils.SchedulersProvider
import io.reactivex.Completable
import io.reactivex.disposables.CompositeDisposable
import timber.log.Timber

open class BasePresenter<T : BaseView>(
    protected val errorHandler: ErrorHandler,
    protected val studentRepository: StudentRepository,
    protected val schedulers: SchedulersProvider
) {

    val disposable = CompositeDisposable()

    var view: T? = null

    open fun onAttachView(view: T) {
        this.view = view
        errorHandler.apply {
            showErrorMessage = view::showError
            onSessionExpired = view::showExpiredDialog
            onNoCurrentStudent = view::openClearLoginView
        }
    }

    fun onExpiredLoginSelected() {
        Timber.i("Attempt to switch the student after the session expires")
        disposable.add(studentRepository.getCurrentStudent(false)
            .flatMapCompletable { studentRepository.logoutStudent(it) }
            .andThen(studentRepository.getSavedStudents(false))
            .flatMapCompletable {
                if (it.isNotEmpty()) {
                    Timber.i("Switching current student")
                    studentRepository.switchStudent(it[0])
                } else Completable.complete()
            }
            .subscribeOn(schedulers.backgroundThread)
            .observeOn(schedulers.mainThread)
            .subscribe({
                Timber.i("Switch student result: Open login view")
                view?.openClearLoginView()
            }, {
                Timber.i("Switch student result: An exception occurred")
                errorHandler.dispatch(it)
            }))
    }

    open fun onDetachView() {
        view = null
        disposable.clear()
        errorHandler.clear()
    }
}
