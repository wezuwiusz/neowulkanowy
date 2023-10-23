package io.github.wulkanowy.ui.modules.timetable

import io.github.wulkanowy.data.dataOrNull
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.db.entities.Timetable
import io.github.wulkanowy.data.enums.TimetableGapsMode.BETWEEN_AND_BEFORE_LESSONS
import io.github.wulkanowy.data.enums.TimetableGapsMode.NO_GAPS
import io.github.wulkanowy.data.enums.TimetableMode
import io.github.wulkanowy.data.flatResourceFlow
import io.github.wulkanowy.data.logResourceStatus
import io.github.wulkanowy.data.onResourceData
import io.github.wulkanowy.data.onResourceError
import io.github.wulkanowy.data.onResourceIntermediate
import io.github.wulkanowy.data.onResourceNotLoading
import io.github.wulkanowy.data.onResourceSuccess
import io.github.wulkanowy.data.repositories.PreferencesRepository
import io.github.wulkanowy.data.repositories.SemesterRepository
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.data.repositories.TimetableRepository
import io.github.wulkanowy.data.toFirstResult
import io.github.wulkanowy.data.waitForResult
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.utils.AnalyticsHelper
import io.github.wulkanowy.utils.capitalise
import io.github.wulkanowy.utils.getLastSchoolDayIfHoliday
import io.github.wulkanowy.utils.isHolidays
import io.github.wulkanowy.utils.isJustFinished
import io.github.wulkanowy.utils.isShowTimeUntil
import io.github.wulkanowy.utils.left
import io.github.wulkanowy.utils.monday
import io.github.wulkanowy.utils.nextOrSameSchoolDay
import io.github.wulkanowy.utils.nextSchoolDay
import io.github.wulkanowy.utils.previousSchoolDay
import io.github.wulkanowy.utils.sunday
import io.github.wulkanowy.utils.toFormattedString
import io.github.wulkanowy.utils.until
import kotlinx.coroutines.flow.firstOrNull
import timber.log.Timber
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDate.now
import java.time.LocalDate.of
import java.time.LocalDate.ofEpochDay
import java.util.Timer
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

    private var initialDate: LocalDate? = null
    private var isWeekendHasLessons: Boolean = false

    var currentDate: LocalDate? = null
        private set

    private lateinit var lastError: Throwable

    private var tickTimer: Timer? = null

    fun onAttachView(view: TimetableView, date: Long?) {
        super.onAttachView(view)
        view.initView()
        Timber.i("Timetable was initialized")
        errorHandler.showErrorMessage = ::showErrorViewOnError
        currentDate = date?.let(::ofEpochDay)
        loadData()
    }

    fun onPreviousDay() {
        val date = if (isWeekendHasLessons) {
            currentDate?.minusDays(1)
        } else currentDate?.previousSchoolDay

        reloadView(date ?: return)
        loadData()
    }

    fun onNextDay() {
        val date = if (isWeekendHasLessons) {
            currentDate?.plusDays(1)
        } else currentDate?.nextSchoolDay

        reloadView(date ?: return)
        loadData()
    }

    fun onPickDate() {
        view?.showDatePickerDialog(currentDate ?: return)
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
                if (currentDate != initialDate) {
                    reloadView(initialDate ?: return)
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

    private fun loadData(forceRefresh: Boolean = false) {
        flatResourceFlow {
            val student = studentRepository.getCurrentStudent()
            val semester = semesterRepository.getCurrentSemester(student)

            checkInitialAndCurrentDate(student, semester)
            timetableRepository.getTimetable(
                student = student,
                semester = semester,
                start = currentDate ?: now(),
                end = currentDate ?: now(),
                forceRefresh = forceRefresh,
                timetableType = TimetableRepository.TimetableType.NORMAL
            )
        }
            .logResourceStatus("load timetable data")
            .onResourceData {
                isWeekendHasLessons = isWeekendHasLessons || isWeekendHasLessons(it.lessons)

                view?.run {
                    enableSwipe(true)
                    showProgress(false)
                    showErrorView(false)
                    showContent(it.lessons.isNotEmpty())
                    showEmpty(it.lessons.isEmpty())
                    updateData(it.lessons)
                    setDayHeaderMessage(it.headers.find { header -> header.date == currentDate }?.content)
                    reloadNavigation()
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

    private suspend fun checkInitialAndCurrentDate(student: Student, semester: Semester) {
        if (initialDate == null) {
            val lessons = timetableRepository.getTimetable(
                student = student,
                semester = semester,
                start = now().monday,
                end = now().sunday,
                forceRefresh = false,
                timetableType = TimetableRepository.TimetableType.NORMAL
            ).toFirstResult().dataOrNull?.lessons.orEmpty()
            isWeekendHasLessons = isWeekendHasLessons(lessons)
            initialDate = getInitialDate(semester)
        }

        if (currentDate == null) {
            currentDate = initialDate
        }
    }

    private fun isWeekendHasLessons(
        lessons: List<Timetable>,
    ): Boolean = lessons.any {
        it.date.dayOfWeek in listOf(
            DayOfWeek.SATURDAY,
            DayOfWeek.SUNDAY,
        )
    }

    private fun getInitialDate(semester: Semester): LocalDate {
        val now = now()

        return when {
            now.isHolidays -> now.getLastSchoolDayIfHoliday(semester.schoolYear)
            isWeekendHasLessons -> now
            else -> now.nextOrSameSchoolDay
        }
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

        var prevNum = when (prefRepository.showTimetableGaps) {
            BETWEEN_AND_BEFORE_LESSONS -> 0
            else -> null
        }
        return buildList {
            filteredItems.forEachIndexed { i, it ->
                if (prefRepository.showTimetableGaps != NO_GAPS && prevNum != null && it.number > prevNum!! + 1) {
                    val emptyLesson = TimetableItem.Empty(
                        numFrom = prevNum!! + 1,
                        numTo = it.number - 1
                    )
                    add(emptyLesson)
                }

                if (it.isStudentPlan) {
                    val normalLesson = TimetableItem.Normal(
                        lesson = it,
                        showGroupsInPlan = prefRepository.showGroupsInPlan,
                        timeLeft = filteredItems.getTimeLeftForLesson(it, i),
                        onClick = ::onTimetableItemSelected
                    )
                    add(normalLesson)
                } else {
                    val smallLesson = TimetableItem.Small(
                        lesson = it,
                        onClick = ::onTimetableItemSelected
                    )
                    add(smallLesson)
                }

                prevNum = it.number
            }
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

        Timber.i("Reload timetable view with the date ${currentDate?.toFormattedString()}")
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
        val currentDate = currentDate ?: return

        view?.apply {
            showPreButton(!currentDate.minusDays(1).isHolidays)
            showNextButton(!currentDate.plusDays(1).isHolidays)
            updateNavigationDay(currentDate.toFormattedString("EEEE, dd.MM").capitalise())
            showNavigation(true)
        }
    }

    override fun onDetachView() {
        tickTimer?.cancel()
        tickTimer = null
        super.onDetachView()
    }
}
