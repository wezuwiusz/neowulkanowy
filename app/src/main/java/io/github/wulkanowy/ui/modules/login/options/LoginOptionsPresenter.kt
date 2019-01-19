package io.github.wulkanowy.ui.modules.login.options

import com.google.firebase.analytics.FirebaseAnalytics.Event.SIGN_UP
import com.google.firebase.analytics.FirebaseAnalytics.Param.GROUP_ID
import com.google.firebase.analytics.FirebaseAnalytics.Param.SUCCESS
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.repositories.SemesterRepository
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.modules.login.LoginErrorHandler
import io.github.wulkanowy.utils.FirebaseAnalyticsHelper
import io.github.wulkanowy.utils.SchedulersProvider
import io.reactivex.Single
import timber.log.Timber
import javax.inject.Inject

class LoginOptionsPresenter @Inject constructor(
    private val errorHandler: LoginErrorHandler,
    private val studentRepository: StudentRepository,
    private val semesterRepository: SemesterRepository,
    private val schedulers: SchedulersProvider,
    private val analytics: FirebaseAnalyticsHelper
) : BasePresenter<LoginOptionsView>(errorHandler) {

    override fun onAttachView(view: LoginOptionsView) {
        super.onAttachView(view)
        view.run {
            initView()
            errorHandler.onStudentDuplicate = {
                showMessage(it)
                Timber.i("The student already registered in the app was selected")
            }
        }
    }

    fun onParentViewLoadData() {
        disposable.add(studentRepository.cachedStudents
            .observeOn(schedulers.mainThread)
            .subscribeOn(schedulers.backgroundThread)
            .doOnSubscribe { view?.showActionBar(true) }
            .subscribe({ view?.updateData(it.map { student -> LoginOptionsItem(student) }) }, { errorHandler.dispatch(it) }))
    }

    fun onItemSelected(item: AbstractFlexibleItem<*>?) {
        if (item is LoginOptionsItem) {
            registerStudent(item.student)
        }
    }

    private fun registerStudent(student: Student) {
        disposable.add(studentRepository.saveStudent(student)
            .map { student.apply { id = it } }
            .flatMap { semesterRepository.getSemesters(student, true) }
            .onErrorResumeNext { studentRepository.logoutStudent(student).andThen(Single.error(it)) }
            .flatMapCompletable { studentRepository.switchStudent(student) }
            .subscribeOn(schedulers.backgroundThread)
            .observeOn(schedulers.mainThread)
            .doOnSubscribe {
                view?.apply {
                    showProgress(true)
                    showContent(false)
                    showActionBar(false)
                }
                Timber.i("Registration started")
            }
            .subscribe({
                analytics.logEvent(SIGN_UP, mapOf(SUCCESS to true, "endpoint" to student.endpoint, "message" to "Success", GROUP_ID to student.symbol))
                Timber.i("Registration result: Success")
                view?.openMainView()
            }, {
                Timber.i("Registration result: An exception occurred ")
                errorHandler.dispatch(it)
                view?.apply {
                    showProgress(false)
                    showContent(true)
                    showActionBar(true)
                }
            }))
    }
}
