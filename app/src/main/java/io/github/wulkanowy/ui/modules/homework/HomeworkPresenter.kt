package io.github.wulkanowy.ui.modules.homework

import com.google.firebase.analytics.FirebaseAnalytics.Param.START_DATE
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import io.github.wulkanowy.data.db.entities.Homework
import io.github.wulkanowy.data.repositories.homework.HomeworkRepository
import io.github.wulkanowy.data.repositories.semester.SemesterRepository
import io.github.wulkanowy.data.repositories.student.StudentRepository
import io.github.wulkanowy.ui.base.session.BaseSessionPresenter
import io.github.wulkanowy.ui.base.session.SessionErrorHandler
import io.github.wulkanowy.utils.FirebaseAnalyticsHelper
import io.github.wulkanowy.utils.SchedulersProvider
import io.github.wulkanowy.utils.friday
import io.github.wulkanowy.utils.isHolidays
import io.github.wulkanowy.utils.monday
import io.github.wulkanowy.utils.nextOrSameSchoolDay
import io.github.wulkanowy.utils.toFormattedString
import org.threeten.bp.LocalDate
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class HomeworkPresenter @Inject constructor(
    private val errorHandler: SessionErrorHandler,
    private val schedulers: SchedulersProvider,
    private val homeworkRepository: HomeworkRepository,
    private val studentRepository: StudentRepository,
    private val semesterRepository: SemesterRepository,
    private val analytics: FirebaseAnalyticsHelper
) : BaseSessionPresenter<HomeworkView>(errorHandler) {

    lateinit var currentDate: LocalDate
        private set

    fun onAttachView(view: HomeworkView, date: Long?) {
        super.onAttachView(view)
        view.initView()
        Timber.i("Homework view was initialized")
        loadData(LocalDate.ofEpochDay(date ?: LocalDate.now().nextOrSameSchoolDay.toEpochDay()))
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

    fun onHomeworkItemSelected(item: AbstractFlexibleItem<*>?) {
        if (item is HomeworkItem) {
            Timber.i("Select homework item ${item.homework.id}")
            view?.showTimetableDialog(item.homework)
        }
    }

    private fun loadData(date: LocalDate, forceRefresh: Boolean = false) {
        Timber.i("Loading homework data started")
        currentDate = date
        disposable.apply {
            clear()
            add(studentRepository.getCurrentStudent()
                .delay(200, TimeUnit.MILLISECONDS)
                .flatMap { semesterRepository.getCurrentSemester(it) }
                .flatMap { homeworkRepository.getHomework(it, currentDate, currentDate, forceRefresh) }
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
                        showContent(it.isNotEmpty())
                    }
                    analytics.logEvent("load_homework", "items" to it.size, "force_refresh" to forceRefresh, START_DATE to currentDate.toFormattedString("yyyy-MM-dd"))
                }) {
                    Timber.i("Loading homework result: An exception occurred")
                    view?.run { showEmpty(isViewEmpty()) }
                    errorHandler.dispatch(it)
                })
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
            clearData()
            showNextButton(!currentDate.plusDays(7).isHolidays)
            showPreButton(!currentDate.minusDays(7).isHolidays)
            updateNavigationWeek("${currentDate.monday.toFormattedString("dd.MM")} - " +
                currentDate.friday.toFormattedString("dd.MM"))
        }
    }
}
