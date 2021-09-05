package io.github.wulkanowy.ui.modules.studentinfo

import io.github.wulkanowy.data.Status
import io.github.wulkanowy.data.db.entities.StudentInfo
import io.github.wulkanowy.data.db.entities.StudentWithSemesters
import io.github.wulkanowy.data.repositories.StudentInfoRepository
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.utils.AnalyticsHelper
import io.github.wulkanowy.utils.afterLoading
import io.github.wulkanowy.utils.flowWithResourceIn
import io.github.wulkanowy.utils.getCurrentOrLast
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import javax.inject.Inject

class StudentInfoPresenter @Inject constructor(
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository,
    private val studentInfoRepository: StudentInfoRepository,
    private val analytics: AnalyticsHelper
) : BasePresenter<StudentInfoView>(errorHandler, studentRepository) {

    private lateinit var infoType: StudentInfoView.Type

    private lateinit var studentWithSemesters: StudentWithSemesters

    private lateinit var lastError: Throwable

    fun onAttachView(
        view: StudentInfoView,
        type: StudentInfoView.Type,
        studentWithSemesters: StudentWithSemesters
    ) {
        super.onAttachView(view)
        infoType = type
        this.studentWithSemesters = studentWithSemesters
        view.initView()
        Timber.i("Student info $infoType view was initialized")
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

    fun onItemSelected(viewType: StudentInfoView.Type?) {
        viewType ?: return

        view?.openStudentInfoView(
            studentWithSemesters = studentWithSemesters,
            infoType = viewType,
        )
    }

    fun onItemLongClick(text: String) {
        view?.copyToClipboard(text)
    }

    private fun loadData(forceRefresh: Boolean = false) {
        flowWithResourceIn {
            val semester = studentWithSemesters.semesters.getCurrentOrLast()
            studentInfoRepository.getStudentInfo(
                student = studentWithSemesters.student,
                semester = semester,
                forceRefresh = forceRefresh
            )
        }.onEach {
            when (it.status) {
                Status.LOADING -> Timber.i("Loading student info $infoType started")
                Status.SUCCESS -> {
                    val isFamily = infoType == StudentInfoView.Type.FAMILY
                    val isFirstGuardianEmpty = it.data?.firstGuardian == null
                    val isSecondGuardianEmpty = it.data?.secondGuardian == null

                    if (it.data != null && !(isFamily && isFirstGuardianEmpty && isSecondGuardianEmpty)) {
                        Timber.i("Loading student info $infoType result: Success")
                        showCorrectData(it.data)
                        view?.run {
                            showContent(true)
                            showEmpty(false)
                            showErrorView(false)
                        }
                        analytics.logEvent("load_item", "type" to "student_info")
                    } else {
                        Timber.i("Loading student info $infoType result: No student or family info found")
                        view?.run {
                            showContent(!isViewEmpty)
                            showEmpty(isViewEmpty)
                            showErrorView(false)
                        }
                    }
                }
                Status.ERROR -> {
                    Timber.i("Loading student info $infoType result: An exception occurred")
                    errorHandler.dispatch(it.error!!)
                }
            }
        }.afterLoading {
            view?.run {
                hideRefresh()
                showProgress(false)
                enableSwipe(true)
            }
        }.launch()
    }

    private fun showCorrectData(studentInfo: StudentInfo) {
        when (infoType) {
            StudentInfoView.Type.PERSONAL -> view?.showPersonalTypeData(studentInfo)
            StudentInfoView.Type.CONTACT -> view?.showContactTypeData(studentInfo)
            StudentInfoView.Type.ADDRESS -> view?.showAddressTypeData(studentInfo)
            StudentInfoView.Type.FAMILY -> view?.showFamilyTypeData(studentInfo)
            StudentInfoView.Type.SECOND_GUARDIAN -> view?.showGuardianTypeData(studentInfo.secondGuardian!!)
            StudentInfoView.Type.FIRST_GUARDIAN -> view?.showGuardianTypeData(studentInfo.firstGuardian!!)
        }
    }

    private fun showErrorViewOnError(message: String, error: Throwable) {
        view?.run {
            if (isViewEmpty) {
                lastError = error
                setErrorDetails(message)
                showErrorView(true)
                showEmpty(false)
                showContent(false)
                showProgress(false)
            } else showError(message, error)
        }
    }
}
