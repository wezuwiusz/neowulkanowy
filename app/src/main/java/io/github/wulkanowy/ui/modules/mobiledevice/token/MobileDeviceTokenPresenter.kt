package io.github.wulkanowy.ui.modules.mobiledevice.token

import io.github.wulkanowy.data.repositories.mobiledevice.MobileDeviceRepository
import io.github.wulkanowy.data.repositories.semester.SemesterRepository
import io.github.wulkanowy.data.repositories.student.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.utils.FirebaseAnalyticsHelper
import io.github.wulkanowy.utils.SchedulersProvider
import timber.log.Timber
import javax.inject.Inject

class MobileDeviceTokenPresenter @Inject constructor(
    schedulers: SchedulersProvider,
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository,
    private val semesterRepository: SemesterRepository,
    private val mobileDeviceRepository: MobileDeviceRepository,
    private val analytics: FirebaseAnalyticsHelper
) : BasePresenter<MobileDeviceTokenVIew>(errorHandler, studentRepository, schedulers) {

    override fun onAttachView(view: MobileDeviceTokenVIew) {
        super.onAttachView(view)
        view.initView()
        Timber.i("Mobile device view was initialized")
        loadData()
    }

    private fun loadData() {
        Timber.i("Mobile device registration data started")
        disposable.add(studentRepository.getCurrentStudent()
            .flatMap { student ->
                semesterRepository.getCurrentSemester(student).flatMap { semester ->
                    mobileDeviceRepository.getToken(student, semester)
                }
            }
            .subscribeOn(schedulers.backgroundThread)
            .observeOn(schedulers.mainThread)
            .doFinally { view?.hideLoading() }
            .subscribe({
                Timber.i("Mobile device registration result: Success")
                view?.run {
                    updateData(it)
                    showContent()
                }
                analytics.logEvent("device_register", "symbol" to it.token.substring(0, 3))
            }) {
                Timber.i("Mobile device registration result: An exception occurred")
                view?.closeDialog()
                errorHandler.dispatch(it)
            }
        )
    }
}
