package io.github.wulkanowy.ui.main.attendance

import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import io.github.wulkanowy.data.ErrorHandler
import io.github.wulkanowy.data.db.entities.Attendance
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.repositories.AttendanceRepository
import io.github.wulkanowy.data.repositories.SessionRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.utils.*
import io.github.wulkanowy.utils.schedulers.SchedulersManager
import org.threeten.bp.LocalDate
import javax.inject.Inject

class AttendancePresenter @Inject constructor(
        private val errorHandler: ErrorHandler,
        private val schedulers: SchedulersManager,
        private val attendanceRepository: AttendanceRepository,
        private val sessionRepository: SessionRepository
) : BasePresenter<AttendanceView>(errorHandler) {

    var currentDate: LocalDate = LocalDate.now().nearSchoolDayPrevOnWeekEnd
        private set

    override fun attachView(view: AttendanceView) {
        super.attachView(view)
        view.initView()
    }

    fun loadAttendanceForPreviousDay() = loadData(currentDate.previousWorkDay.toEpochDay())

    fun loadAttendanceForNextDay() = loadData(currentDate.nextWorkDay.toEpochDay())

    fun loadData(date: Long?, forceRefresh: Boolean = false) {
        this.currentDate = LocalDate.ofEpochDay(date
                ?: currentDate.nearSchoolDayPrevOnWeekEnd.toEpochDay())
        if (currentDate.isHolidays) return

        disposable.clear()
        disposable.add(sessionRepository.getSemesters()
                .map { selectSemester(it, -1) }
                .flatMap { attendanceRepository.getAttendance(it, currentDate, currentDate, forceRefresh) }
                .map { createTimetableItems(it) }
                .subscribeOn(schedulers.backgroundThread())
                .observeOn(schedulers.mainThread())
                .doOnSubscribe {
                    view?.run {
                        showRefresh(forceRefresh)
                        showProgress(!forceRefresh)
                        if (!forceRefresh) {
                            showEmpty(false)
                            clearData()
                        }
                        showPreButton(!currentDate.minusDays(1).isHolidays)
                        showNextButton(!currentDate.plusDays(1).isHolidays)
                        updateNavigationDay(currentDate.toFormattedString("EEEE \n dd.MM.YYYY").capitalize())
                    }
                }
                .doFinally {
                    view?.run {
                        showRefresh(false)
                        showProgress(false)
                    }
                }
                .subscribe({
                    view?.run {
                        showEmpty(it.isEmpty())
                        showContent(it.isNotEmpty())
                        updateData(it)
                    }
                }) {
                    view?.run { showEmpty(isViewEmpty()) }
                    errorHandler.proceed(it)
                })
    }

    private fun createTimetableItems(items: List<Attendance>): List<AttendanceItem> {
        return items.map {
            AttendanceItem().apply { attendance = it }
        }
    }

    fun onAttendanceItemSelected(item: AbstractFlexibleItem<*>?) {
        if (item is AttendanceItem) view?.showAttendanceDialog(item.attendance)
    }

    private fun selectSemester(semesters: List<Semester>, index: Int): Semester {
        return semesters.single { it.current }.let { currentSemester ->
            if (index == -1) currentSemester
            else semesters.single { semester ->
                semester.run {
                    semesterName - 1 == index && diaryId == currentSemester.diaryId
                }
            }
        }
    }
}
