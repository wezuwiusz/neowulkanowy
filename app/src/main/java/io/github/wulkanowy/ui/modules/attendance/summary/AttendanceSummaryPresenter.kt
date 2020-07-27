package io.github.wulkanowy.ui.modules.attendance.summary

import io.github.wulkanowy.data.Status
import io.github.wulkanowy.data.db.entities.Subject
import io.github.wulkanowy.data.repositories.attendancesummary.AttendanceSummaryRepository
import io.github.wulkanowy.data.repositories.semester.SemesterRepository
import io.github.wulkanowy.data.repositories.student.StudentRepository
import io.github.wulkanowy.data.repositories.subject.SubjectRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.utils.FirebaseAnalyticsHelper
import io.github.wulkanowy.utils.afterLoading
import io.github.wulkanowy.utils.flowWithResourceIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import java.time.Month
import javax.inject.Inject

class AttendanceSummaryPresenter @Inject constructor(
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository,
    private val attendanceSummaryRepository: AttendanceSummaryRepository,
    private val subjectRepository: SubjectRepository,
    private val semesterRepository: SemesterRepository,
    private val analytics: FirebaseAnalyticsHelper
) : BasePresenter<AttendanceSummaryView>(errorHandler, studentRepository) {

    private var subjects = emptyList<Subject>()

    var currentSubjectId = -1
        private set

    private lateinit var lastError: Throwable

    fun onAttachView(view: AttendanceSummaryView, subjectId: Int?) {
        super.onAttachView(view)
        view.initView()
        Timber.i("Attendance summary view was initialized with subject id ${subjectId ?: -1}")
        errorHandler.showErrorMessage = ::showErrorViewOnError
        loadData(subjectId ?: -1)
        loadSubjects()
    }

    fun onSwipeRefresh() {
        Timber.i("Force refreshing the attendance summary")
        loadData(currentSubjectId, true)
    }

    fun onRetry() {
        view?.run {
            showErrorView(false)
            showProgress(true)
        }
        loadData(currentSubjectId, true)
    }

    fun onDetailsClick() {
        view?.showErrorDetailsDialog(lastError)
    }

    fun onSubjectSelected(name: String?) {
        Timber.i("Select attendance summary subject $name")
        view?.run {
            showContent(false)
            showProgress(true)
            enableSwipe(false)
            showEmpty(false)
            showErrorView(false)
            clearView()
        }
        (subjects.singleOrNull { it.name == name }?.realId ?: -1).let {
            if (it != currentSubjectId) loadData(it)
        }
    }

    private fun loadData(subjectId: Int, forceRefresh: Boolean = false) {
        currentSubjectId = subjectId

        flowWithResourceIn {
            val student = studentRepository.getCurrentStudent()
            val semester = semesterRepository.getCurrentSemester(student)
            attendanceSummaryRepository.getAttendanceSummary(student, semester, subjectId, forceRefresh)
        }.onEach {
            when (it.status) {
                Status.LOADING -> Timber.i("Loading attendance summary data started")
                Status.SUCCESS -> {
                    Timber.i("Loading attendance summary result: Success")
                    view?.apply {
                        showEmpty(it.data!!.isEmpty())
                        showContent(it.data.isNotEmpty())
                        updateDataSet(it.data.sortedByDescending { item ->
                            if (item.month.value <= Month.JUNE.value) item.month.value + 12 else item.month.value
                        })
                    }
                    analytics.logEvent(
                        "load_data",
                        "type" to "attendance_summary",
                        "items" to it.data!!.size,
                        "item_id" to subjectId
                    )
                }
                Status.ERROR -> {
                    Timber.i("Loading attendance summary result: An exception occurred")
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

    private fun loadSubjects() {
        flowWithResourceIn {
            val student = studentRepository.getCurrentStudent()
            val semester = semesterRepository.getCurrentSemester(student)
            subjectRepository.getSubjects(student, semester)
        }.onEach {
            when (it.status) {
                Status.LOADING -> Timber.i("Loading attendance summary subjects started")
                Status.SUCCESS -> {
                    subjects = it.data!!

                    Timber.i("Loading attendance summary subjects result: Success")
                    view?.run {
                        view?.updateSubjects(ArrayList(it.data.map { subject -> subject.name }))
                        showSubjects(true)
                    }
                }
                Status.ERROR -> {
                    Timber.i("Loading attendance summary subjects result: An exception occurred")
                    errorHandler.dispatch(it.error!!)
                }
            }
        }.launch("subjects")
    }
}
