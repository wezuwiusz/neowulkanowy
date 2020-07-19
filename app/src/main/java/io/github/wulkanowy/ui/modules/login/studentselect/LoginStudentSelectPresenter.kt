package io.github.wulkanowy.ui.modules.login.studentselect

import io.github.wulkanowy.data.Status
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.repositories.student.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.modules.login.LoginErrorHandler
import io.github.wulkanowy.utils.FirebaseAnalyticsHelper
import io.github.wulkanowy.utils.SchedulersProvider
import io.github.wulkanowy.utils.flowWithResource
import io.github.wulkanowy.utils.ifNullOrBlank
import kotlinx.coroutines.flow.onEach
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

    private val selectedStudents = mutableListOf<Student>()

    fun onAttachView(view: LoginStudentSelectView, students: Serializable?) {
        super.onAttachView(view)
        with(view) {
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

    fun onItemSelected(student: Student, alreadySaved: Boolean) {
        if (alreadySaved) return

        selectedStudents
            .removeAll { it == student }
            .let { if (!it) selectedStudents.add(student) }

        view?.enableSignIn(selectedStudents.isNotEmpty())
    }

    private fun compareStudents(a: Student, b: Student): Boolean {
        return a.email == b.email
            && a.symbol == b.symbol
            && a.studentId == b.studentId
            && a.schoolSymbol == b.schoolSymbol
            && a.classId == b.classId
    }

    private fun loadData(students: List<Student>) {
        resetSelectedState()
        this.students = students

        flowWithResource { studentRepository.getSavedStudents(false) }.onEach {
            when (it.status) {
                Status.LOADING -> Timber.d("Login student select students load started")
                Status.SUCCESS -> view?.updateData(students.map { student ->
                    student to it.data!!.any { item -> compareStudents(student, item) }
                })
                Status.ERROR -> {
                    errorHandler.dispatch(it.error!!)
                    lastError = it.error
                    view?.updateData(students.map { student -> student to false })
                }
            }
        }.launch()
    }

    private fun resetSelectedState() {
        selectedStudents.clear()
        view?.enableSignIn(false)
    }

    private fun registerStudents(students: List<Student>) {
        flowWithResource {
            val savedStudents = studentRepository.saveStudents(students)
            val firstRegistered = students.first().apply { id = savedStudents.first() }
            studentRepository.switchStudent(firstRegistered)
        }.onEach {
            when (it.status) {
                Status.LOADING -> view?.run {
                    Timber.i("Registration started")
                    showProgress(true)
                    showContent(false)
                }
                Status.SUCCESS -> {
                    Timber.i("Registration result: Success")
                    view?.openMainView()
                    logRegisterEvent(students)
                }
                Status.ERROR -> {
                    Timber.i("Registration result: An exception occurred ")
                    view?.apply {
                        showProgress(false)
                        showContent(true)
                        showContact(true)
                    }
                    lastError = it.error
                    loginErrorHandler.dispatch(it.error!!)
                    logRegisterEvent(students, it.error)
                }
            }
        }.launch("register")
    }

    fun onDiscordClick() {
        view?.openDiscordInvite()
    }

    fun onEmailClick() {
        view?.openEmail(lastError?.message.ifNullOrBlank { "empty" })
    }

    private fun logRegisterEvent(students: List<Student>, error: Throwable? = null) {
        students.forEach { student ->
            analytics.logEvent(
                "registration_student_select",
                "success" to (error != null),
                "scrapperBaseUrl" to student.scrapperBaseUrl,
                "symbol" to student.symbol,
                "error" to (error?.message?.ifBlank { "No message" } ?: "No error"))
        }
    }
}
