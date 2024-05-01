package io.github.wulkanowy.ui.modules.timetable.additional.add

import io.github.wulkanowy.data.db.entities.TimetableAdditional
import io.github.wulkanowy.data.repositories.SemesterRepository
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.data.repositories.TimetableRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.utils.lastSchoolDayInSchoolYear
import io.github.wulkanowy.utils.toLocalDate
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import java.util.UUID
import javax.inject.Inject

class AdditionalLessonAddPresenter @Inject constructor(
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository,
    private val timetableRepository: TimetableRepository,
    private val semesterRepository: SemesterRepository
) : BasePresenter<AdditionalLessonAddView>(errorHandler, studentRepository) {

    private var selectedStartTime = LocalTime.of(15, 0)

    private var selectedEndTime = LocalTime.of(15, 45)

    private var selectedDate = LocalDate.now()

    override fun onAttachView(view: AdditionalLessonAddView) {
        super.onAttachView(view)
        view.initView(selectedDate)
        Timber.i("AdditionalLesson details view was initialized")
    }

    fun showDatePicker() {
        view?.showDatePickerDialog(selectedDate)
    }

    fun showStartTimePicker() {
        view?.showStartTimePickerDialog(selectedStartTime)
    }

    fun showEndTimePicker() {
        view?.showEndTimePickerDialog(selectedEndTime)
    }

    fun onStartTimeSelected(time: LocalTime) {
        selectedStartTime = time
    }

    fun onEndTimeSelected(time: LocalTime) {
        selectedEndTime = time
    }

    fun onDateSelected(date: LocalDate) {
        selectedDate = date
    }

    fun onAddAdditionalClicked(
        start: String?,
        end: String?,
        date: String?,
        content: String?,
        isRepeat: Boolean
    ) {
        if (isUserInputValid(start, end, date, content)) {
            addAdditionalLesson(
                start = LocalTime.parse(start!!),
                end = LocalTime.parse(end),
                date = date!!.toLocalDate(),
                subject = content!!,
                isRepeat = isRepeat
            )
        }
    }

    private fun isUserInputValid(
        start: String?,
        end: String?,
        date: String?,
        content: String?
    ): Boolean {
        var isValid = true

        if (start.isNullOrBlank()) {
            view?.setErrorStartRequired()
            isValid = false
        }

        if (end.isNullOrBlank()) {
            view?.setErrorEndRequired()
            isValid = false
        }

        if (date.isNullOrBlank()) {
            view?.setErrorDateRequired()
            isValid = false
        }

        if (content.isNullOrBlank()) {
            view?.setErrorContentRequired()
            isValid = false
        }

        if (selectedStartTime >= selectedEndTime) {
            view?.setErrorIncorrectEndTime()
            isValid = false
        }

        return isValid
    }

    private fun addAdditionalLesson(
        start: LocalTime,
        end: LocalTime,
        date: LocalDate,
        subject: String,
        isRepeat: Boolean
    ) {
        presenterScope.launch {
            val semester = runCatching {
                val student = studentRepository.getCurrentStudent()
                semesterRepository.getCurrentSemester(student)
            }
                .onFailure(errorHandler::dispatch)
                .getOrNull() ?: return@launch

            val weeks = if (isRepeat) {
                ChronoUnit.WEEKS.between(date, date.lastSchoolDayInSchoolYear)
            } else 0
            val uniqueRepeatId = UUID.randomUUID().takeIf { isRepeat }

            val lessonsToAdd = (0..weeks).map {
                TimetableAdditional(
                    studentId = semester.studentId,
                    diaryId = semester.diaryId,
                    start = ZonedDateTime.of(date, start, ZoneId.systemDefault()).toInstant(),
                    end = ZonedDateTime.of(date, end, ZoneId.systemDefault()).toInstant(),
                    date = date.plusWeeks(it),
                    subject = subject
                ).apply {
                    isAddedByUser = true
                    repeatId = uniqueRepeatId
                }
            }

            Timber.i("AdditionalLesson insert start")
            runCatching { timetableRepository.saveAdditionalList(lessonsToAdd) }
                .onSuccess {
                    Timber.i("AdditionalLesson insert: Success")
                    view?.run {
                        showSuccessMessage()
                        closeDialog()
                    }
                }
                .onFailure {
                    Timber.i("AdditionalLesson insert result: An exception occurred")
                    errorHandler.dispatch(it)
                }
        }
    }
}
