package io.github.wulkanowy.ui.modules.login

import io.github.wulkanowy.data.db.entities.StudentWithSemesters
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import timber.log.Timber
import javax.inject.Inject

class LoginPresenter @Inject constructor(
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository
) : BasePresenter<LoginView>(errorHandler, studentRepository) {

    override fun onAttachView(view: LoginView) {
        super.onAttachView(view)
        with(view) {
            initView()
            showActionBar(false)
        }
        Timber.i("Login view was initialized")
    }

    fun onFormViewAccountLogged(studentsWithSemesters: List<StudentWithSemesters>, loginData: Triple<String, String, String>) {
        view?.apply {
            if (studentsWithSemesters.isEmpty()) {
                Timber.i("Switch to symbol form")
                notifyInitSymbolFragment(loginData)
                switchView(1)
            } else {
                Timber.i("Switch to student select")
                notifyInitStudentSelectFragment(studentsWithSemesters)
                switchView(2)
            }
        }
    }

    fun onSymbolViewAccountLogged(studentsWithSemesters: List<StudentWithSemesters>) {
        view?.apply {
            Timber.i("Switch to student select")
            notifyInitStudentSelectFragment(studentsWithSemesters)
            switchView(2)
        }
    }

    fun onAdvancedLoginClick() {
        view?.switchView(3)
    }

    fun onRecoverClick() {
        view?.switchView(4)
    }

    fun onViewSelected(index: Int) {
        view?.apply {
            when (index) {
                0 -> showActionBar(false)
                1, 2, 3, 4 -> showActionBar(true)
            }
        }
    }

    fun onBackPressed(default: () -> Unit) {
        Timber.i("Back pressed in login view")
        view?.apply {
            when (currentViewIndex) {
                1, 2, 3, 4 -> switchView(0)
                else -> default()
            }
        }
    }
}
