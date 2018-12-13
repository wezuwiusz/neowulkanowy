package io.github.wulkanowy.ui.modules.splash

import io.github.wulkanowy.data.ErrorHandler
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import javax.inject.Inject

class SplashPresenter @Inject constructor(
    private val studentRepository: StudentRepository,
    errorHandler: ErrorHandler
) : BasePresenter<SplashView>(errorHandler) {

    override fun onAttachView(view: SplashView) {
        super.onAttachView(view)
        view.run {
            if (studentRepository.isStudentSaved) openMainView()
            else openLoginView()
        }
    }
}
