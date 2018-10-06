package io.github.wulkanowy.ui.main.timetable

import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import io.github.wulkanowy.data.ErrorHandler
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Timetable
import io.github.wulkanowy.data.repositories.SessionRepository
import io.github.wulkanowy.data.repositories.TimetableRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.utils.*
import io.github.wulkanowy.utils.schedulers.SchedulersManager
import org.threeten.bp.LocalDate
import javax.inject.Inject

class TimetablePresenter @Inject constructor(
        private val errorHandler: ErrorHandler,
        private val schedulers: SchedulersManager,
        private val timetableRepository: TimetableRepository,
        private val sessionRepository: SessionRepository
) : BasePresenter<TimetableView>(errorHandler) {

    var currentDate: LocalDate = LocalDate.now().nearSchoolDayNextOnWeekEnd
        private set

    override fun attachView(view: TimetableView) {
        super.attachView(view)
        view.initView()
    }

    fun loadTimetableForPreviousDay() = loadData(currentDate.previousWorkDay.toEpochDay())

    fun loadTimetableForNextDay() = loadData(currentDate.nextWorkDay.toEpochDay())

    fun loadData(date: Long?, forceRefresh: Boolean = false) {
        this.currentDate = LocalDate.ofEpochDay(date ?: currentDate.nearSchoolDayNextOnWeekEnd.toEpochDay())
        if (currentDate.isHolidays) return

        disposable.clear()
        disposable.add(sessionRepository.getSemesters()
                .map { selectSemester(it, -1) }
                .flatMap { timetableRepository.getTimetable(it, currentDate, currentDate, forceRefresh) }
                .map { createTimetableItems(it) }
                .subscribeOn(schedulers.backgroundThread())
                .observeOn(schedulers.mainThread())
                .doOnSubscribe {
                    view?.run {
                        showRefresh(forceRefresh)
                        showProgress(!forceRefresh)
                        if (!forceRefresh) clearData()
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

    private fun createTimetableItems(items: List<Timetable>): List<TimetableItem> {
        return items.map {
            TimetableItem().apply { lesson = it }
        }
    }

    fun onTimetableItemSelected(item: AbstractFlexibleItem<*>?) {
        if (item is TimetableItem) view?.showTimetableDialog(item.lesson)
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
