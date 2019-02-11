package io.github.wulkanowy.ui.modules.login.studentselect

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
import java.io.Serializable
import javax.inject.Inject

class LoginStudentSelectPresenter @Inject constructor(
    private val errorHandler: LoginErrorHandler,
    private val studentRepository: StudentRepository,
    private val semesterRepository: SemesterRepository,
    private val schedulers: SchedulersProvider,
    private val analytics: FirebaseAnalyticsHelper
) : BasePresenter<LoginStudentSelectView>(errorHandler) {

    var students = emptyList<Student>()

    fun onAttachView(view: LoginStudentSelectView, students: Serializable?) {
        super.onAttachView(view)
        view.run {
            initView()
            errorHandler.onStudentDuplicate = {
                showMessage(it)
                Timber.i("The student already registered in the app was selected")
            }
        }

        if (students is List<*> && students.isNotEmpty()) {
            loadData(students.filterIsInstance<Student>())
        }
    }

    fun onParentInitStudentSelectView(students: List<Student>) {
        loadData(students)
    }

    fun onItemSelected(item: AbstractFlexibleItem<*>?) {
        if (item is LoginStudentSelectItem) {
            registerStudent(item.student)
        }
    }

    private fun loadData(students: List<Student>) {
        this.students = students
        view?.apply {
            updateData(students.map { LoginStudentSelectItem(it) })
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
                analytics.logEvent("registration_student_select", SUCCESS to true, "endpoint" to student.endpoint, "symbol" to student.symbol, "error" to "No error")
                Timber.i("Registration result: Success")
                view?.openMainView()
            }, {
                analytics.logEvent("registration_student_select", SUCCESS to false, "endpoint" to student.endpoint, "symbol" to student.symbol, "error" to it.localizedMessage)
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
