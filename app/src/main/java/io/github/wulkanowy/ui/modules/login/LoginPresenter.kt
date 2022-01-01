package io.github.wulkanowy.ui.modules.login

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
        view.initView()
        Timber.i("Login view was initialized")
    }
}
