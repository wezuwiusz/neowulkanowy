package io.github.wulkanowy.ui.modules.account

import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import io.github.wulkanowy.data.repositories.student.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.utils.SchedulersProvider
import io.reactivex.Single
import timber.log.Timber
import javax.inject.Inject

class AccountPresenter @Inject constructor(
    private val errorHandler: ErrorHandler,
    private val studentRepository: StudentRepository,
    private val schedulers: SchedulersProvider
) : BasePresenter<AccountView>(errorHandler) {

    override fun onAttachView(view: AccountView) {
        super.onAttachView(view)
        Timber.i("Account dialog is attached")
        view.initView()
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
                        openClearLoginView()
                    } else {
                        Timber.i("Logout result: Switch to another student")
                        recreateView()
                    }
                }
            }, {
                Timber.i("Logout result: An exception occurred")
                errorHandler.dispatch(it)
            }))
    }

    fun onItemSelected(item: AbstractFlexibleItem<*>) {
        if (item is AccountItem) {
            Timber.i("Select student item ${item.student.id}")
            if (item.student.isCurrent) {
                view?.dismissView()
            } else {
                Timber.i("Attempt to change a student")
                disposable.add(studentRepository.switchStudent(item.student)
                    .subscribeOn(schedulers.backgroundThread)
                    .observeOn(schedulers.mainThread)
                    .subscribe({
                        Timber.i("Change a student result: Success")
                        view?.recreateView()
                    }, {
                        Timber.i("Change a student result: An exception occurred")
                        errorHandler.dispatch(it)
                    }))
            }
        }
    }

    private fun loadData() {
        Timber.i("Loading account data started")
        disposable.add(studentRepository.getSavedStudents(false)
            .map { it.map { item -> AccountItem(item) } }
            .subscribeOn(schedulers.backgroundThread)
            .observeOn(schedulers.mainThread)
            .subscribe({
                Timber.i("Loading account result: Success")
                view?.updateData(it)
            }, {
                Timber.i("Loading account result: An exception occurred")
                errorHandler.dispatch(it)
            }))
    }
}

