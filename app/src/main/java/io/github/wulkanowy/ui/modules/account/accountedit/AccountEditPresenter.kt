package io.github.wulkanowy.ui.modules.account.accountedit

import io.github.wulkanowy.data.Status
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.db.entities.StudentNickAndAvatar
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.utils.AppInfo
import io.github.wulkanowy.utils.afterLoading
import io.github.wulkanowy.utils.flowWithResource
import kotlinx.coroutines.flow.onEach
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
        flowWithResource {
            studentRepository.getStudentById(student.id, false).avatarColor
        }.onEach { resource ->
            when (resource.status) {
                Status.LOADING -> Timber.i("Attempt to load student")
                Status.SUCCESS -> {
                    view?.updateSelectedColorData(resource.data?.toInt()!!)
                    Timber.i("Attempt to load student: Success")
                }
                Status.ERROR -> {
                    Timber.i("Attempt to load student: An exception occurred")
                    errorHandler.dispatch(resource.error!!)
                }
            }
        }.launch("load_data")
    }

    fun changeStudentNickAndAvatar(nick: String, avatarColor: Int) {
        flowWithResource {
            val studentNick =
                StudentNickAndAvatar(nick = nick.trim(), avatarColor = avatarColor.toLong())
                    .apply { id = student.id }
            studentRepository.updateStudentNickAndAvatar(studentNick)
        }.onEach {
            when (it.status) {
                Status.LOADING -> Timber.i("Attempt to change a student nick and avatar")
                Status.SUCCESS -> {
                    Timber.i("Change a student nick and avatar result: Success")
                    view?.recreateMainView()
                }
                Status.ERROR -> {
                    Timber.i("Change a student nick and avatar result: An exception occurred")
                    errorHandler.dispatch(it.error!!)
                }
            }
        }
            .afterLoading { view?.popView() }
            .launch("update_student")
    }
}
