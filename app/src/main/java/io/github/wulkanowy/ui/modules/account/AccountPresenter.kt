package io.github.wulkanowy.ui.modules.account

import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.repositories.student.StudentRepository
import io.github.wulkanowy.services.sync.SyncManager
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.utils.SchedulersProvider
import io.reactivex.Single
import timber.log.Timber
import javax.inject.Inject

class AccountPresenter @Inject constructor(
    schedulers: SchedulersProvider,
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository,
    private val syncManager: SyncManager
) : BasePresenter<AccountView>(errorHandler, studentRepository, schedulers) {

    override fun onAttachView(view: AccountView) {
        super.onAttachView(view)
        view.initView()
        Timber.i("Account dialog view was initialized")
        loadData()
    }

    fun onAddSelected() {
        Timber.i("Select add account")
        view?.openLoginView()
    }

    fun onRemoveSelected() {
        Timber.i("Select remove account")
        view?.showConfirmDialog()
    }

    fun onLogoutConfirm() {
        Timber.i("Attempt to logout current user ")
        disposable.add(studentRepository.getCurrentStudent()
            .flatMapCompletable { studentRepository.logoutStudent(it) }
            .andThen(studentRepository.getSavedStudents(false))
            .flatMap {
                if (it.isNotEmpty()) studentRepository.switchStudent(it[0]).toSingle { it }
                else Single.just(it)
            }
            .subscribeOn(schedulers.backgroundThread)
            .observeOn(schedulers.mainThread)
            .doFinally { view?.dismissView() }
            .subscribe({
                view?.apply {
                    if (it.isEmpty()) {
                        Timber.i("Logout result: Open login view")
                        syncManager.stopSyncWorker()
                        openClearLoginView()
                    } else {
                        Timber.i("Logout result: Switch to another student")
                        recreateMainView()
                    }
                }
            }, {
                Timber.i("Logout result: An exception occurred")
                errorHandler.dispatch(it)
            }))
    }

    fun onItemSelected(student: Student) {
        Timber.i("Select student item ${student.id}")
        if (student.isCurrent) {
            view?.dismissView()
        } else {
            Timber.i("Attempt to change a student")
            disposable.add(studentRepository.switchStudent(student)
                .subscribeOn(schedulers.backgroundThread)
                .observeOn(schedulers.mainThread)
                .doFinally { view?.dismissView() }
                .subscribe({
                    Timber.i("Change a student result: Success")
                    view?.recreateMainView()
                }, {
                    Timber.i("Change a student result: An exception occurred")
                    errorHandler.dispatch(it)
                }))
        }
    }

    private fun createAccountItems(items: List<Student>): List<AccountItem<*>> {
        return items.groupBy { Account(it.email, it.isParent) }.map { (account, students) ->
            listOf(AccountItem(account, AccountItem.ViewType.HEADER)) + students.map { student ->
                AccountItem(student, AccountItem.ViewType.ITEM)
            }
        }.flatten()
    }

    private fun loadData() {
        Timber.i("Loading account data started")
        disposable.add(studentRepository.getSavedStudents(false)
            .subscribeOn(schedulers.backgroundThread)
            .observeOn(schedulers.mainThread)
            .map { createAccountItems(it) }
            .subscribe({
                Timber.i("Loading account result: Success")
                view?.updateData(it)
            }, {
                Timber.i("Loading account result: An exception occurred")
                errorHandler.dispatch(it)
            }))
    }
}
