package io.github.wulkanowy.ui.modules.account.accountdetails

import io.github.wulkanowy.data.Status
import io.github.wulkanowy.data.db.entities.StudentWithSemesters
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.services.sync.SyncManager
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.ui.modules.studentinfo.StudentInfoView
import io.github.wulkanowy.utils.afterLoading
import io.github.wulkanowy.utils.flowWithResource
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import javax.inject.Inject

class AccountDetailsPresenter @Inject constructor(
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository,
    private val syncManager: SyncManager
) : BasePresenter<AccountDetailsView>(errorHandler, studentRepository) {

    lateinit var studentWithSemesters: StudentWithSemesters

    override fun onAttachView(view: AccountDetailsView) {
        super.onAttachView(view)
        view.initView()
        Timber.i("Account details view was initialized")

        view.showAccountData(studentWithSemesters)
    }

    fun onStudentInfoSelected(infoType: StudentInfoView.Type) {
        view?.openStudentInfoView(infoType, studentWithSemesters)
    }

    fun onStudentSelect() {
        Timber.i("Select student ${studentWithSemesters.student.id}")

        flowWithResource { studentRepository.switchStudent(studentWithSemesters) }
            .onEach {
                when (it.status) {
                    Status.LOADING -> Timber.i("Attempt to change a student")
                    Status.SUCCESS -> {
                        Timber.i("Change a student result: Success")
                        view?.recreateMainView()
                    }
                    Status.ERROR -> {
                        Timber.i("Change a student result: An exception occurred")
                        errorHandler.dispatch(it.error!!)
                    }
                }
            }.afterLoading {
                view?.popView()
            }.launch("switch")
    }

    fun onRemoveSelected() {
        Timber.i("Select remove account")
        view?.showLogoutConfirmDialog()
    }

    fun onLogoutConfirm() {
        flowWithResource {
            val studentToLogout = studentWithSemesters.student

            studentRepository.logoutStudent(studentToLogout)
            val students = studentRepository.getSavedStudents(false)

            if (studentToLogout.isCurrent && students.isNotEmpty()) {
                studentRepository.switchStudent(students[0])
            }

            return@flowWithResource students
        }.onEach {
            when (it.status) {
                Status.LOADING -> Timber.i("Attempt to logout user")
                Status.SUCCESS -> view?.run {
                    when {
                        it.data!!.isEmpty() -> {
                            Timber.i("Logout result: Open login view")
                            syncManager.stopSyncWorker()
                            openClearLoginView()
                        }
                        studentWithSemesters.student.isCurrent -> {
                            Timber.i("Logout result: Logout student and switch to another")
                            recreateMainView()
                        }
                        else -> Timber.i("Logout result: Logout student")
                    }
                }
                Status.ERROR -> {
                    Timber.i("Logout result: An exception occurred")
                    errorHandler.dispatch(it.error!!)
                }
            }
        }.afterLoading {
            view?.popView()
        }.launch("logout")
    }
}
