package io.github.wulkanowy.ui.modules.timetable

import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import io.github.wulkanowy.data.repositories.semester.SemesterRepository
import io.github.wulkanowy.data.repositories.student.StudentRepository
import io.github.wulkanowy.data.repositories.timetable.TimetableRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.utils.FirebaseAnalyticsHelper
import io.github.wulkanowy.utils.SchedulersProvider
import io.github.wulkanowy.utils.getLastSchoolDayIfHoliday
import io.github.wulkanowy.utils.isHolidays
import io.github.wulkanowy.utils.nextOrSameSchoolDay
import io.github.wulkanowy.utils.nextSchoolDay
import io.github.wulkanowy.utils.previousSchoolDay
import io.github.wulkanowy.utils.toFormattedString
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDate.now
import org.threeten.bp.LocalDate.ofEpochDay
import timber.log.Timber
import java.util.concurrent.TimeUnit.MILLISECONDS
import javax.inject.Inject

class TimetablePresenter @Inject constructor(
    schedulers: SchedulersProvider,
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository,
    private val timetableRepository: TimetableRepository,
    private val semesterRepository: SemesterRepository,
    private val analytics: FirebaseAnalyticsHelper
) : BasePresenter<TimetableView>(errorHandler, studentRepository, schedulers) {

    private var baseDate: LocalDate = now().nextOrSameSchoolDay

    lateinit var currentDate: LocalDate
        private set

    fun onAttachView(view: TimetableView, date: Long?) {
        super.onAttachView(view)
        view.initView()
        Timber.i("Timetable was initialized")
        loadData(ofEpochDay(date ?: baseDate.toEpochDay()))
        if (currentDate.isHolidays) setBaseDateOnHolidays()
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
        Timber.i("Force refreshing the timetable")
        loadData(currentDate, true)
    }

    fun onViewReselected() {
        Timber.i("Timetable view is reselected")
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

    fun onTimetableItemSelected(item: AbstractFlexibleItem<*>?) {
        if (item is TimetableItem) {
            Timber.i("Select timetable item ${item.lesson.id}")
            view?.showTimetableDialog(item.lesson)
        }
    }

    fun onCompletedLessonsSwitchSelected(): Boolean {
        view?.openCompletedLessonsView()
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
        Timber.i("Loading timetable data started")
        currentDate = date
        disposable.apply {
            clear()
            add(studentRepository.getCurrentStudent()
                .flatMap { semesterRepository.getCurrentSemester(it) }
                .delay(200, MILLISECONDS)
                .flatMap { timetableRepository.getTimetable(it, currentDate, currentDate, forceRefresh) }
                .map { items -> items.map { TimetableItem(it, view?.roomString.orEmpty()) } }
                .map { items -> items.sortedBy { it.lesson.number } }
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
                    Timber.i("Loading timetable result: Success")
                    view?.apply {
                        updateData(it)
                        showEmpty(it.isEmpty())
                        showContent(it.isNotEmpty())
                    }
                    analytics.logEvent("load_timetable", "items" to it.size, "force_refresh" to forceRefresh)
                }) {
                    Timber.i("Loading timetable result: An exception occurred")
                    view?.run { showEmpty(isViewEmpty) }
                    errorHandler.dispatch(it)
                })
        }
    }

    private fun reloadView() {
        Timber.i("Reload timetable view with the date ${currentDate.toFormattedString()}")
        view?.apply {
            showProgress(true)
            enableSwipe(false)
            showContent(false)
            showEmpty(false)
            clearData()
            reloadNavigation()
        }
    }

    private fun reloadNavigation() {
        view?.apply {
            showPreButton(!currentDate.minusDays(1).isHolidays)
            showNextButton(!currentDate.plusDays(1).isHolidays)
            updateNavigationDay(currentDate.toFormattedString("EEEE, dd.MM").capitalize())
        }
    }
}
