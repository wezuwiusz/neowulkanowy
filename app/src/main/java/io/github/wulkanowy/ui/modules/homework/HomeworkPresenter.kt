package io.github.wulkanowy.ui.modules.homework

import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import io.github.wulkanowy.data.db.entities.Homework
import io.github.wulkanowy.data.repositories.homework.HomeworkRepository
import io.github.wulkanowy.data.repositories.semester.SemesterRepository
import io.github.wulkanowy.data.repositories.student.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.utils.FirebaseAnalyticsHelper
import io.github.wulkanowy.utils.SchedulersProvider
import io.github.wulkanowy.utils.friday
import io.github.wulkanowy.utils.getLastSchoolDayIfHoliday
import io.github.wulkanowy.utils.isHolidays
import io.github.wulkanowy.utils.monday
import io.github.wulkanowy.utils.nextOrSameSchoolDay
import io.github.wulkanowy.utils.toFormattedString
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDate.ofEpochDay
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class HomeworkPresenter @Inject constructor(
    schedulers: SchedulersProvider,
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository,
    private val homeworkRepository: HomeworkRepository,
    private val semesterRepository: SemesterRepository,
    private val analytics: FirebaseAnalyticsHelper
) : BasePresenter<HomeworkView>(errorHandler, studentRepository, schedulers) {

    private var baseDate: LocalDate = LocalDate.now().nextOrSameSchoolDay

    lateinit var currentDate: LocalDate
        private set

    private lateinit var lastError: Throwable

    fun onAttachView(view: HomeworkView, date: Long?) {
        super.onAttachView(view)
        view.initView()
        Timber.i("Homework view was initialized")
        errorHandler.showErrorMessage = ::showErrorViewOnError
        loadData(ofEpochDay(date ?: baseDate.toEpochDay()))
        if (currentDate.isHolidays) setBaseDateOnHolidays()
        reloadView()
    }

    fun onPreviousDay() {
        loadData(currentDate.minusDays(7))
        reloadView()
    }

    fun onNextDay() {
        loadData(currentDate.plusDays(7))
        reloadView()
    }

    fun onSwipeRefresh() {
        Timber.i("Force refreshing the homework")
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

    fun onHomeworkItemSelected(item: AbstractFlexibleItem<*>?) {
        if (item is HomeworkItem) {
            Timber.i("Select homework item ${item.homework.id}")
            view?.showTimetableDialog(item.homework)
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

    fun reloadData() {
        loadData(currentDate, false)
    }

    private fun loadData(date: LocalDate, forceRefresh: Boolean = false) {
        Timber.i("Loading homework data started")
        currentDate = date
        disposable.apply {
            clear()
            add(studentRepository.getCurrentStudent()
                .flatMap { student ->
                    semesterRepository.getCurrentSemester(student).flatMap { semester ->
                        homeworkRepository.getHomework(student, semester, currentDate, currentDate, forceRefresh)
                    }
                }
                .delay(200, TimeUnit.MILLISECONDS)
                .map { it.groupBy { homework -> homework.date }.toSortedMap() }
                .map { createHomeworkItem(it) }
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
                    Timber.i("Loading homework result: Success")
                    view?.apply {
                        updateData(it)
                        showEmpty(it.isEmpty())
                        showErrorView(false)
                        showContent(it.isNotEmpty())
                    }
                    analytics.logEvent("load_homework", "items" to it.size, "force_refresh" to forceRefresh)
                }) {
                    Timber.i("Loading homework result: An exception occurred")

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

    private fun createHomeworkItem(items: Map<LocalDate, List<Homework>>): List<HomeworkItem> {
        return items.flatMap {
            HomeworkHeader(it.key).let { header ->
                it.value.reversed().map { item -> HomeworkItem(header, item) }
            }
        }
    }

    private fun reloadView() {
        Timber.i("Reload homework view with the date ${currentDate.toFormattedString()}")
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

    private fun reloadNavigation() {
        view?.apply {
            showPreButton(!currentDate.minusDays(7).isHolidays)
            showNextButton(!currentDate.plusDays(7).isHolidays)
            updateNavigationWeek("${currentDate.monday.toFormattedString("dd.MM")} - " +
                currentDate.friday.toFormattedString("dd.MM"))
        }
    }
}
