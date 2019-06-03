package io.github.wulkanowy.ui.modules.exam

import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import io.github.wulkanowy.data.db.entities.Exam
import io.github.wulkanowy.data.repositories.exam.ExamRepository
import io.github.wulkanowy.data.repositories.semester.SemesterRepository
import io.github.wulkanowy.data.repositories.student.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.utils.FirebaseAnalyticsHelper
import io.github.wulkanowy.utils.SchedulersProvider
import io.github.wulkanowy.utils.friday
import io.github.wulkanowy.utils.isHolidays
import io.github.wulkanowy.utils.monday
import io.github.wulkanowy.utils.nextOrSameSchoolDay
import io.github.wulkanowy.utils.toFormattedString
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDate.now
import org.threeten.bp.LocalDate.ofEpochDay
import timber.log.Timber
import java.util.concurrent.TimeUnit.MILLISECONDS
import javax.inject.Inject

class ExamPresenter @Inject constructor(
    schedulers: SchedulersProvider,
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository,
    private val examRepository: ExamRepository,
    private val semesterRepository: SemesterRepository,
    private val analytics: FirebaseAnalyticsHelper
) : BasePresenter<ExamView>(errorHandler, studentRepository, schedulers) {

    lateinit var currentDate: LocalDate
        private set

    fun onAttachView(view: ExamView, date: Long?) {
        super.onAttachView(view)
        view.initView()
        Timber.i("Exam view was initialized")
        loadData(ofEpochDay(date ?: now().nextOrSameSchoolDay.toEpochDay()))
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

    fun onExamItemSelected(item: AbstractFlexibleItem<*>?) {
        if (item is ExamItem) {
            Timber.i("Select exam item ${item.exam.id}")
            view?.showExamDialog(item.exam)
        }
    }

    fun onViewReselected() {
        Timber.i("Exam view is reselected")
        now().nextOrSameSchoolDay.also {
            if (currentDate != it) {
                loadData(it)
                reloadView()
            } else if (view?.isViewEmpty == false) view?.resetView()
        }
    }

    private fun loadData(date: LocalDate, forceRefresh: Boolean = false) {
        Timber.i("Loading exam data started")
        currentDate = date
        disposable.apply {
            clear()
            add(studentRepository.getCurrentStudent()
                .delay(200, MILLISECONDS)
                .flatMap { semesterRepository.getCurrentSemester(it) }
                .flatMap {
                    examRepository.getExams(it, currentDate.monday, currentDate.friday, forceRefresh)
                }.map { it.groupBy { exam -> exam.date }.toSortedMap() }
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
                        showContent(it.isNotEmpty())
                    }
                    analytics.logEvent("load_exam", "items" to it.size, "force_refresh" to forceRefresh)
                }) {
                    Timber.i("Loading exam result: An exception occurred")
                    view?.run { showEmpty(isViewEmpty) }
                    errorHandler.dispatch(it)
                })
        }
    }

    private fun createExamItems(items: Map<LocalDate, List<Exam>>): List<ExamItem> {
        return items.flatMap {
            ExamHeader(it.key).let { header ->
                it.value.reversed().map { item -> ExamItem(header, item) }
            }
        }
    }

    private fun reloadView() {
        Timber.i("Reload exam view with the date ${currentDate.toFormattedString()}")
        view?.apply {
            showProgress(true)
            enableSwipe(false)
            showContent(false)
            showEmpty(false)
            clearData()
            showPreButton(!currentDate.minusDays(7).isHolidays)
            showNextButton(!currentDate.plusDays(7).isHolidays)
            updateNavigationWeek("${currentDate.monday.toFormattedString("dd.MM")} - " +
                currentDate.friday.toFormattedString("dd.MM"))
        }
    }
}
