package io.github.wulkanowy.ui.modules.account

import io.github.wulkanowy.data.db.entities.StudentWithSemesters
import io.github.wulkanowy.data.logResourceStatus
import io.github.wulkanowy.data.onResourceError
import io.github.wulkanowy.data.onResourceSuccess
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.data.resourceFlow
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
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
        view?.openAccountDetailsView(studentWithSemesters.student)
    }

    private fun loadData() {
        resourceFlow { studentRepository.getSavedStudents(false) }
            .logResourceStatus("load account data")
            .onResourceSuccess { view?.updateData(createAccountItems(it)) }
            .onResourceError(errorHandler::dispatch)
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
