package io.github.wulkanowy.ui.modules.account.accountedit

import io.github.wulkanowy.data.Status
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.db.entities.StudentNick
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.utils.afterLoading
import io.github.wulkanowy.utils.flowWithResource
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import javax.inject.Inject

class AccountEditPresenter @Inject constructor(
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository
) : BasePresenter<AccountEditView>(errorHandler, studentRepository) {

    lateinit var student: Student

    fun onAttachView(view: AccountEditView, student: Student) {
        super.onAttachView(view)
        this.student = student

        with(view) {
            initView()
            showCurrentNick(student.nick.trim())
        }
        Timber.i("Account edit dialog view was initialized")
    }

    fun changeStudentNick(nick: String) {
        flowWithResource {
            val studentNick =
                StudentNick(nick = nick.trim()).apply { id = student.id }
            studentRepository.updateStudentNick(studentNick)
        }.onEach {
            when (it.status) {
                Status.LOADING -> Timber.i("Attempt to change a student nick")
                Status.SUCCESS -> {
                    Timber.i("Change a student nick result: Success")
                    view?.recreateMainView()
                }
                Status.ERROR -> {
                    Timber.i("Change a student result: An exception occurred")
                    errorHandler.dispatch(it.error!!)
                }
            }
        }
            .afterLoading { view?.popView() }
            .launch()
    }
}
