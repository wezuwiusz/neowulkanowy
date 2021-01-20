package io.github.wulkanowy.ui.modules.login.studentselect

import io.github.wulkanowy.data.Status
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.db.entities.StudentWithSemesters
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.modules.login.LoginErrorHandler
import io.github.wulkanowy.utils.AnalyticsHelper
import io.github.wulkanowy.utils.flowWithResource
import io.github.wulkanowy.utils.ifNullOrBlank
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import java.io.Serializable
import javax.inject.Inject

class LoginStudentSelectPresenter @Inject constructor(
    studentRepository: StudentRepository,
    private val loginErrorHandler: LoginErrorHandler,
    private val analytics: AnalyticsHelper
) : BasePresenter<LoginStudentSelectView>(loginErrorHandler, studentRepository) {

    private var lastError: Throwable? = null

    var students = emptyList<StudentWithSemesters>()

    private val selectedStudents = mutableListOf<StudentWithSemesters>()

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
            loadData(students.filterIsInstance<StudentWithSemesters>())
        }
    }

    fun onSignIn() {
        registerStudents(selectedStudents)
    }

    fun onParentInitStudentSelectView(studentsWithSemesters: List<StudentWithSemesters>) {
        loadData(studentsWithSemesters)
        if (studentsWithSemesters.size == 1) registerStudents(studentsWithSemesters)
    }

    fun onItemSelected(studentWithSemester: StudentWithSemesters, alreadySaved: Boolean) {
        if (alreadySaved) return

        selectedStudents
            .removeAll { it == studentWithSemester }
            .let { if (!it) selectedStudents.add(studentWithSemester) }

        view?.enableSignIn(selectedStudents.isNotEmpty())
    }

    private fun compareStudents(a: Student, b: Student): Boolean {
        return a.email == b.email
            && a.symbol == b.symbol
            && a.studentId == b.studentId
            && a.schoolSymbol == b.schoolSymbol
            && a.classId == b.classId
    }

    private fun loadData(studentsWithSemesters: List<StudentWithSemesters>) {
        resetSelectedState()
        this.students = studentsWithSemesters

        flowWithResource { studentRepository.getSavedStudents(false) }.onEach {
            when (it.status) {
                Status.LOADING -> Timber.d("Login student select students load started")
                Status.SUCCESS -> view?.updateData(studentsWithSemesters.map { studentWithSemesters ->
                    studentWithSemesters to it.data!!.any { item -> compareStudents(studentWithSemesters.student, item.student) }
                })
                Status.ERROR -> {
                    errorHandler.dispatch(it.error!!)
                    lastError = it.error
                    view?.updateData(studentsWithSemesters.map { student -> student to false })
                }
            }
        }.launch()
    }

    private fun resetSelectedState() {
        selectedStudents.clear()
        view?.enableSignIn(false)
    }

    private fun registerStudents(studentsWithSemesters: List<StudentWithSemesters>) {
        flowWithResource {
            val savedStudents = studentRepository.saveStudents(studentsWithSemesters)
            val firstRegistered = studentsWithSemesters.first().apply { student.id = savedStudents.first() }
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
                    logRegisterEvent(studentsWithSemesters)
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
                    logRegisterEvent(studentsWithSemesters, it.error)
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

    private fun logRegisterEvent(studentsWithSemesters: List<StudentWithSemesters>, error: Throwable? = null) {
        studentsWithSemesters.forEach { student ->
            analytics.logEvent(
                "registration_student_select",
                "success" to (error != null),
                "scrapperBaseUrl" to student.student.scrapperBaseUrl,
                "symbol" to student.student.symbol,
                "error" to (error?.message?.ifBlank { "No message" } ?: "No error"))
        }
    }
}
