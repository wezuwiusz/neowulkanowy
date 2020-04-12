package io.github.wulkanowy.ui.modules.mobiledevice

import io.github.wulkanowy.data.db.entities.MobileDevice
import io.github.wulkanowy.data.repositories.mobiledevice.MobileDeviceRepository
import io.github.wulkanowy.data.repositories.semester.SemesterRepository
import io.github.wulkanowy.data.repositories.student.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.utils.FirebaseAnalyticsHelper
import io.github.wulkanowy.utils.SchedulersProvider
import timber.log.Timber
import javax.inject.Inject

class MobileDevicePresenter @Inject constructor(
    schedulers: SchedulersProvider,
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository,
    private val semesterRepository: SemesterRepository,
    private val mobileDeviceRepository: MobileDeviceRepository,
    private val analytics: FirebaseAnalyticsHelper
) : BasePresenter<MobileDeviceView>(errorHandler, studentRepository, schedulers) {

    private lateinit var lastError: Throwable

    override fun onAttachView(view: MobileDeviceView) {
        super.onAttachView(view)
        view.initView()
        Timber.i("Mobile device view was initialized")
        errorHandler.showErrorMessage = ::showErrorViewOnError
        loadData()
    }

    fun onSwipeRefresh() {
        loadData(true)
    }

    fun onRetry() {
        view?.run {
            showErrorView(false)
            showProgress(true)
        }
        loadData(true)
    }

    fun onDetailsClick() {
        view?.showErrorDetailsDialog(lastError)
    }

    private fun loadData(forceRefresh: Boolean = false) {
        Timber.i("Loading mobile devices data started")
        disposable.add(studentRepository.getCurrentStudent()
            .flatMap { student ->
                semesterRepository.getCurrentSemester(student).flatMap { semester ->
                    mobileDeviceRepository.getDevices(student, semester, forceRefresh)
                }
            }
            .map { items -> items.map { MobileDeviceItem(it) } }
            .subscribeOn(schedulers.backgroundThread)
            .observeOn(schedulers.mainThread)
            .doFinally {
                view?.run {
                    hideRefresh()
                    showProgress(false)
                    enableSwipe(true)
                }
            }.subscribe({
                Timber.i("Loading mobile devices result: Success")
                view?.run {
                    updateData(it)
                    showContent(it.isNotEmpty())
                    showEmpty(it.isEmpty())
                    showErrorView(false)
                }
                analytics.logEvent("load_devices", "items" to it.size, "force_refresh" to forceRefresh)
            }) {
                Timber.i("Loading mobile devices result: An exception occurred")
                errorHandler.dispatch(it)
            })
    }

    private fun showErrorViewOnError(message: String, error: Throwable) {
        view?.run {
            if (isViewEmpty) {
                lastError = error
                setErrorDetails(message)
                showErrorView(true)
                showEmpty(false)
            } else showError(message, error)
        }
    }

    fun onRegisterDevice() {
        view?.showTokenDialog()
    }

    fun onUnregisterDevice(device: MobileDevice, position: Int) {
        view?.run {
            showUndo(position, device)
            showEmpty(isViewEmpty)
        }
    }

    fun onUnregisterCancelled() {
        view?.run {
            restoreDeleteItem()
            showEmpty(isViewEmpty)
        }
    }

    fun onUnregisterConfirmed(device: MobileDevice) {
        Timber.i("Unregister device started")
        disposable.add(studentRepository.getCurrentStudent()
            .flatMap { student ->
                semesterRepository.getCurrentSemester(student).flatMap { semester ->
                    mobileDeviceRepository.unregisterDevice(student, semester, device)
                        .flatMap { mobileDeviceRepository.getDevices(student, semester, it) }
                }
            }
            .map { items -> items.map { MobileDeviceItem(it) } }
            .subscribeOn(schedulers.backgroundThread)
            .observeOn(schedulers.mainThread)
            .doFinally {
                view?.run {
                    showProgress(false)
                    enableSwipe(true)
                }
            }
            .subscribe({
                Timber.i("Unregister device result: Success")
                view?.run {
                    updateData(it)
                    showContent(it.isNotEmpty())
                    showEmpty(it.isEmpty())
                }
            }) {
                Timber.i("Unregister device result: An exception occurred")
                errorHandler.dispatch(it)
            }
        )
    }
}
