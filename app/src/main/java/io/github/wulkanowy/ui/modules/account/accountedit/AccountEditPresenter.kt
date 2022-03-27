package io.github.wulkanowy.ui.modules.account.accountedit

import io.github.wulkanowy.data.*
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.db.entities.StudentNickAndAvatar
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.utils.AppInfo
import timber.log.Timber
import javax.inject.Inject

class AccountEditPresenter @Inject constructor(
    appInfo: AppInfo,
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository
) : BasePresenter<AccountEditView>(errorHandler, studentRepository) {

    lateinit var student: Student

    private val colors = appInfo.defaultColorsForAvatar.map { it.toInt() }

    fun onAttachView(view: AccountEditView, student: Student) {
        super.onAttachView(view)
        this.student = student

        with(view) {
            initView()
            showCurrentNick(student.nick.trim())
        }
        Timber.i("Account edit dialog view was initialized")
        loadData()

        view.updateColorsData(colors)
    }

    private fun loadData() {
        resourceFlow { studentRepository.getStudentById(student.id, false).avatarColor }
            .logResourceStatus("load student")
            .onResourceSuccess { view?.updateSelectedColorData(it.toInt()) }
            .onResourceError(errorHandler::dispatch)
            .launch("load_data")
    }

    fun changeStudentNickAndAvatar(nick: String, avatarColor: Int) {
        resourceFlow {
            val studentNick = StudentNickAndAvatar(
                nick = nick.trim(),
                avatarColor = avatarColor.toLong()
            ).apply { id = student.id }

            studentRepository.updateStudentNickAndAvatar(studentNick)
        }
            .logResourceStatus("change student nick and avatar")
            .onResourceSuccess { view?.recreateMainView() }
            .onResourceNotLoading { view?.popView() }
            .onResourceError(errorHandler::dispatch)
            .launch("update_student")
    }
}
