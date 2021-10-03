package io.github.wulkanowy.ui.modules.mobiledevice

import io.github.wulkanowy.data.Status
import io.github.wulkanowy.data.db.entities.MobileDevice
import io.github.wulkanowy.data.repositories.MobileDeviceRepository
import io.github.wulkanowy.data.repositories.SemesterRepository
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.utils.AnalyticsHelper
import io.github.wulkanowy.utils.afterLoading
import io.github.wulkanowy.utils.flowWithResource
import io.github.wulkanowy.utils.flowWithResourceIn
import kotlinx.coroutines.flow.onEach
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

        flowWithResourceIn {
            val student = studentRepository.getCurrentStudent()
            val semester = semesterRepository.getCurrentSemester(student)
            mobileDeviceRepository.getDevices(student, semester, forceRefresh)
        }.onEach {
            when (it.status) {
                Status.LOADING -> {
                    if (!it.data.isNullOrEmpty()) {
                        view?.run {
                            enableSwipe(true)
                            showRefresh(true)
                            showProgress(false)
                            showContent(true)
                            updateData(it.data)
                        }
                    }
                }
                Status.SUCCESS -> {
                    Timber.i("Loading mobile devices result: Success")
                    view?.run {
                        updateData(it.data!!)
                        showContent(it.data.isNotEmpty())
                        showEmpty(it.data.isEmpty())
                        showErrorView(false)
                    }
                    analytics.logEvent(
                        "load_data",
                        "type" to "devices",
                        "items" to it.data!!.size
                    )
                }
                Status.ERROR -> {
                    Timber.i("Loading mobile devices result: An exception occurred")
                    errorHandler.dispatch(it.error!!)
                }
            }
        }.afterLoading {
            view?.run {
                showRefresh(false)
                showProgress(false)
                enableSwipe(true)
            }
        }.launch()
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
        flowWithResource {
            val student = studentRepository.getCurrentStudent()
            val semester = semesterRepository.getCurrentSemester(student)
            mobileDeviceRepository.unregisterDevice(student, semester, device)
        }.onEach {
            when (it.status) {
                Status.LOADING -> Timber.i("Unregister device started")
                Status.SUCCESS -> {
                    Timber.i("Unregister device result: Success")
                    view?.run {
                        showProgress(false)
                        enableSwipe(true)
                    }
                }
                Status.ERROR -> {
                    Timber.i("Unregister device result: An exception occurred")
                    errorHandler.dispatch(it.error!!)
                }
            }
        }.launch("unregister")
    }
}
