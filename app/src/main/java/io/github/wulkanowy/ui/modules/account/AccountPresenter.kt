package io.github.wulkanowy.ui.modules.account

import io.github.wulkanowy.data.Status
import io.github.wulkanowy.data.db.entities.StudentWithSemesters
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.utils.flowWithResource
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import javax.inject.Inject

class AccountPresenter @Inject constructor(
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository,
) : BasePresenter<AccountView>(errorHandler, studentRepository) {

    override fun onAttachView(view: AccountView) {
        super.onAttachView(view)
        view.initView()
        Timber.i("Account view was initialized")
        loadData()
    }

    fun onAddSelected() {
        Timber.i("Select add account")
        view?.openLoginView()
    }

    fun onItemSelected(studentWithSemesters: StudentWithSemesters) {
        view?.openAccountDetailsView(studentWithSemesters)
    }

    private fun loadData() {
        flowWithResource { studentRepository.getSavedStudents(false) }
            .onEach {
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
            }
            .launch("load")
    }

    private fun createAccountItems(items: List<StudentWithSemesters>): List<AccountItem<*>> {
        return items.groupBy {
            Account("${it.student.userName} (${it.student.email})", it.student.isParent)
        }
            .map { (account, students) ->
                listOf(
                    AccountItem(account, AccountItem.ViewType.HEADER)
                ) + students.map { student ->
                    AccountItem(student, AccountItem.ViewType.ITEM)
                }
            }
            .flatten()
    }
}
