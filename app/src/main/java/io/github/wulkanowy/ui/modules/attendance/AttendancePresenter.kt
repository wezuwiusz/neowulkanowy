package io.github.wulkanowy.ui.modules.attendance

import android.annotation.SuppressLint
import io.github.wulkanowy.data.db.entities.Attendance
import io.github.wulkanowy.data.repositories.attendance.AttendanceRepository
import io.github.wulkanowy.data.repositories.preferences.PreferencesRepository
import io.github.wulkanowy.data.repositories.semester.SemesterRepository
import io.github.wulkanowy.data.repositories.student.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.utils.FirebaseAnalyticsHelper
import io.github.wulkanowy.utils.SchedulersProvider
import io.github.wulkanowy.utils.getLastSchoolDayIfHoliday
import io.github.wulkanowy.utils.isHolidays
import io.github.wulkanowy.utils.nextSchoolDay
import io.github.wulkanowy.utils.previousOrSameSchoolDay
import io.github.wulkanowy.utils.previousSchoolDay
import io.github.wulkanowy.utils.toFormattedString
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDate.now
import org.threeten.bp.LocalDate.ofEpochDay
import timber.log.Timber
import java.util.concurrent.TimeUnit.MILLISECONDS
import javax.inject.Inject

class AttendancePresenter @Inject constructor(
    schedulers: SchedulersProvider,
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository,
    private val attendanceRepository: AttendanceRepository,
    private val semesterRepository: SemesterRepository,
    private val prefRepository: PreferencesRepository,
    private val analytics: FirebaseAnalyticsHelper
) : BasePresenter<AttendanceView>(errorHandler, studentRepository, schedulers) {

    private var baseDate: LocalDate = now().previousOrSameSchoolDay

    lateinit var currentDate: LocalDate
        private set

    private lateinit var lastError: Throwable

    private val attendanceToExcuseList = mutableListOf<Attendance>()

    fun onAttachView(view: AttendanceView, date: Long?) {
        super.onAttachView(view)
        view.initView()
        Timber.i("Attendance view was initialized")
        errorHandler.showErrorMessage = ::showErrorViewOnError
        loadData(ofEpochDay(date ?: baseDate.toEpochDay()))
        if (currentDate.isHolidays) setBaseDateOnHolidays()
        reloadView()
    }

    fun onPreviousDay() {
        view?.finishActionMode()
        attendanceToExcuseList.clear()
        loadData(currentDate.previousSchoolDay)
        reloadView()
    }

    fun onNextDay() {
        view?.finishActionMode()
        attendanceToExcuseList.clear()
        loadData(currentDate.nextSchoolDay)
        reloadView()
    }

    fun onPickDate() {
        view?.showDatePickerDialog(currentDate)
    }

    fun onDateSet(year: Int, month: Int, day: Int) {
        loadData(LocalDate.of(year, month, day))
        reloadView()
    }

    fun onSwipeRefresh() {
        Timber.i("Force refreshing the attendance")
        loadData(currentDate, true)
    }

    fun onRetry() {
        view?.run {
            showErrorView(false)
            showProgress(true)
        }
        loadData(currentDate, true)
    }

    fun onDetailsClick() {
        view?.showErrorDetailsDialog(lastError)
    }

    fun onViewReselected() {
        Timber.i("Attendance view is reselected")
        view?.also { view ->
            if (view.currentStackSize == 1) {
                baseDate.also {
                    if (currentDate != it) {
                        loadData(it)
                        reloadView()
                    } else if (!view.isViewEmpty) view.resetView()
                }
            } else view.popView()
        }
    }

    fun onMainViewChanged() {
        view?.finishActionMode()
    }

    fun onAttendanceItemSelected(attendance: Attendance) {
        view?.apply {
            if (!excuseActionMode) {
                Timber.i("Select attendance item ${attendance.id}")
                showAttendanceDialog(attendance)
            }
        }
    }

    fun onExcuseButtonClick() {
        view?.startActionMode()
    }

    fun onExcuseCheckboxSelect(attendanceItem: Attendance, checked: Boolean) {
        if (checked) attendanceToExcuseList.add(attendanceItem)
        else attendanceToExcuseList.remove(attendanceItem)
    }

    fun onExcuseSubmitButtonClick(): Boolean {
        view?.apply {
            return if (attendanceToExcuseList.isNotEmpty()) {
                showExcuseDialog()
                true
            } else {
                showMessage(excuseNoSelectionString)
                false
            }
        }
        return false
    }

    fun onExcuseDialogSubmit(reason: String) {
        view?.finishActionMode()
        excuseAbsence(if (reason != "") reason else null, attendanceToExcuseList.toList())
    }

    fun onPrepareActionMode(): Boolean {
        view?.apply {
            showExcuseCheckboxes(true)
            showExcuseButton(false)
        }
        attendanceToExcuseList.clear()
        return true
    }

    fun onDestroyActionMode() {
        view?.apply {
            showExcuseCheckboxes(false)
            showExcuseButton(true)
        }
    }

    fun onSummarySwitchSelected(): Boolean {
        view?.openSummaryView()
        return true
    }

    private fun setBaseDateOnHolidays() {
        disposable.add(studentRepository.getCurrentStudent()
            .flatMap { semesterRepository.getCurrentSemester(it) }
            .subscribeOn(schedulers.backgroundThread)
            .observeOn(schedulers.mainThread)
            .subscribe({
                baseDate = baseDate.getLastSchoolDayIfHoliday(it.schoolYear)
                currentDate = baseDate
                reloadNavigation()
            }) {
                Timber.i("Loading semester result: An exception occurred")
            })
    }

    private fun loadData(date: LocalDate, forceRefresh: Boolean = false) {
        Timber.i("Loading attendance data started")
        currentDate = date
        disposable.apply {
            clear()
            add(studentRepository.getCurrentStudent()
                .flatMap { student ->
                    semesterRepository.getCurrentSemester(student).flatMap { semester ->
                        attendanceRepository.getAttendance(student, semester, date, date, forceRefresh)
                    }
                }
                .map { list ->
                    if (prefRepository.isShowPresent) list
                    else list.filter { !it.presence }
                }
                .map { items -> items.sortedBy { it.number } }
                .subscribeOn(schedulers.backgroundThread)
                .observeOn(schedulers.mainThread)
                .doFinally {
                    view?.run {
                        hideRefresh()
                        showProgress(false)
                        enableSwipe(true)
                    }
                }
                .subscribe({
                    Timber.i("Loading attendance result: Success")
                    view?.apply {
                        updateData(it)
                        showEmpty(it.isEmpty())
                        showErrorView(false)
                        showContent(it.isNotEmpty())
                        showExcuseButton(it.any { item -> item.excusable })
                    }
                    analytics.logEvent(
                        "load_data",
                        "type" to "attendance",
                        "items" to it.size,
                        "force_refresh" to forceRefresh
                    )
                }) {
                    Timber.i("Loading attendance result: An exception occurred")
                    errorHandler.dispatch(it)
                }
            )
        }
    }

    private fun excuseAbsence(reason: String?, toExcuseList: List<Attendance>) {
        Timber.i("Excusing absence started")
        disposable.apply {
            add(studentRepository.getCurrentStudent()
                .flatMap { student ->
                    semesterRepository.getCurrentSemester(student).flatMap { semester ->
                        attendanceRepository.excuseForAbsence(student, semester, toExcuseList, reason)
                    }
                }
                .subscribeOn(schedulers.backgroundThread)
                .observeOn(schedulers.mainThread)
                .doOnSubscribe {
                    view?.apply {
                        showProgress(true)
                        showContent(false)
                        showExcuseButton(false)
                    }
                }
                .subscribe({
                    Timber.i("Excusing for absence result: Success")
                    analytics.logEvent("excuse_absence", "items" to attendanceToExcuseList.size)
                    attendanceToExcuseList.clear()
                    view?.apply {
                        showExcuseButton(false)
                        showMessage(excuseSuccessString)
                    }
                    loadData(currentDate, true)
                }) {
                    Timber.i("Excusing for absence result: An exception occurred")
                    view?.showProgress(false)
                    errorHandler.dispatch(it)
                })
        }
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

    private fun reloadView() {
        Timber.i("Reload attendance view with the date ${currentDate.toFormattedString()}")
        view?.apply {
            showProgress(true)
            enableSwipe(false)
            showContent(false)
            showEmpty(false)
            showErrorView(false)
            clearData()
            reloadNavigation()
        }
    }

    @SuppressLint("DefaultLocale")
    private fun reloadNavigation() {
        view?.apply {
            showPreButton(!currentDate.minusDays(1).isHolidays)
            showNextButton(!currentDate.plusDays(1).isHolidays)
            updateNavigationDay(currentDate.toFormattedString("EEEE, dd.MM").capitalize())
        }
    }
}
