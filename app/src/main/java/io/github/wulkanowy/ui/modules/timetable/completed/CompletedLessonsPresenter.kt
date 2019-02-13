package io.github.wulkanowy.ui.modules.timetable.completed

import com.google.firebase.analytics.FirebaseAnalytics.Param.START_DATE
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import io.github.wulkanowy.data.repositories.completedlessons.CompletedLessonsRepository
import io.github.wulkanowy.data.repositories.semester.SemesterRepository
import io.github.wulkanowy.data.repositories.student.StudentRepository
import io.github.wulkanowy.ui.base.session.BaseSessionPresenter
import io.github.wulkanowy.utils.FirebaseAnalyticsHelper
import io.github.wulkanowy.utils.SchedulersProvider
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
    private val schedulers: SchedulersProvider,
    private val errorHandler: CompletedLessonsErrorHandler,
    private val studentRepository: StudentRepository,
    private val semesterRepository: SemesterRepository,
    private val completedLessonsRepository: CompletedLessonsRepository,
    private val analytics: FirebaseAnalyticsHelper
) : BaseSessionPresenter<CompletedLessonsView>(errorHandler) {

    lateinit var currentDate: LocalDate
        private set

    fun onAttachView(view: CompletedLessonsView, date: Long?) {
        super.onAttachView(view)
        Timber.i("Completed lessons is attached")
        view.initView()
        loadData(ofEpochDay(date ?: now().nextOrSameSchoolDay.toEpochDay()))
        reloadView()
        errorHandler.onFeatureDisabled = {
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
                    }
                }
                .subscribe({
                    Timber.i("Loading completed lessons lessons result: Success")
                    view?.apply {
                        updateData(it)
                        showEmpty(it.isEmpty())
                        showContent(it.isNotEmpty())
                    }
                    analytics.logEvent("load_completed_lessons", "items" to it.size, "force_refresh" to forceRefresh, START_DATE to currentDate.toFormattedString("yyyy-MM-dd"))
                }) {
                    Timber.i("Loading completed lessons result: An exception occurred")
                    view?.run { showEmpty(isViewEmpty) }
                    errorHandler.dispatch(it)
                })
        }
    }

    private fun reloadView() {
        Timber.i("Reload completed lessons view with the date ${currentDate.toFormattedString()}")
        view?.apply {
            showProgress(true)
            showContent(false)
            showEmpty(false)
            clearData()
            showNextButton(!currentDate.plusDays(1).isHolidays)
            showPreButton(!currentDate.minusDays(1).isHolidays)
            updateNavigationDay(currentDate.toFormattedString("EEEE \n dd.MM.YYYY").capitalize())
        }
    }
}
