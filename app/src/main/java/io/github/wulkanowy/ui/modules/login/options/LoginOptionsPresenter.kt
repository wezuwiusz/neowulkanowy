package io.github.wulkanowy.ui.modules.login.options

import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.repositories.SemesterRepository
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.modules.login.LoginErrorHandler
import io.github.wulkanowy.utils.SchedulersProvider
import io.github.wulkanowy.utils.logRegister
import io.reactivex.Single
import javax.inject.Inject

class LoginOptionsPresenter @Inject constructor(
    private val errorHandler: LoginErrorHandler,
    private val studentRepository: StudentRepository,
    private val semesterRepository: SemesterRepository,
    private val schedulers: SchedulersProvider
) : BasePresenter<LoginOptionsView>(errorHandler) {

    override fun onAttachView(view: LoginOptionsView) {
        super.onAttachView(view)
        view.initView()
    }

    fun onParentViewLoadData() {
        disposable.add(studentRepository.cachedStudents
            .observeOn(schedulers.mainThread)
            .subscribeOn(schedulers.backgroundThread)
            .doOnSubscribe { view?.showActionBar(true) }
            .subscribe({ view?.updateData(it.map { student -> LoginOptionsItem(student) }) }, { errorHandler.proceed(it) }))
    }

    fun onItemSelected(item: AbstractFlexibleItem<*>?) {
        if (item is LoginOptionsItem) {
            registerStudent(item.student)
        }
    }

    private fun registerStudent(student: Student) {
        disposable.add(studentRepository.saveStudent(student.apply { isCurrent = true })
            .andThen(semesterRepository.getSemesters(student, true))
            .onErrorResumeNext { studentRepository.logoutCurrentStudent().andThen(Single.error(it)) }
            .subscribeOn(schedulers.backgroundThread)
            .observeOn(schedulers.mainThread)
            .doOnSubscribe {
                view?.apply {
                    showProgress(true)
                    showContent(false)
                    showActionBar(false)
                }
            }
            .subscribe({
                logRegister("Success", true, student.symbol, student.endpoint)
                view?.openMainView()
            }, {
                errorHandler.proceed(it)
                view?.apply {
                    showProgress(false)
                    showContent(true)
                    showActionBar(true)
                }
            }))
    }
}
