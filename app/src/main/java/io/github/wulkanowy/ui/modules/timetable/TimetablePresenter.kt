package io.github.wulkanowy.ui.modules.timetable

import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import io.github.wulkanowy.data.ErrorHandler
import io.github.wulkanowy.data.repositories.SessionRepository
import io.github.wulkanowy.data.repositories.TimetableRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.utils.*
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDate.now
import org.threeten.bp.LocalDate.ofEpochDay
import java.util.concurrent.TimeUnit.MILLISECONDS
import javax.inject.Inject

class TimetablePresenter @Inject constructor(
        private val errorHandler: ErrorHandler,
        private val schedulers: SchedulersProvider,
        private val timetableRepository: TimetableRepository,
        private val sessionRepository: SessionRepository
) : BasePresenter<TimetableView>(errorHandler) {

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
        loadData(now().nextOrSameSchoolDay)
        reloadView()
    }

    fun onTimetableItemSelected(item: AbstractFlexibleItem<*>?) {
        if (item is TimetableItem) view?.showTimetableDialog(item.lesson)
    }

    private fun loadData(date: LocalDate, forceRefresh: Boolean = false) {
        currentDate = date
        disposable.apply {
            clear()
            add(sessionRepository.getSemesters()
                    .delay(200, MILLISECONDS)
                    .map { it.single { semester -> semester.current } }
                    .flatMap { timetableRepository.getTimetable(it, currentDate, currentDate, forceRefresh) }
                    .map { items -> items.map { TimetableItem(it, view?.roomString.orEmpty()) } }
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
                    }) {
                        view?.run { showEmpty(isViewEmpty()) }
                        errorHandler.proceed(it)
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
