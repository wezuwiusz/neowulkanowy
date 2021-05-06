package io.github.wulkanowy.ui.modules.attendance

import android.annotation.SuppressLint
import io.github.wulkanowy.data.Status
import io.github.wulkanowy.data.db.entities.Attendance
import io.github.wulkanowy.data.repositories.AttendanceRepository
import io.github.wulkanowy.data.repositories.PreferencesRepository
import io.github.wulkanowy.data.repositories.SemesterRepository
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.utils.AnalyticsHelper
import io.github.wulkanowy.utils.afterLoading
import io.github.wulkanowy.utils.capitalise
import io.github.wulkanowy.utils.flowWithResource
import io.github.wulkanowy.utils.flowWithResourceIn
import io.github.wulkanowy.utils.getLastSchoolDayIfHoliday
import io.github.wulkanowy.utils.isHolidays
import io.github.wulkanowy.utils.nextSchoolDay
import io.github.wulkanowy.utils.previousOrSameSchoolDay
import io.github.wulkanowy.utils.previousSchoolDay
import io.github.wulkanowy.utils.toFormattedString
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import java.time.LocalDate
import java.time.LocalDate.now
import java.time.LocalDate.ofEpochDay
import javax.inject.Inject

class AttendancePresenter @Inject constructor(
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository,
    private val attendanceRepository: AttendanceRepository,
    private val semesterRepository: SemesterRepository,
    private val prefRepository: PreferencesRepository,
    private val analytics: AnalyticsHelper
) : BasePresenter<AttendanceView>(errorHandler, studentRepository) {

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
        reloadView(ofEpochDay(date ?: baseDate.toEpochDay()))
        loadData()
        if (currentDate.isHolidays) setBaseDateOnHolidays()
    }

    fun onPreviousDay() {
        view?.finishActionMode()
        attendanceToExcuseList.clear()
        reloadView(currentDate.previousSchoolDay)
        loadData()
    }

    fun onNextDay() {
        view?.finishActionMode()
        attendanceToExcuseList.clear()
        reloadView(currentDate.nextSchoolDay)
        loadData()
    }

    fun onPickDate() {
        view?.showDatePickerDialog(currentDate)
    }

    fun onDateSet(year: Int, month: Int, day: Int) {
        reloadView(LocalDate.of(year, month, day))
        loadData()
    }

    fun onSwipeRefresh() {
        Timber.i("Force refreshing the attendance")
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

    fun onViewReselected() {
        Timber.i("Attendance view is reselected")
        view?.also { view ->
            if (view.currentStackSize == 1) {
                baseDate.also {
                    if (currentDate != it) {
                        reloadView(it)
                        loadData()
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
        flow {
            val student = studentRepository.getCurrentStudent()
            emit(semesterRepository.getCurrentSemester(student))
        }.catch {
            Timber.i("Loading semester result: An exception occurred")
        }.onEach {
            baseDate = baseDate.getLastSchoolDayIfHoliday(it.schoolYear)
            currentDate = baseDate
            reloadNavigation()
        }.launch("holidays")
    }

    private fun loadData(forceRefresh: Boolean = false) {
        Timber.i("Loading attendance data started")

        flowWithResourceIn {
            val student = studentRepository.getCurrentStudent()
            val semester = semesterRepository.getCurrentSemester(student)
            attendanceRepository.getAttendance(
                student,
                semester,
                currentDate,
                currentDate,
                forceRefresh
            )
        }.onEach {
            when (it.status) {
                Status.LOADING -> {
                    view?.showExcuseButton(false)
                    if (!it.data.isNullOrEmpty()) {
                        val filteredAttendance = if (prefRepository.isShowPresent) {
                            it.data
                        } else {
                            it.data.filter { item -> !item.presence }
                        }

                        view?.run {
                            enableSwipe(true)
                            showRefresh(true)
                            showProgress(false)
                            showEmpty(filteredAttendance.isEmpty())
                            showContent(filteredAttendance.isNotEmpty())
                            updateData(filteredAttendance.sortedBy { item -> item.number })
                        }
                    }
                }
                Status.SUCCESS -> {
                    Timber.i("Loading attendance result: Success")
                    val filteredAttendance = if (prefRepository.isShowPresent) {
                        it.data.orEmpty()
                    } else {
                        it.data?.filter { item -> !item.presence }.orEmpty()
                    }

                    view?.apply {
                        updateData(filteredAttendance.sortedBy { item -> item.number })
                        showEmpty(filteredAttendance.isEmpty())
                        showErrorView(false)
                        showContent(filteredAttendance.isNotEmpty())
                        showExcuseButton(filteredAttendance.any { item -> item.excusable })
                    }
                    analytics.logEvent(
                        "load_data",
                        "type" to "attendance",
                        "items" to it.data!!.size
                    )
                }
                Status.ERROR -> {
                    Timber.i("Loading attendance result: An exception occurred")
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

    private fun excuseAbsence(reason: String?, toExcuseList: List<Attendance>) {
        flowWithResource {
            val student = studentRepository.getCurrentStudent()
            val semester = semesterRepository.getCurrentSemester(student)
            attendanceRepository.excuseForAbsence(student, semester, toExcuseList, reason)
        }.onEach {
            when (it.status) {
                Status.LOADING -> view?.run {
                    Timber.i("Excusing absence started")
                    showProgress(true)
                    showContent(false)
                    showExcuseButton(false)
                }
                Status.SUCCESS -> {
                    Timber.i("Excusing for absence result: Success")
                    analytics.logEvent("excuse_absence", "items" to attendanceToExcuseList.size)
                    attendanceToExcuseList.clear()
                    view?.run {
                        showExcuseButton(false)
                        showMessage(excuseSuccessString)
                        showContent(true)
                        showProgress(false)
                    }
                    loadData(forceRefresh = true)
                }
                Status.ERROR -> {
                    Timber.i("Excusing for absence result: An exception occurred")
                    errorHandler.dispatch(it.error!!)
                    loadData()
                }
            }
        }.launch("excuse")
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

    private fun reloadView(date: LocalDate) {
        currentDate = date

        Timber.i("Reload attendance view with the date ${currentDate.toFormattedString()}")
        view?.apply {
            showProgress(true)
            enableSwipe(false)
            showRefresh(false)
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
            updateNavigationDay(currentDate.toFormattedString("EEEE, dd.MM").capitalise())
        }
    }
}
