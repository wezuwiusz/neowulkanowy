package io.github.wulkanowy.ui.modules.account

import io.github.wulkanowy.data.Status
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.repositories.student.StudentRepository
import io.github.wulkanowy.services.sync.SyncManager
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.utils.afterLoading
import io.github.wulkanowy.utils.flowWithResource
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import javax.inject.Inject

class AccountPresenter @Inject constructor(
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository,
    private val syncManager: SyncManager
) : BasePresenter<AccountView>(errorHandler, studentRepository) {

    override fun onAttachView(view: AccountView) {
        super.onAttachView(view)
        view.initView()
        Timber.i("Account dialog view was initialized")
        loadData()
    }

    fun onAddSelected() {
        Timber.i("Select add account")
        view?.openLoginView()
    }

    fun onRemoveSelected() {
        Timber.i("Select remove account")
        view?.showConfirmDialog()
    }

    fun onLogoutConfirm() {
        flowWithResource {
            val student = studentRepository.getCurrentStudent(false)
            studentRepository.logoutStudent(student)

            val students = studentRepository.getSavedStudents(false)
            if (students.isNotEmpty()) {
                studentRepository.switchStudent(students[0])
            }
            students
        }.onEach {
            when (it.status) {
                Status.LOADING -> Timber.i("Attempt to logout current user ")
                Status.SUCCESS -> view?.run {
                    if (it.data!!.isEmpty()) {
                        Timber.i("Logout result: Open login view")
                        syncManager.stopSyncWorker()
                        openClearLoginView()
                    } else {
                        Timber.i("Logout result: Switch to another student")
                        recreateMainView()
                    }
                }
                Status.ERROR -> {
                    Timber.i("Logout result: An exception occurred")
                    errorHandler.dispatch(it.error!!)
                }
            }
        }.afterLoading {
            view?.dismissView()
        }.launch("logout")
    }

    fun onItemSelected(student: Student) {
        Timber.i("Select student item ${student.id}")
        if (student.isCurrent) {
            view?.dismissView()
        } else flowWithResource { studentRepository.switchStudent(student) }.onEach {
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
            view?.dismissView()
        }.launch("switch")
    }

    private fun createAccountItems(items: List<Student>): List<AccountItem<*>> {
        return items.groupBy { Account(it.email, it.isParent) }.map { (account, students) ->
            listOf(AccountItem(account, AccountItem.ViewType.HEADER)) + students.map { student ->
                AccountItem(student, AccountItem.ViewType.ITEM)
            }
        }.flatten()
    }

    private fun loadData() {
        flowWithResource { studentRepository.getSavedStudents(false) }.onEach {
            when (it.status) {
                Status.LOADING -> Timber.i("Loading account data started")
                Status.SUCCESS -> {
                    Timber.i("Loading account result: Success")
                    view?.updateData(createAccountItems(it.data!!))
                }
                Status.ERROR -> {
                    Timber.i("Loading account result: An exception occurred")
                    errorHandler.dispatch(it.error!!)
                }
            }
        }.launch()
    }
}
