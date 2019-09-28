package io.github.wulkanowy.ui.modules.timetable.completed

import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import io.github.wulkanowy.data.repositories.completedlessons.CompletedLessonsRepository
import io.github.wulkanowy.data.repositories.semester.SemesterRepository
import io.github.wulkanowy.data.repositories.student.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
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
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class CompletedLessonsPresenter @Inject constructor(
    schedulers: SchedulersProvider,
    studentRepository: StudentRepository,
    private val completedLessonsErrorHandler: CompletedLessonsErrorHandler,
    private val semesterRepository: SemesterRepository,
    private val completedLessonsRepository: CompletedLessonsRepository,
    private val analytics: FirebaseAnalyticsHelper
) : BasePresenter<CompletedLessonsView>(completedLessonsErrorHandler, studentRepository, schedulers) {

    private var baseDate: LocalDate = now().nextOrSameSchoolDay

    lateinit var currentDate: LocalDate
        private set

    fun onAttachView(view: CompletedLessonsView, date: Long?) {
        super.onAttachView(view)
        Timber.i("Completed lessons is attached")
        view.initView()
        loadData(ofEpochDay(date ?: baseDate.toEpochDay()))
        if (currentDate.isHolidays) setBaseDateOnHolidays()
        reloadView()
        completedLessonsErrorHandler.onFeatureDisabled = {
            this.view?.showFeatureDisabled()
            Timber.i("Completed lessons feature disabled by school")
        }
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
        Timber.i("Force refreshing the completed lessons")
        loadData(currentDate, true)
    }

    fun onCompletedLessonsItemSelected(item: AbstractFlexibleItem<*>?) {
        if (item is CompletedLessonItem) {
            Timber.i("Select completed lessons item ${item.completedLesson.id}")
            view?.showCompletedLessonDialog(item.completedLesson)
        }
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
        Timber.i("Loading completed lessons data started")
        currentDate = date
        disposable.apply {
            clear()
            add(studentRepository.getCurrentStudent()
                .flatMap { semesterRepository.getCurrentSemester(it) }
                .delay(200, TimeUnit.MILLISECONDS)
                .flatMap { completedLessonsRepository.getCompletedLessons(it, currentDate, currentDate, forceRefresh) }
                .map { items -> items.map { CompletedLessonItem(it) } }
                .map { items -> items.sortedBy { it.completedLesson.number } }
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
                    Timber.i("Loading completed lessons lessons result: Success")
                    view?.apply {
                        updateData(it)
                        showEmpty(it.isEmpty())
                        showContent(it.isNotEmpty())
                    }
                    analytics.logEvent("load_completed_lessons", "items" to it.size, "force_refresh" to forceRefresh)
                }) {
                    Timber.i("Loading completed lessons result: An exception occurred")
                    view?.run { showEmpty(isViewEmpty) }
                    completedLessonsErrorHandler.dispatch(it)
                })
        }
    }

    private fun reloadView() {
        Timber.i("Reload completed lessons view with the date ${currentDate.toFormattedString()}")
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
