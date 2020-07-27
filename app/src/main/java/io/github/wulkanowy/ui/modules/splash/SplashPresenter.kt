package io.github.wulkanowy.ui.modules.splash

import io.github.wulkanowy.data.Status
import io.github.wulkanowy.data.repositories.student.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.utils.flowWithResource
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import javax.inject.Inject

class SplashPresenter @Inject constructor(
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository
) : BasePresenter<SplashView>(errorHandler, studentRepository) {

    override fun onAttachView(view: SplashView) {
        super.onAttachView(view)
        flowWithResource { studentRepository.isCurrentStudentSet() }.onEach {
            when (it.status) {
                Status.LOADING -> Timber.d("Is current user set check started")
                Status.SUCCESS -> with(view) {
                    if (it.data!!) openMainView()
                    else openLoginView()
                }
                Status.ERROR -> errorHandler.dispatch(it.error!!)
            }
        }.launch()
    }
}
