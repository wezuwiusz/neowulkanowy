package io.github.wulkanowy.ui.modules.account

import io.github.wulkanowy.data.Status
import io.github.wulkanowy.data.db.entities.StudentWithSemesters
import io.github.wulkanowy.data.repositories.StudentRepository
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
) : BasePresenter<AccountView>(errorHandler, studentRepository) {

    private lateinit var lastError: Throwable

    override fun onAttachView(view: AccountView) {
        super.onAttachView(view)
        view.initView()
        Timber.i("Account view was initialized")
        errorHandler.showErrorMessage = ::showErrorViewOnError
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

    fun onAddSelected() {
        Timber.i("Select add account")
        view?.openLoginView()
    }

    fun onItemSelected(studentWithSemesters: StudentWithSemesters) {
        view?.openAccountDetailsView(studentWithSemesters)
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

    private fun loadData() {
        flowWithResource { studentRepository.getSavedStudents() }
            .onEach {
                when (it.status) {
                    Status.LOADING -> Timber.i("Loading account data started")
                    Status.SUCCESS -> {
                        Timber.i("Loading account result: Success")
                        view?.updateData(createAccountItems(it.data!!))
                        view?.run {
                            showContent(true)
                            showErrorView(false)
                        }
                    }
                    Status.ERROR -> {
                        Timber.i("Loading account result: An exception occurred")
                        errorHandler.dispatch(it.error!!)
                    }
                }
            }
            .afterLoading { view?.showProgress(false) }
            .launch()
    }

    private fun showErrorViewOnError(message: String, error: Throwable) {
        view?.run {
            if (isViewEmpty) {
                lastError = error
                setErrorDetails(message)
                showErrorView(true)
                showContent(false)
                showProgress(false)
            } else showError(message, error)
        }
    }
}
