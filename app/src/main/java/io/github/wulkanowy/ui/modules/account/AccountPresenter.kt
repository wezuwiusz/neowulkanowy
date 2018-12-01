package io.github.wulkanowy.ui.modules.account

import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.modules.main.MainErrorHandler
import io.github.wulkanowy.utils.SchedulersProvider
import io.reactivex.Single
import javax.inject.Inject

class AccountPresenter @Inject constructor(
    private val errorHandler: MainErrorHandler,
    private val studentRepository: StudentRepository,
    private val schedulers: SchedulersProvider
) : BasePresenter<AccountView>(errorHandler) {

    override fun onAttachView(view: AccountView) {
        super.onAttachView(view)
        view.initView()
        loadData()
    }

    fun onAddSelected() {
        view?.openLoginView()
    }

    fun onRemoveSelected() {
        view?.showConfirmDialog()
    }

    fun onLogoutConfirm() {
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
                    if (it.isEmpty()) openClearLoginView()
                    else recreateView()
                }
            }, { errorHandler.dispatch(it) }))
    }

    fun onItemSelected(item: AbstractFlexibleItem<*>) {
        if (item is AccountItem) {
            if (item.student.isCurrent) {
                view?.dismissView()
            } else {
                disposable.add(studentRepository.switchStudent(item.student)
                    .subscribeOn(schedulers.backgroundThread)
                    .observeOn(schedulers.mainThread)
                    .subscribe({ view?.recreateView() }, { errorHandler.dispatch(it) }))
            }
        }
    }

    private fun loadData() {
        disposable.add(studentRepository.getSavedStudents(false)
            .map { it.map { item -> AccountItem(item) } }
            .subscribeOn(schedulers.backgroundThread)
            .observeOn(schedulers.mainThread)
            .subscribe({ view?.updateData(it) }, { errorHandler.dispatch(it) }))
    }
}

