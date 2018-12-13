package io.github.wulkanowy.ui.modules.attendance.summary

import io.github.wulkanowy.data.ErrorHandler
import io.github.wulkanowy.data.db.entities.AttendanceSummary
import io.github.wulkanowy.data.db.entities.Subject
import io.github.wulkanowy.data.repositories.AttendanceSummaryRepository
import io.github.wulkanowy.data.repositories.SemesterRepository
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.data.repositories.SubjectRepostory
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.utils.FirebaseAnalyticsHelper
import io.github.wulkanowy.utils.SchedulersProvider
import io.github.wulkanowy.utils.calculatePercentage
import io.github.wulkanowy.utils.getFormattedName
import java.lang.String.format
import java.util.Locale.FRANCE
import java.util.concurrent.TimeUnit.MILLISECONDS
import javax.inject.Inject

class AttendanceSummaryPresenter @Inject constructor(
    private val errorHandler: ErrorHandler,
    private val attendanceSummaryRepository: AttendanceSummaryRepository,
    private val subjectRepository: SubjectRepostory,
    private val studentRepository: StudentRepository,
    private val semesterRepository: SemesterRepository,
    private val schedulers: SchedulersProvider,
    private val analytics: FirebaseAnalyticsHelper
) : BasePresenter<AttendanceSummaryView>(errorHandler) {

    private var subjects = emptyList<Subject>()

    var currentSubjectId = -1
        private set

    fun onAttachView(view: AttendanceSummaryView, subjectId: Int?) {
        super.onAttachView(view)
        view.initView()
        loadData(subjectId ?: -1)
        loadSubjects()
    }

    fun onSwipeRefresh() {
        loadData(currentSubjectId, true)
    }

    fun onSubjectSelected(name: String) {
        view?.run {
            showContent(false)
            showProgress(true)
            clearView()
        }
        loadData(subjects.singleOrNull { it.name == name }?.realId ?: -1)
    }

    private fun loadData(subjectId: Int, forceRefresh: Boolean = false) {
        currentSubjectId = subjectId
        disposable.apply {
            clear()
            add(studentRepository.getCurrentStudent()
                .delay(200, MILLISECONDS)
                .flatMap { semesterRepository.getCurrentSemester(it) }
                .flatMap { attendanceSummaryRepository.getAttendanceSummary(it, subjectId, forceRefresh) }
                .map { createAttendanceSummaryItems(it) to AttendanceSummaryScrollableHeader(formatPercentage(it.calculatePercentage())) }
                .subscribeOn(schedulers.backgroundThread)
                .observeOn(schedulers.mainThread)
                .doFinally {
                    view?.run {
                        hideRefresh()
                        showProgress(false)
                    }
                }
                .subscribe({
                    view?.apply {
                        showEmpty(it.first.isEmpty())
                        showContent(it.first.isNotEmpty())
                        updateDataSet(it.first, it.second)
                    }
                    analytics.logEvent("load_attendance_summary", mapOf("items" to it.first.size, "force_refresh" to forceRefresh, "item_id" to subjectId))
                }) {
                    view?.run { showEmpty(isViewEmpty) }
                    errorHandler.dispatch(it)
                }
            )
        }
    }

    private fun loadSubjects() {
        disposable.add(studentRepository.getCurrentStudent()
            .flatMap { semesterRepository.getCurrentSemester(it) }
            .flatMap { subjectRepository.getSubjects(it) }
            .doOnSuccess { subjects = it }
            .map { ArrayList(it.map { subject -> subject.name }) }
            .subscribeOn(schedulers.backgroundThread)
            .observeOn(schedulers.mainThread)
            .subscribe({
                view?.run {
                    view?.updateSubjects(it)
                    showSubjects(true)
                }
            }, { errorHandler.dispatch(it) })
        )
    }

    private fun createAttendanceSummaryItems(attendanceSummary: List<AttendanceSummary>): List<AttendanceSummaryItem> {
        return attendanceSummary.sortedByDescending { it.id }.map {
            AttendanceSummaryItem(
                month = it.month.getFormattedName(),
                percentage = formatPercentage(it.calculatePercentage()),
                present = it.presence.toString(),
                absence = it.absence.toString(),
                excusedAbsence = it.absenceExcused.toString(),
                schoolAbsence = it.absenceForSchoolReasons.toString(),
                exemption = it.exemption.toString(),
                lateness = it.lateness.toString(),
                excusedLateness = it.latenessExcused.toString()
            )
        }
    }

    private fun formatPercentage(percentage: Double): String {
        return if (percentage == 0.0) "0%"
        else "${format(FRANCE, "%.2f", percentage)}%"
    }
}
