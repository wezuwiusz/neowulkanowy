package io.github.wulkanowy.ui.modules.login.options

import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import io.github.wulkanowy.data.ErrorHandler
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.repositories.SessionRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.utils.SchedulersProvider
import io.github.wulkanowy.utils.logRegister
import javax.inject.Inject

class LoginOptionsPresenter @Inject constructor(
    private val errorHandler: ErrorHandler,
    private val repository: SessionRepository,
    private val schedulers: SchedulersProvider
) : BasePresenter<LoginOptionsView>(errorHandler) {

    override fun onAttachView(view: LoginOptionsView) {
        super.onAttachView(view)
        view.initView()
    }

    fun onParentViewLoadData() {
        disposable.add(repository.cachedStudents
            .observeOn(schedulers.mainThread)
            .subscribeOn(schedulers.backgroundThread)
            .doOnSubscribe { view?.showActionBar(true) }
            .subscribe({ view?.updateData(it.map { student -> LoginOptionsItem(student) }) }, { errorHandler.proceed(it) }))
    }

    fun onSelectItem(item: AbstractFlexibleItem<*>?) {
        if (item is LoginOptionsItem) {
            registerStudent(item.student)
        }
    }

    private fun registerStudent(student: Student) {
        disposable.add(repository.saveStudent(student)
            .subscribeOn(schedulers.backgroundThread)
            .observeOn(schedulers.mainThread)
            .doOnSubscribe {
                view?.run {
                    showProgress(true)
                    showContent(false)
                    showActionBar(false)
                }
            }
            .subscribe({
                logRegister("Success", true, student.symbol, student.endpoint)
                view?.openMainView()
            }, { errorHandler.proceed(it) }))
    }
}
