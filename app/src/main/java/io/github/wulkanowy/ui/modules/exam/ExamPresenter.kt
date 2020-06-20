package io.github.wulkanowy.ui.modules.exam

import io.github.wulkanowy.data.db.entities.Exam
import io.github.wulkanowy.data.repositories.exam.ExamRepository
import io.github.wulkanowy.data.repositories.semester.SemesterRepository
import io.github.wulkanowy.data.repositories.student.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.utils.FirebaseAnalyticsHelper
import io.github.wulkanowy.utils.SchedulersProvider
import io.github.wulkanowy.utils.sunday
import io.github.wulkanowy.utils.getLastSchoolDayIfHoliday
import io.github.wulkanowy.utils.isHolidays
import io.github.wulkanowy.utils.monday
import io.github.wulkanowy.utils.nextOrSameSchoolDay
import io.github.wulkanowy.utils.toFormattedString
import kotlinx.coroutines.rx2.rxSingle
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDate.now
import org.threeten.bp.LocalDate.ofEpochDay
import timber.log.Timber
import javax.inject.Inject

class ExamPresenter @Inject constructor(
    schedulers: SchedulersProvider,
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository,
    private val examRepository: ExamRepository,
    private val semesterRepository: SemesterRepository,
    private val analytics: FirebaseAnalyticsHelper
) : BasePresenter<ExamView>(errorHandler, studentRepository, schedulers) {

    private var baseDate: LocalDate = now().nextOrSameSchoolDay

    lateinit var currentDate: LocalDate
        private set

    private lateinit var lastError: Throwable

    fun onAttachView(view: ExamView, date: Long?) {
        super.onAttachView(view)
        view.initView()
        Timber.i("Exam view was initialized")
        errorHandler.showErrorMessage = ::showErrorViewOnError
        loadData(ofEpochDay(date ?: baseDate.toEpochDay()))
        if (currentDate.isHolidays) setBaseDateOnHolidays()
        reloadView()
    }

    fun onPreviousWeek() {
        loadData(currentDate.minusDays(7))
        reloadView()
    }

    fun onNextWeek() {
        loadData(currentDate.plusDays(7))
        reloadView()
    }

    fun onSwipeRefresh() {
        Timber.i("Force refreshing the exam")
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

    fun onExamItemSelected(exam: Exam) {
        Timber.i("Select exam item ${exam.id}")
        view?.showExamDialog(exam)
    }

    fun onViewReselected() {
        Timber.i("Exam view is reselected")
        baseDate.also {
            if (currentDate != it) {
                loadData(it)
                reloadView()
            } else if (view?.isViewEmpty == false) view?.resetView()
        }
    }

    private fun setBaseDateOnHolidays() {
        disposable.add(rxSingle { studentRepository.getCurrentStudent() }
            .flatMap { rxSingle { semesterRepository.getCurrentSemester(it) } }
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
        Timber.i("Loading exam data started")
        currentDate = date
        disposable.apply {
            clear()
            add(rxSingle { studentRepository.getCurrentStudent() }
                .flatMap { student ->
                    rxSingle { semesterRepository.getCurrentSemester(student) }.flatMap { semester ->
                        rxSingle { examRepository.getExams(student, semester, currentDate.monday, currentDate.sunday, forceRefresh) }
                    }
                }
                .map { createExamItems(it) }
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
                    Timber.i("Loading exam result: Success")
                    view?.apply {
                        updateData(it)
                        showEmpty(it.isEmpty())
                        showErrorView(false)
                        showContent(it.isNotEmpty())
                    }
                    analytics.logEvent(
                        "load_data",
                        "type" to "exam",
                        "items" to it.size,
                        "force_refresh" to forceRefresh
                    )
                }) {
                    Timber.i("Loading exam result: An exception occurred")
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

    private fun createExamItems(items: List<Exam>): List<ExamItem<*>> {
        return items.groupBy { it.date }.toSortedMap().map { (date, exams) ->
            listOf(ExamItem(date, ExamItem.ViewType.HEADER)) + exams.reversed().map { exam ->
                ExamItem(exam, ExamItem.ViewType.ITEM)
            }
        }.flatten()
    }

    private fun reloadView() {
        Timber.i("Reload exam view with the date ${currentDate.toFormattedString()}")
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
                currentDate.sunday.toFormattedString("dd.MM"))
        }
    }
}
