package io.github.wulkanowy.ui.modules.account.accountdetails

import io.github.wulkanowy.data.*
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.db.entities.StudentWithSemesters
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.services.sync.SyncManager
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.ui.modules.studentinfo.StudentInfoView
import timber.log.Timber
import javax.inject.Inject

class AccountDetailsPresenter @Inject constructor(
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository,
    private val syncManager: SyncManager
) : BasePresenter<AccountDetailsView>(errorHandler, studentRepository) {

    private var studentWithSemesters: StudentWithSemesters? = null

    private lateinit var lastError: Throwable

    private var studentId: Long? = null

    fun onAttachView(view: AccountDetailsView, student: Student) {
        super.onAttachView(view)
        studentId = student.id

        view.initView()
        errorHandler.showErrorMessage = ::showErrorViewOnError
        Timber.i("Account details view was initialized")
        loadData()
    }

    fun onRetry() {
        view?.run {
            showErrorView(false)
            showProgress(true)
        }
        loadData()
    }

    fun onDetailsClick() {
        view?.showErrorDetailsDialog(lastError)
    }

    private fun loadData() {
        resourceFlow { studentRepository.getSavedStudentById(studentId ?: -1) }
            .logResourceStatus("loading account details view")
            .onResourceLoading {
                view?.run {
                    showProgress(true)
                    showContent(false)
                }
            }
            .onResourceSuccess {
                studentWithSemesters = it
                view?.run {
                    showAccountData(studentWithSemesters!!.student)
                    enableSelectStudentButton(!studentWithSemesters!!.student.isCurrent)
                    showContent(true)
                    showErrorView(false)
                }
            }
            .onResourceNotLoading { view?.showProgress(false) }
            .onResourceError(errorHandler::dispatch)
            .launch()
    }

    fun onAccountEditSelected() {
        studentWithSemesters?.let {
            view?.showAccountEditDetailsDialog(it.student)
        }
    }

    fun onStudentInfoSelected(infoType: StudentInfoView.Type) {
        studentWithSemesters?.let {
            view?.openStudentInfoView(infoType, it)
        }
    }

    fun onStudentSelect() {
        if (studentWithSemesters == null) return

        Timber.i("Select student ${studentWithSemesters!!.student.id}")

        resourceFlow { studentRepository.switchStudent(studentWithSemesters!!) }
            .logResourceStatus("change student")
            .onResourceSuccess { view?.recreateMainView() }
            .onResourceNotLoading { view?.popViewToMain() }
            .onResourceError(errorHandler::dispatch)
            .launch("switch")
    }

    fun onRemoveSelected() {
        Timber.i("Select remove account")
        view?.showLogoutConfirmDialog()
    }

    fun onLogoutConfirm() {
        if (studentWithSemesters == null) return

        resourceFlow {
            val studentToLogout = studentWithSemesters!!.student

            studentRepository.logoutStudent(studentToLogout)
            val students = studentRepository.getSavedStudents(false)

            if (studentToLogout.isCurrent && students.isNotEmpty()) {
                studentRepository.switchStudent(students[0])
            }

            students
        }
            .logResourceStatus("logout user")
            .onResourceSuccess {
                view?.run {
                    when {
                        it.isEmpty() -> {
                            Timber.i("Logout result: Open login view")
                            syncManager.stopSyncWorker()
                            openClearLoginView()
                        }
                        studentWithSemesters?.student?.isCurrent == true -> {
                            Timber.i("Logout result: Logout student and switch to another")
                            recreateMainView()
                        }
                        else -> {
                            Timber.i("Logout result: Logout student")
                            recreateMainView()
                        }
                    }
                }
            }
            .onResourceNotLoading {
                if (studentWithSemesters?.student?.isCurrent == true) {
                    view?.popViewToMain()
                } else {
                    view?.popViewToAccounts()
                }
            }
            .onResourceError(errorHandler::dispatch)
            .launch("logout")
    }

    private fun showErrorViewOnError(message: String, error: Throwable) {
        view?.run {
            lastError = error
            setErrorDetails(message)
            showErrorView(true)
            showContent(false)
            showProgress(false)
        }
    }
}
