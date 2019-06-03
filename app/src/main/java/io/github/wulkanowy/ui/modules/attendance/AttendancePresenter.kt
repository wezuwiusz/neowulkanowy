package io.github.wulkanowy.ui.modules.attendance

import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import io.github.wulkanowy.data.repositories.attendance.AttendanceRepository
import io.github.wulkanowy.data.repositories.preferences.PreferencesRepository
import io.github.wulkanowy.data.repositories.semester.SemesterRepository
import io.github.wulkanowy.data.repositories.student.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.utils.FirebaseAnalyticsHelper
import io.github.wulkanowy.utils.*
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

    lateinit var currentDate: LocalDate
        private set

    fun onAttachView(view: AttendanceView, date: Long?) {
        super.onAttachView(view)
        view.initView()
        Timber.i("Attendance view was initialized")
        loadData(ofEpochDay(date ?: now().previousOrSameSchoolDay.toEpochDay()))
        reloadView()
    }

    fun onPreviousDay() {
        loadData(currentDate.previousSchoolDay)
        reloadView()
    }

    fun onNextDay() {
        loadData(currentDate.nextSchoolDay)
        reloadView()
    }

    fun onSwipeRefresh() {
        Timber.i("Force refreshing the attendance")
        loadData(currentDate, true)
    }

    fun onViewReselected() {
        Timber.i("Attendance view is reselected")
        view?.also { view ->
            if (view.currentStackSize == 1) {
                now().previousOrSameSchoolDay.also {
                    if (currentDate != it) {
                        loadData(it)
                        reloadView()
                    } else if (!view.isViewEmpty) view.resetView()
                }
            } else view.popView()
        }
    }

    fun onAttendanceItemSelected(item: AbstractFlexibleItem<*>?) {
        if (item is AttendanceItem) {
            Timber.i("Select attendance item ${item.attendance.id}")
            view?.showAttendanceDialog(item.attendance)
        }
    }

    fun onSummarySwitchSelected(): Boolean {
        view?.openSummaryView()
        return true
    }

    private fun loadData(date: LocalDate, forceRefresh: Boolean = false) {
        Timber.i("Loading attendance data started")
        currentDate = date
        disposable.apply {
            clear()
            add(studentRepository.getCurrentStudent()
                .delay(200, MILLISECONDS)
                .flatMap { semesterRepository.getCurrentSemester(it) }
                .flatMap { attendanceRepository.getAttendance(it, date, date, forceRefresh) }
                .map { list ->
                    if (prefRepository.isShowPresent) list
                    else list.filter { !it.presence }
                }
                .map { items -> items.map { AttendanceItem(it) } }
                .map { items -> items.sortedBy { it.attendance.number } }
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
                        showContent(it.isNotEmpty())
                    }
                    analytics.logEvent("load_attendance", "items" to it.size, "force_refresh" to forceRefresh)
                }) {
                    Timber.i("Loading attendance result: An exception occurred")
                    view?.run { showEmpty(isViewEmpty) }
                    errorHandler.dispatch(it)
                }
            )
        }
    }

    private fun reloadView() {
        Timber.i("Reload attendance view with the date ${currentDate.toFormattedString()}")
        view?.apply {
            showProgress(true)
            enableSwipe(false)
            showContent(false)
            showEmpty(false)
            clearData()
            showNextButton(!currentDate.plusDays(1).isHolidays)
            showPreButton(!currentDate.minusDays(1).isHolidays)
            updateNavigationDay(currentDate.toFormattedString("EEEE\ndd.MM.YYYY").capitalize())
        }
    }
}
