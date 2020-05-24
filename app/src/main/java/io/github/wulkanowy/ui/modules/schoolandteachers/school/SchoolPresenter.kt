package io.github.wulkanowy.ui.modules.schoolandteachers.school

import io.github.wulkanowy.data.repositories.school.SchoolRepository
import io.github.wulkanowy.data.repositories.semester.SemesterRepository
import io.github.wulkanowy.data.repositories.student.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.utils.FirebaseAnalyticsHelper
import io.github.wulkanowy.utils.SchedulersProvider
import timber.log.Timber
import javax.inject.Inject

class SchoolPresenter @Inject constructor(
    schedulers: SchedulersProvider,
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository,
    private val semesterRepository: SemesterRepository,
    private val schoolRepository: SchoolRepository,
    private val analytics: FirebaseAnalyticsHelper
) : BasePresenter<SchoolView>(errorHandler, studentRepository, schedulers) {

    private var address: String? = null

    private var contact: String? = null

    private lateinit var lastError: Throwable

    override fun onAttachView(view: SchoolView) {
        super.onAttachView(view)
        view.initView()
        Timber.i("School view was initialized")
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

    fun onParentViewLoadData(forceRefresh: Boolean) {
        loadData(forceRefresh)
    }

    fun onAddressSelected() {
        address?.let { view?.openMapsLocation(it) }
    }

    fun onTelephoneSelected() {
        contact?.let { view?.dialPhone(it) }
    }

    private fun loadData(forceRefresh: Boolean = false) {
        Timber.i("Loading school info started")
        disposable.add(studentRepository.getCurrentStudent()
            .flatMapMaybe { student ->
                semesterRepository.getCurrentSemester(student).flatMapMaybe {
                    schoolRepository.getSchoolInfo(student, it, forceRefresh)
                }
            }
            .subscribeOn(schedulers.backgroundThread)
            .observeOn(schedulers.mainThread)
            .doFinally {
                view?.run {
                    hideRefresh()
                    showProgress(false)
                    enableSwipe(true)
                    notifyParentDataLoaded()
                }
            }.subscribe({
                Timber.i("Loading teachers result: Success")
                view?.run {
                    address = it.address.ifBlank { null }
                    contact = it.contact.ifBlank { null }
                    updateData(it)
                    showContent(true)
                    showEmpty(false)
                    showErrorView(false)
                }
                analytics.logEvent(
                    "load_item",
                    "type" to "school",
                    "force_refresh" to forceRefresh
                )
            }, {
                Timber.i("Loading school result: An exception occurred")
                errorHandler.dispatch(it)
            }, {
                Timber.i("Loading school result: No school info found")
                view?.run {
                    showContent(!isViewEmpty)
                    showEmpty(isViewEmpty)
                    showErrorView(false)
                }
            }))
    }

    private fun showErrorViewOnError(message: String, error: Throwable) {
        view?.run {
            if (isViewEmpty) {
                lastError = error
                setErrorDetails(message)
                showErrorView(true)
                showEmpty(false)
                showContent(false)
            } else showError(message, error)
        }
    }
}
