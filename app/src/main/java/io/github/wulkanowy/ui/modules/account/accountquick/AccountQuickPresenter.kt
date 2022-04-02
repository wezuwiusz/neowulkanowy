package io.github.wulkanowy.ui.modules.account.accountquick

import io.github.wulkanowy.data.*
import io.github.wulkanowy.data.db.entities.StudentWithSemesters
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.ui.modules.account.AccountItem
import timber.log.Timber
import javax.inject.Inject

class AccountQuickPresenter @Inject constructor(
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository
) : BasePresenter<AccountQuickView>(errorHandler, studentRepository) {

    private lateinit var studentsWithSemesters: List<StudentWithSemesters>

    fun onAttachView(view: AccountQuickView, studentsWithSemesters: List<StudentWithSemesters>) {
        super.onAttachView(view)
        this.studentsWithSemesters = studentsWithSemesters

        view.initView()
        Timber.i("Account quick dialog view was initialized")
        view.updateData(createAccountItems(studentsWithSemesters))
    }

    fun onManagerSelected() {
        view?.run {
            openAccountView()
            popView()
        }
    }

    fun onStudentSelect(studentWithSemesters: StudentWithSemesters) {
        Timber.i("Select student ${studentWithSemesters.student.id}")

        if (studentWithSemesters.student.isCurrent) {
            view?.popView()
            return
        }

        resourceFlow { studentRepository.switchStudent(studentWithSemesters) }
            .logResourceStatus("change student")
            .onResourceSuccess { view?.recreateMainView() }
            .onResourceNotLoading { view?.popView() }
            .onResourceError(errorHandler::dispatch)
            .launch("switch")
    }

    private fun createAccountItems(items: List<StudentWithSemesters>) = items.map {
        AccountItem(it, AccountItem.ViewType.ITEM)
    }
}
