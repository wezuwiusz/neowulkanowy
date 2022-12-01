package io.github.wulkanowy.ui.modules.login.studentselect

import io.github.wulkanowy.data.Resource
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.db.entities.StudentWithSemesters
import io.github.wulkanowy.data.logResourceStatus
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.data.resourceFlow
import io.github.wulkanowy.services.sync.SyncManager
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.modules.login.LoginErrorHandler
import io.github.wulkanowy.utils.AnalyticsHelper
import io.github.wulkanowy.utils.ifNullOrBlank
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import javax.inject.Inject

class LoginStudentSelectPresenter @Inject constructor(
    studentRepository: StudentRepository,
    private val loginErrorHandler: LoginErrorHandler,
    private val syncManager: SyncManager,
    private val analytics: AnalyticsHelper
) : BasePresenter<LoginStudentSelectView>(loginErrorHandler, studentRepository) {

    private var lastError: Throwable? = null

    private val selectedStudents = mutableListOf<StudentWithSemesters>()

    fun onAttachView(view: LoginStudentSelectView, students: List<StudentWithSemesters>) {
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

        if (students.size == 1) registerStudents(students)
        loadData(students)
    }

    fun onSignIn() {
        registerStudents(selectedStudents)
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

        resourceFlow { studentRepository.getSavedStudents(false) }.onEach {
            when (it) {
                is Resource.Loading -> Timber.d("Login student select students load started")
                is Resource.Success -> view?.updateData(studentsWithSemesters.map { studentWithSemesters ->
                    studentWithSemesters to it.data.any { item ->
                        compareStudents(studentWithSemesters.student, item.student)
                    }
                })
                is Resource.Error -> {
                    errorHandler.dispatch(it.error)
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
        resourceFlow { studentRepository.saveStudents(studentsWithSemesters) }
            .logResourceStatus("registration")
            .onEach {
                when (it) {
                    is Resource.Loading -> view?.run {
                        showProgress(true)
                        showContent(false)
                    }
                    is Resource.Success -> {
                        syncManager.startOneTimeSyncWorker(quiet = true)
                        view?.navigateToNext()
                        logRegisterEvent(studentsWithSemesters)
                    }
                    is Resource.Error -> {
                        view?.apply {
                            showProgress(false)
                            showContent(true)
                            showContact(true)
                        }
                        lastError = it.error
                        loginErrorHandler.dispatch(it.error)
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

    private fun logRegisterEvent(
        studentsWithSemesters: List<StudentWithSemesters>,
        error: Throwable? = null
    ) {
        studentsWithSemesters.forEach { student ->
            analytics.logEvent(
                "registration_student_select",
                "success" to (error != null),
                "scrapperBaseUrl" to student.student.scrapperBaseUrl,
                "symbol" to student.student.symbol,
                "error" to (error?.message?.ifBlank { "No message" } ?: "No error")
            )
        }
    }
}
