package io.github.wulkanowy.ui.modules.timetable

import com.google.firebase.analytics.FirebaseAnalytics.Param.START_DATE
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import io.github.wulkanowy.data.repositories.SemesterRepository
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.data.repositories.TimetableRepository
import io.github.wulkanowy.ui.base.session.BaseSessionPresenter
import io.github.wulkanowy.ui.base.session.SessionErrorHandler
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
import java.util.concurrent.TimeUnit.MILLISECONDS
import javax.inject.Inject

class TimetablePresenter @Inject constructor(
    private val errorHandler: SessionErrorHandler,
    private val schedulers: SchedulersProvider,
    private val timetableRepository: TimetableRepository,
    private val studentRepository: StudentRepository,
    private val semesterRepository: SemesterRepository,
    private val analytics: FirebaseAnalyticsHelper
) : BaseSessionPresenter<TimetableView>(errorHandler) {

    lateinit var currentDate: LocalDate
        private set

    fun onAttachView(view: TimetableView, date: Long?) {
        super.onAttachView(view)
        view.initView()
        loadData(ofEpochDay(date ?: now().nextOrSameSchoolDay.toEpochDay()))
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
        loadData(currentDate, true)
    }

    fun onViewReselected() {
        now().nextOrSameSchoolDay.also {
            if (currentDate != it) {
                loadData(it)
                reloadView()
            } else if (view?.isViewEmpty == false) view?.resetView()
        }
    }

    fun onTimetableItemSelected(item: AbstractFlexibleItem<*>?) {
        if (item is TimetableItem) view?.showTimetableDialog(item.lesson)
    }

    private fun loadData(date: LocalDate, forceRefresh: Boolean = false) {
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
                    }
                }
                .subscribe({
                    view?.apply {
                        updateData(it)
                        showEmpty(it.isEmpty())
                        showContent(it.isNotEmpty())
                    }
                    analytics.logEvent("load_attendance", mapOf("items" to it.size, "force_refresh" to forceRefresh, START_DATE to currentDate.toFormattedString("yyyy-MM-dd")))
                }) {
                    view?.run { showEmpty(isViewEmpty) }
                    errorHandler.dispatch(it)
                })
        }
    }

    private fun reloadView() {
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
