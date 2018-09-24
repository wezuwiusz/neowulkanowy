package io.github.wulkanowy.ui.login.options

import io.github.wulkanowy.data.ErrorHandler
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.repositories.SessionRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.utils.schedulers.SchedulersManager
import javax.inject.Inject

class LoginOptionsPresenter @Inject constructor(
        private val errorHandler: ErrorHandler,
        private val repository: SessionRepository,
        private val schedulers: SchedulersManager)
    : BasePresenter<LoginOptionsView>(errorHandler) {

    override fun attachView(view: LoginOptionsView) {
        super.attachView(view)
        view.initRecycler()
    }

    fun refreshData() {
        disposable.add(repository.cachedStudents
                .observeOn(schedulers.mainThread())
                .subscribeOn(schedulers.backgroundThread())
                .doOnSubscribe { view?.showActionBar(true) }
                .doFinally { repository.clearCache() }
                .subscribe({
                    view?.updateData(it.map { student ->
                        LoginOptionsItem(student)
                    })
                }, { errorHandler.proceed(it) }))
    }

    fun onSelectStudent(student: Student) {
        disposable.add(repository.saveStudent(student)
                .subscribeOn(schedulers.backgroundThread())
                .observeOn(schedulers.mainThread())
                .doOnSubscribe { _ ->
                    view?.showLoginProgress(true)
                    view?.showActionBar(false)
                }
                .subscribe({ view?.openMainView() }, { errorHandler.proceed(it) }))
    }
}
