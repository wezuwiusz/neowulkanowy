package io.github.wulkanowy.ui.modules.timetable

import android.os.Handler
import android.os.Looper
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Timetable
import io.github.wulkanowy.data.db.entities.TimetableAdditional
import io.github.wulkanowy.data.enums.ShowAdditionalLessonsMode.BELOW
import io.github.wulkanowy.data.enums.ShowAdditionalLessonsMode.NONE
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
import io.github.wulkanowy.data.pojos.TimetableFull
import io.github.wulkanowy.data.repositories.PreferencesRepository
import io.github.wulkanowy.data.repositories.SemesterRepository
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.data.repositories.TimetableRepository
import io.github.wulkanowy.domain.timetable.IsStudentHasLessonsOnWeekendUseCase
import io.github.wulkanowy.domain.timetable.IsWeekendHasLessonsUseCase
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.utils.AnalyticsHelper
import io.github.wulkanowy.utils.capitalise
import io.github.wulkanowy.utils.getLastSchoolDayIfHoliday
import io.github.wulkanowy.utils.isHolidays
import io.github.wulkanowy.utils.isJustFinished
import io.github.wulkanowy.utils.isShowTimeUntil
import io.github.wulkanowy.utils.left
import io.github.wulkanowy.utils.nextOrSameSchoolDay
import io.github.wulkanowy.utils.nextSchoolDay
import io.github.wulkanowy.utils.previousSchoolDay
import io.github.wulkanowy.utils.toFormattedString
import io.github.wulkanowy.utils.until
import timber.log.Timber
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
    private val isStudentHasLessonsOnWeekendUseCase: IsStudentHasLessonsOnWeekendUseCase,
    private val isWeekendHasLessonsUseCase: IsWeekendHasLessonsUseCase,
    private val semesterRepository: SemesterRepository,
    private val prefRepository: PreferencesRepository,
    private val analytics: AnalyticsHelper,
) : BasePresenter<TimetableView>(errorHandler, studentRepository) {

    private var initialDate: LocalDate? = null
    private var isWeekendHasLessons: Boolean = false
    private var isEduOne: Boolean = false

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
        loadData(isDayChanged = true)
    }

    fun onNextDay() {
        val date = if (isWeekendHasLessons) {
            currentDate?.plusDays(1)
        } else currentDate?.nextSchoolDay

        reloadView(date ?: return)
        loadData(isDayChanged = true)
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
        loadData(forceRefresh = true)
    }

    fun onRetry() {
        view?.run {
            showErrorView(false)
            showProgress(true)
        }
        loadData(forceRefresh = true)
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

    private fun loadData(forceRefresh: Boolean = false, isDayChanged: Boolean = false) {
        flatResourceFlow {
            val student = studentRepository.getCurrentStudent()
            val semester = semesterRepository.getCurrentSemester(student)

            isEduOne = student.isEduOne == true
            checkInitialAndCurrentDate(semester)
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
                isWeekendHasLessons = isWeekendHasLessons || isWeekendHasLessonsUseCase(it.lessons)

                view?.run {
                    enableSwipe(true)
                    showProgress(false)
                    showErrorView(false)
                    updateData(it, isDayChanged)
                    showContent(it.lessons.isNotEmpty() || it.additional.isNotEmpty())
                    showEmpty(it.lessons.isEmpty() && it.additional.isEmpty())
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

    private suspend fun checkInitialAndCurrentDate(semester: Semester) {
        if (initialDate == null) {
            isWeekendHasLessons = isStudentHasLessonsOnWeekendUseCase(semester)
            initialDate = getInitialDate(semester)
        }

        if (currentDate == null) {
            currentDate = initialDate
        }
    }

    private fun getInitialDate(semester: Semester): LocalDate {
        val now = now()

        return when {
            now.isHolidays -> now.getLastSchoolDayIfHoliday(semester.schoolYear)
            isWeekendHasLessons -> now
            else -> now.nextOrSameSchoolDay
        }
    }

    private fun updateData(lessons: TimetableFull, isDayChanged: Boolean) {
        tickTimer?.cancel()

        view?.updateData(createItems(lessons), isDayChanged)
        if (currentDate == now()) {
            tickTimer = timer(period = 2_000, initialDelay = 2_000) {
                Handler(Looper.getMainLooper()).post {
                    view?.updateData(createItems(lessons), isDayChanged)
                }
            }
        }
    }

    private sealed class Item(
        val isStudentPlan: Boolean,
        val start: Instant,
        val number: Int?,
    ) {
        class Lesson(val lesson: Timetable) :
            Item(lesson.isStudentPlan, lesson.start, lesson.number)

        class Additional(val additional: TimetableAdditional) : Item(true, additional.start, null)
    }

    private fun createItems(fullTimetable: TimetableFull): List<TimetableItem> {
        val showAdditionalLessonsInPlan = prefRepository.showAdditionalLessonsInPlan
        val allItems =
            fullTimetable.lessons.map(Item::Lesson) + fullTimetable.additional.map(Item::Additional)
                .takeIf { showAdditionalLessonsInPlan != NONE }.orEmpty()

        val filteredItems = allItems.filter {
            if (prefRepository.showWholeClassPlan == TimetableMode.ONLY_CURRENT_GROUP) {
                it.isStudentPlan
            } else true
        }.sortedWith(
            (compareBy<Item> { it is Item.Additional }
                .takeIf { showAdditionalLessonsInPlan == BELOW } ?: EmptyComparator())
                .thenBy { it.start }
                .thenBy { !it.isStudentPlan }
        )

        var prevNum = when (prefRepository.showTimetableGaps) {
            BETWEEN_AND_BEFORE_LESSONS -> 0
            else -> null
        }
        var prevIsAdditional = false
        return buildList {
            filteredItems.forEachIndexed { i, it ->
                if (prefRepository.showTimetableGaps != NO_GAPS) {
                    if (prevNum != null && it.number != null && it.number > prevNum!! + 1) {
                        if (!prevIsAdditional) {
                            // Additional lessons do count as a lesson so don't add empty lessons
                            // when there is an additional lesson present
                            val emptyLesson = TimetableItem.Empty(
                                numFrom = prevNum!! + 1, numTo = it.number - 1
                            )
                            add(emptyLesson)
                        }
                    }
                    prevNum = it.number
                    prevIsAdditional = it is Item.Additional
                }

                if (it is Item.Lesson) {
                    if (it.isStudentPlan) {
                        val normalLesson = TimetableItem.Normal(
                            lesson = it.lesson,
                            showGroupsInPlan = prefRepository.showGroupsInPlan,
                            timeLeft = filteredItems.getTimeLeftForLesson(it.lesson, i),
                            onClick = ::onTimetableItemSelected,
                            isLessonNumberVisible = !isEduOne
                        )
                        add(normalLesson)
                    } else {
                        val smallLesson = TimetableItem.Small(
                            lesson = it.lesson,
                            onClick = ::onTimetableItemSelected,
                            isLessonNumberVisible = !isEduOne
                        )
                        add(smallLesson)
                    }
                } else if (it is Item.Additional) {
                    // If the user disabled showing additional lessons, they would've been filtered
                    // out already, so there's no need to check it again.
                    add(TimetableItem.Additional(it.additional))
                }
            }
        }
    }

    private fun List<Item>.getTimeLeftForLesson(lesson: Timetable, index: Int): TimeLeft {
        val isShowTimeUntil = lesson.isShowTimeUntil(getPreviousLesson(index))
        return TimeLeft(
            until = lesson.until.plusMinutes(1).takeIf { isShowTimeUntil },
            left = lesson.left?.plusMinutes(1),
            isJustFinished = lesson.isJustFinished,
        )
    }

    private fun List<Item>.getPreviousLesson(position: Int): Instant? {
        val lessonAdditionalOffset = filterIndexed { i, item ->
            i < position && item is Item.Additional
        }.size
        val lessonStudentPlanOffset = filterIndexed { i, item ->
            i < position && !item.isStudentPlan
        }.size
        val lessonIndex = position - 1 - lessonAdditionalOffset - lessonStudentPlanOffset

        return filterIsInstance<Item.Lesson>()
            .filter { it.isStudentPlan }
            .getOrNull(lessonIndex)
            ?.let {
                if (!it.lesson.canceled && it.isStudentPlan) it.lesson.end
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

private class EmptyComparator<T> : Comparator<T> {
    override fun compare(o1: T, o2: T) = 0
}
