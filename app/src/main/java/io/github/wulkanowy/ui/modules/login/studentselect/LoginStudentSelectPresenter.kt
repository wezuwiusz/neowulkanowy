package io.github.wulkanowy.ui.modules.login.studentselect

import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.repositories.student.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.modules.login.LoginErrorHandler
import io.github.wulkanowy.utils.FirebaseAnalyticsHelper
import io.github.wulkanowy.utils.SchedulersProvider
import io.github.wulkanowy.utils.ifNullOrBlank
import timber.log.Timber
import java.io.Serializable
import javax.inject.Inject

class LoginStudentSelectPresenter @Inject constructor(
    schedulers: SchedulersProvider,
    studentRepository: StudentRepository,
    private val loginErrorHandler: LoginErrorHandler,
    private val analytics: FirebaseAnalyticsHelper
) : BasePresenter<LoginStudentSelectView>(loginErrorHandler, studentRepository, schedulers) {

    private var lastError: Throwable? = null

    var students = emptyList<Student>()

    private var selectedStudents = mutableListOf<Student>()

    fun onAttachView(view: LoginStudentSelectView, students: Serializable?) {
        super.onAttachView(view)
        view.run {
            initView()
            showContact(false)
            enableSignIn(false)
            loginErrorHandler.onStudentDuplicate = {
                showMessage(it)
                Timber.i("The student already registered in the app was selected")
            }
        }

        if (students is List<*> && students.isNotEmpty()) {
            loadData(students.filterIsInstance<Student>())
        }
    }

    fun onSignIn() {
        registerStudents(selectedStudents)
    }

    fun onParentInitStudentSelectView(students: List<Student>) {
        loadData(students)
        if (students.size == 1) registerStudents(students)
    }

    fun onItemSelected(item: AbstractFlexibleItem<*>?) {
        if (item is LoginStudentSelectItem && !item.alreadySaved) {
            selectedStudents.removeAll { it == item.student }
                .let { if (!it) selectedStudents.add(item.student) }

            view?.enableSignIn(selectedStudents.isNotEmpty())
        }
    }

    private fun compareStudents(a: Student, b: Student): Boolean {
        return a.email == b.email
            && a.symbol == b.symbol
            && a.studentId == b.studentId
            && a.schoolSymbol == b.schoolSymbol
            && a.classId == b.classId
    }

    private fun loadData(students: List<Student>) {
        this.students = students
        disposable.add(studentRepository.getSavedStudents()
            .map { savedStudents ->
                students.map { student ->
                    Pair(student, savedStudents.any { compareStudents(student, it) })
                }
            }
            .subscribeOn(schedulers.backgroundThread)
            .observeOn(schedulers.mainThread)
            .subscribe({
                view?.updateData(it.map { studentPair ->
                    LoginStudentSelectItem(studentPair.first, studentPair.second)
                })
            }, {
                errorHandler.dispatch(it)
                lastError = it
                view?.updateData(students.map { student -> LoginStudentSelectItem(student, false) })
            })
        )
    }

    private fun registerStudents(students: List<Student>) {
        disposable.add(studentRepository.saveStudents(students)
            .map { students.first().apply { id = it.first() } }
            .flatMapCompletable { studentRepository.switchStudent(it) }
            .subscribeOn(schedulers.backgroundThread)
            .observeOn(schedulers.mainThread)
            .doOnSubscribe {
                view?.apply {
                    showProgress(true)
                    showContent(false)
                }
                Timber.i("Registration started")
            }
            .subscribe({
                students.forEach { analytics.logEvent("registration_student_select", "success" to true, "scrapperBaseUrl" to it.scrapperBaseUrl, "symbol" to it.symbol, "error" to "No error") }
                Timber.i("Registration result: Success")
                view?.openMainView()
            }, { error ->
                students.forEach { analytics.logEvent("registration_student_select", "success" to false, "scrapperBaseUrl" to it.scrapperBaseUrl, "symbol" to it.symbol, "error" to error.message.ifNullOrBlank { "No message" }) }
                Timber.i("Registration result: An exception occurred ")
                loginErrorHandler.dispatch(error)
                lastError = error
                view?.apply {
                    showProgress(false)
                    showContent(true)
                    showContact(true)
                }
            }))
    }

    fun onDiscordClick() {
        view?.openDiscordInvite()
    }

    fun onEmailClick() {
        view?.openEmail(lastError?.message.ifNullOrBlank { "empty" })
    }
}
