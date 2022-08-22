package io.github.wulkanowy.ui.modules.timetable

import io.github.wulkanowy.data.*
import io.github.wulkanowy.data.db.entities.Timetable
import io.github.wulkanowy.data.enums.TimetableMode
import io.github.wulkanowy.data.repositories.PreferencesRepository
import io.github.wulkanowy.data.repositories.SemesterRepository
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.data.repositories.TimetableRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.utils.*
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDate.*
import java.util.*
import javax.inject.Inject
import kotlin.concurrent.timer

class TimetablePresenter @Inject constructor(
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository,
    private val timetableRepository: TimetableRepository,
    private val semesterRepository: SemesterRepository,
    private val prefRepository: PreferencesRepository,
    private val analytics: AnalyticsHelper,
) : BasePresenter<TimetableView>(errorHandler, studentRepository) {

    private var baseDate: LocalDate = now().nextOrSameSchoolDay

    lateinit var currentDate: LocalDate
        private set

    private lateinit var lastError: Throwable

    private var tickTimer: Timer? = null

    fun onAttachView(view: TimetableView, date: Long?) {
        super.onAttachView(view)
        view.initView()
        Timber.i("Timetable was initialized")
        errorHandler.showErrorMessage = ::showErrorViewOnError
        reloadView(ofEpochDay(date ?: baseDate.toEpochDay()))
        loadData()
        if (currentDate.isHolidays) setBaseDateOnHolidays()
    }

    fun onPreviousDay() {
        reloadView(currentDate.previousSchoolDay)
        loadData()
    }

    fun onNextDay() {
        reloadView(currentDate.nextSchoolDay)
        loadData()
    }

    fun onPickDate() {
        view?.showDatePickerDialog(currentDate)
    }

    fun onDateSet(year: Int, month: Int, day: Int) {
        reloadView(of(year, month, day))
        loadData()
    }

    fun onSwipeRefresh() {
        Timber.i("Force refreshing the timetable")
        loadData(true)
    }

    fun onRetry() {
        view?.run {
            showErrorView(false)
            showProgress(true)
        }
        loadData(true)
    }

    fun onDetailsClick() {
        view?.showErrorDetailsDialog(lastError)
    }

    fun onViewReselected() {
        Timber.i("Timetable view is reselected")
        view?.let { view ->
            if (view.currentStackSize == 1) {
                baseDate = now().nextOrSameSchoolDay

                if (currentDate != baseDate) {
                    reloadView(baseDate)
                    loadData()
                } else if (!view.isViewEmpty) {
                    view.resetView()
                }
            } else {
                view.popView()
            }
        }
    }

    fun onAdditionalLessonsSwitchSelected(): Boolean {
        view?.openAdditionalLessonsView()
        return true
    }

    fun onCompletedLessonsSwitchSelected(): Boolean {
        view?.openCompletedLessonsView()
        return true
    }

    private fun setBaseDateOnHolidays() {
        flow {
            val student = studentRepository.getCurrentStudent()
            emit(semesterRepository.getCurrentSemester(student))
        }.catch {
            Timber.i("Loading semester result: An exception occurred")
        }.onEach {
            baseDate = baseDate.getLastSchoolDayIfHoliday(it.schoolYear)
            currentDate = baseDate
            reloadNavigation()
        }.launch("holidays")
    }

    private fun loadData(forceRefresh: Boolean = false) {
        flatResourceFlow {
            val student = studentRepository.getCurrentStudent()
            val semester = semesterRepository.getCurrentSemester(student)
            timetableRepository.getTimetable(
                student = student,
                semester = semester,
                start = currentDate,
                end = currentDate,
                forceRefresh = forceRefresh,
                timetableType = TimetableRepository.TimetableType.NORMAL
            )
        }
            .logResourceStatus("load timetable data")
            .onResourceData {
                view?.run {
                    enableSwipe(true)
                    showProgress(false)
                    showErrorView(false)
                    showContent(it.lessons.isNotEmpty())
                    showEmpty(it.lessons.isEmpty())
                    updateData(it.lessons)
                    setDayHeaderMessage(it.headers.singleOrNull { header -> header.date == currentDate }?.content)
                }
            }
            .onResourceIntermediate { view?.showRefresh(true) }
            .onResourceSuccess {
                analytics.logEvent(
                    "load_data",
                    "type" to "timetable",
                    "items" to it.lessons.size
                )
            }
            .onResourceNotLoading {
                view?.run {
                    enableSwipe(true)
                    showProgress(false)
                    showRefresh(false)
                }
            }
            .onResourceError(errorHandler::dispatch)
            .launch()
    }

    private fun updateData(lessons: List<Timetable>) {
        tickTimer?.cancel()

        if (!prefRepository.showTimetableTimers) {
            view?.updateData(createItems(lessons))
        } else {
            tickTimer = timer(period = 2_000) {
                view?.updateData(createItems(lessons))
            }
        }
    }

    private fun createItems(items: List<Timetable>): List<TimetableItem> {
        val filteredItems = items
            .filter {
                if (prefRepository.showWholeClassPlan == TimetableMode.ONLY_CURRENT_GROUP) {
                    it.isStudentPlan
                } else true
            }.sortedWith(
                compareBy({ item -> item.number }, { item -> !item.isStudentPlan })
            )

        return filteredItems.mapIndexed { i, it ->
            if (it.isStudentPlan) TimetableItem.Normal(
                lesson = it,
                showGroupsInPlan = prefRepository.showGroupsInPlan,
                timeLeft = filteredItems.getTimeLeftForLesson(it, i),
                onClick = ::onTimetableItemSelected
            ) else TimetableItem.Small(
                lesson = it,
                onClick = ::onTimetableItemSelected
            )
        }
    }

    private fun List<Timetable>.getTimeLeftForLesson(lesson: Timetable, index: Int): TimeLeft {
        val isShowTimeUntil = lesson.isShowTimeUntil(getPreviousLesson(index))
        return TimeLeft(
            until = lesson.until.plusMinutes(1).takeIf { isShowTimeUntil },
            left = lesson.left?.plusMinutes(1),
            isJustFinished = lesson.isJustFinished,
        )
    }

    private fun List<Timetable>.getPreviousLesson(position: Int): Instant? {
        return filter { it.isStudentPlan }
            .getOrNull(position - 1 - filterIndexed { i, item -> i < position && !item.isStudentPlan }.size)
            ?.let {
                if (!it.canceled && it.isStudentPlan) it.end
                else null
            }
    }

    private fun onTimetableItemSelected(lesson: Timetable) {
        Timber.i("Select timetable item ${lesson.id}")
        view?.showTimetableDialog(lesson)
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

    private fun reloadView(date: LocalDate) {
        currentDate = date

        Timber.i("Reload timetable view with the date ${currentDate.toFormattedString()}")
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
            showPreButton(!currentDate.minusDays(1).isHolidays)
            showNextButton(!currentDate.plusDays(1).isHolidays)
            updateNavigationDay(currentDate.toFormattedString("EEEE, dd.MM").capitalise())
        }
    }

    override fun onDetachView() {
        tickTimer?.cancel()
        tickTimer = null
        super.onDetachView()
    }
}
