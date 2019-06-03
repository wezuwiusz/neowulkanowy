package io.github.wulkanowy.ui.modules.splash

import io.github.wulkanowy.data.repositories.student.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.utils.SchedulersProvider
import javax.inject.Inject

class SplashPresenter @Inject constructor(
    schedulers: SchedulersProvider,
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository
) : BasePresenter<SplashView>(errorHandler, studentRepository, schedulers) {

    override fun onAttachView(view: SplashView) {
        super.onAttachView(view)
        disposable.add(studentRepository.isCurrentStudentSet()
            .subscribeOn(schedulers.backgroundThread)
            .observeOn(schedulers.mainThread)
            .subscribe({
                view.apply {
                    if (it) openMainView()
                    else openLoginView()
                }
            }, { errorHandler.dispatch(it) }))
    }
}
