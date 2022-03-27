package io.github.wulkanowy.ui.modules.attendance.summary

import io.github.wulkanowy.data.*
import io.github.wulkanowy.data.db.entities.AttendanceSummary
import io.github.wulkanowy.data.db.entities.Subject
import io.github.wulkanowy.data.repositories.AttendanceSummaryRepository
import io.github.wulkanowy.data.repositories.SemesterRepository
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.data.repositories.SubjectRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.utils.AnalyticsHelper
import timber.log.Timber
import java.time.Month
import javax.inject.Inject

class AttendanceSummaryPresenter @Inject constructor(
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository,
    private val attendanceSummaryRepository: AttendanceSummaryRepository,
    private val subjectRepository: SubjectRepository,
    private val semesterRepository: SemesterRepository,
    private val analytics: AnalyticsHelper
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

        flatResourceFlow {
            val student = studentRepository.getCurrentStudent()
            val semester = semesterRepository.getCurrentSemester(student)

            attendanceSummaryRepository.getAttendanceSummary(
                student = student,
                semester = semester,
                subjectId = subjectId,
                forceRefresh = forceRefresh
            )
        }
            .logResourceStatus("load attendance summary")
            .mapResourceData(this::sortItems)
            .onResourceData {
                view?.run {
                    enableSwipe(true)
                    showProgress(false)
                    showErrorView(false)
                    showContent(it.isNotEmpty())
                    showEmpty(it.isEmpty())
                    updateDataSet(it)
                }
            }
            .onResourceIntermediate { view?.showRefresh(true) }
            .onResourceSuccess {
                analytics.logEvent(
                    "load_data",
                    "type" to "attendance_summary",
                    "items" to it.size,
                    "item_id" to subjectId
                )
            }
            .onResourceNotLoading {
                view?.run {
                    showProgress(false)
                    showRefresh(false)
                    enableSwipe(true)
                }
            }
            .onResourceError(errorHandler::dispatch)
            .launch()
    }

    private fun sortItems(items: List<AttendanceSummary>) = items.sortedByDescending { item ->
        if (item.month.value <= Month.JUNE.value) item.month.value + 12 else item.month.value
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
        flatResourceFlow {
            val student = studentRepository.getCurrentStudent()
            val semester = semesterRepository.getCurrentSemester(student)
            subjectRepository.getSubjects(student, semester)
        }
            .logResourceStatus("load attendance summary subjects")
            .onResourceData {
                subjects = it
                view?.run {
                    view?.updateSubjects(it.map { subject -> subject.name }.toList())
                    showSubjects(true)
                }
            }
            .onResourceError(errorHandler::dispatch)
            .launch("subjects")
    }
}
