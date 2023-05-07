package io.github.wulkanowy.ui.modules.mobiledevice

import io.github.wulkanowy.data.*
import io.github.wulkanowy.data.db.entities.MobileDevice
import io.github.wulkanowy.data.repositories.MobileDeviceRepository
import io.github.wulkanowy.data.repositories.SemesterRepository
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.utils.AnalyticsHelper
import timber.log.Timber
import javax.inject.Inject

class MobileDevicePresenter @Inject constructor(
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository,
    private val semesterRepository: SemesterRepository,
    private val mobileDeviceRepository: MobileDeviceRepository,
    private val analytics: AnalyticsHelper
) : BasePresenter<MobileDeviceView>(errorHandler, studentRepository) {

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

        flatResourceFlow {
            val student = studentRepository.getCurrentStudent()
            val semester = semesterRepository.getCurrentSemester(student)
            mobileDeviceRepository.getDevices(student, semester, forceRefresh)
        }
            .logResourceStatus("load mobile devices data")
            .onResourceData {
                view?.run {
                    enableSwipe(true)
                    showProgress(false)
                    showErrorView(false)
                    showContent(it.isNotEmpty())
                    showEmpty(it.isEmpty())
                    updateData(it)
                }
            }
            .onResourceIntermediate { view?.showRefresh(true) }
            .onResourceSuccess {
                analytics.logEvent(
                    "load_data",
                    "type" to "devices",
                    "items" to it.size
                )
            }
            .onResourceNotLoading {
                view?.run {
                    enableSwipe(true)
                    showProgress(false)
                    showRefresh(false)
                }
            }
            .onResourceError(errorHandler::dispatch)
            .launch()
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
            deleteItem(device, position)
            showUndo(device, position)
            showEmpty(isViewEmpty)
        }
    }

    fun onUnregisterCancelled(device: MobileDevice, position: Int) {
        view?.run {
            restoreDeleteItem(device, position)
            showEmpty(isViewEmpty)
        }
    }

    fun onUnregisterConfirmed(device: MobileDevice) {
        resourceFlow {
            val student = studentRepository.getCurrentStudent()
            val semester = semesterRepository.getCurrentSemester(student)
            mobileDeviceRepository.unregisterDevice(student, semester, device)
        }
            .logResourceStatus("unregister device")
            .onResourceSuccess {
                view?.run {
                    showProgress(false)
                    enableSwipe(true)
                }
            }
            .onResourceError(errorHandler::dispatch)
            .launch("unregister")
    }

    fun onFragmentReselected() {
        if (view?.isViewEmpty == false) {
            view?.resetView()
        }
    }
}
