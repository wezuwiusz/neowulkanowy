package io.github.wulkanowy.ui.modules.timetable.additional

import android.annotation.SuppressLint
import io.github.wulkanowy.data.db.entities.TimetableAdditional
import io.github.wulkanowy.data.flatResourceFlow
import io.github.wulkanowy.data.logResourceStatus
import io.github.wulkanowy.data.onResourceData
import io.github.wulkanowy.data.onResourceError
import io.github.wulkanowy.data.onResourceNotLoading
import io.github.wulkanowy.data.onResourceSuccess
import io.github.wulkanowy.data.repositories.SemesterRepository
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.data.repositories.TimetableRepository
import io.github.wulkanowy.domain.timetable.IsStudentHasLessonsOnWeekendUseCase
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.utils.AnalyticsHelper
import io.github.wulkanowy.utils.capitalise
import io.github.wulkanowy.utils.getLastSchoolDayIfHoliday
import io.github.wulkanowy.utils.isHolidays
import io.github.wulkanowy.utils.nextOrSameSchoolDay
import io.github.wulkanowy.utils.nextSchoolDay
import io.github.wulkanowy.utils.previousSchoolDay
import io.github.wulkanowy.utils.toFormattedString
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDate
import javax.inject.Inject

class AdditionalLessonsPresenter @Inject constructor(
    studentRepository: StudentRepository,
    errorHandler: ErrorHandler,
    private val semesterRepository: SemesterRepository,
    private val timetableRepository: TimetableRepository,
    private val isStudentHasLessonsOnWeekendUseCase: IsStudentHasLessonsOnWeekendUseCase,
    private val analytics: AnalyticsHelper
) : BasePresenter<AdditionalLessonsView>(errorHandler, studentRepository) {

    private var baseDate: LocalDate = LocalDate.now().nextOrSameSchoolDay

    private var isWeekendHasLessons: Boolean = false

    lateinit var currentDate: LocalDate
        private set

    private lateinit var lastError: Throwable

    fun onAttachView(view: AdditionalLessonsView, date: Long?) {
        super.onAttachView(view)
        view.initView()
        Timber.i("Additional lessons was initialized")
        errorHandler.showErrorMessage = ::showErrorViewOnError
        loadData(LocalDate.ofEpochDay(date ?: baseDate.toEpochDay()))
        if (currentDate.isHolidays) setBaseDateOnHolidays()
        reloadView()
    }

    fun onPreviousDay() {
        val date = if (isWeekendHasLessons) {
            currentDate.minusDays(1)
        } else currentDate.previousSchoolDay
        loadData(date)
        reloadView()
    }

    fun onNextDay() {
        val date = if (isWeekendHasLessons) {
            currentDate.plusDays(1)
        } else currentDate.nextSchoolDay
        loadData(date)
        reloadView()
    }

    fun onPickDate() {
        view?.showDatePickerDialog(currentDate)
    }

    fun onAdditionalLessonAddButtonClicked() {
        view?.showAddAdditionalLessonDialog(currentDate)
    }

    fun onDateSet(year: Int, month: Int, day: Int) {
        loadData(LocalDate.of(year, month, day))
        reloadView()
    }

    fun onSwipeRefresh() {
        Timber.i("Force refreshing the additional lessons")
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

    fun onDeleteLessonsSelected(timetableAdditional: TimetableAdditional) {
        if (timetableAdditional.repeatId == null) {
            deleteAdditionalLessons(timetableAdditional, false)
        } else {
            view?.showDeleteLessonDialog(timetableAdditional)
        }
    }

    fun onDeleteDialogSelectItem(position: Int, timetableAdditional: TimetableAdditional) {
        deleteAdditionalLessons(timetableAdditional, position == 1)
    }

    private fun deleteAdditionalLessons(
        timetableAdditional: TimetableAdditional,
        deleteSeries: Boolean
    ) {
        presenterScope.launch {
            Timber.i("Additional Lesson delete start")
            runCatching { timetableRepository.deleteAdditional(timetableAdditional, deleteSeries) }
                .onSuccess {
                    Timber.i("Additional Lesson delete: Success")
                    view?.showSuccessMessage()
                }
                .onFailure {
                    Timber.i("Additional Lesson delete result: An exception occurred")
                    errorHandler.dispatch(it)
                }
        }
    }

    private fun loadData(date: LocalDate, forceRefresh: Boolean = false) {
        currentDate = date

        flatResourceFlow {
            val student = studentRepository.getCurrentStudent()
            val semester = semesterRepository.getCurrentSemester(student)

            isWeekendHasLessons = isStudentHasLessonsOnWeekendUseCase(semester, currentDate)
            timetableRepository.getTimetable(
                student = student,
                semester = semester,
                start = date,
                end = date,
                forceRefresh = forceRefresh,
                refreshAdditional = true,
                timetableType = TimetableRepository.TimetableType.ADDITIONAL
            )
        }
            .logResourceStatus("load additional lessons")
            .onResourceData {
                view?.apply {
                    updateData(it.additional.sortedBy { item -> item.start })
                    showEmpty(it.additional.isEmpty())
                    showErrorView(false)
                    showContent(it.additional.isNotEmpty())
                }
            }
            .onResourceSuccess {
                analytics.logEvent(
                    "load_data",
                    "type" to "additional_lessons",
                    "items" to it.additional.size
                )
            }
            .onResourceNotLoading {
                view?.run {
                    hideRefresh()
                    showProgress(false)
                    enableSwipe(true)
                }
            }
            .onResourceError(errorHandler::dispatch)
            .launch()
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

    private fun reloadView() {
        Timber.i("Reload additional lessons view with the date ${currentDate.toFormattedString()}")
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

    @SuppressLint("DefaultLocale")
    private fun reloadNavigation() {
        view?.apply {
            showPreButton(!currentDate.minusDays(1).isHolidays)
            showNextButton(!currentDate.plusDays(1).isHolidays)
            updateNavigationDay(currentDate.toFormattedString("EEEE, dd.MM").capitalise())
        }
    }
}
