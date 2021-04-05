package io.github.wulkanowy.ui.modules.timetable.additional

import android.annotation.SuppressLint
import io.github.wulkanowy.data.Status
import io.github.wulkanowy.data.repositories.SemesterRepository
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.data.repositories.TimetableRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.utils.AnalyticsHelper
import io.github.wulkanowy.utils.afterLoading
import io.github.wulkanowy.utils.flowWithResourceIn
import io.github.wulkanowy.utils.getLastSchoolDayIfHoliday
import io.github.wulkanowy.utils.isHolidays
import io.github.wulkanowy.utils.nextOrSameSchoolDay
import io.github.wulkanowy.utils.nextSchoolDay
import io.github.wulkanowy.utils.previousSchoolDay
import io.github.wulkanowy.utils.toFormattedString
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import java.time.LocalDate
import javax.inject.Inject

class AdditionalLessonsPresenter @Inject constructor(
    studentRepository: StudentRepository,
    errorHandler: ErrorHandler,
    private val semesterRepository: SemesterRepository,
    private val timetableRepository: TimetableRepository,
    private val analytics: AnalyticsHelper
) : BasePresenter<AdditionalLessonsView>(errorHandler, studentRepository) {

    private var baseDate: LocalDate = LocalDate.now().nextOrSameSchoolDay

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
        loadData(currentDate.previousSchoolDay)
        reloadView()
    }

    fun onNextDay() {
        loadData(currentDate.nextSchoolDay)
        reloadView()
    }

    fun onPickDate() {
        view?.showDatePickerDialog(currentDate)
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

    private fun loadData(date: LocalDate, forceRefresh: Boolean = false) {
        currentDate = date

        flowWithResourceIn {
            val student = studentRepository.getCurrentStudent()
            val semester = semesterRepository.getCurrentSemester(student)
            timetableRepository.getTimetable(student, semester, date, date, forceRefresh, true)
        }.onEach {
            when (it.status) {
                Status.LOADING -> Timber.i("Loading additional lessons data started")
                Status.SUCCESS -> {
                    Timber.i("Loading additional lessons lessons result: Success")
                    view?.apply {
                        updateData(it.data!!.additional.sortedBy { item -> item.date })
                        showEmpty(it.data.additional.isEmpty())
                        showErrorView(false)
                        showContent(it.data.additional.isNotEmpty())
                    }
                    analytics.logEvent(
                        "load_data",
                        "type" to "additional_lessons",
                        "items" to it.data!!.additional.size
                    )
                }
                Status.ERROR -> {
                    Timber.i("Loading additional lessons result: An exception occurred")
                    errorHandler.dispatch(it.error!!)
                }
            }
        }.afterLoading {
            view?.run {
                hideRefresh()
                showProgress(false)
                enableSwipe(true)
            }
        }.launch()
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
            updateNavigationDay(currentDate.toFormattedString("EEEE, dd.MM").capitalize())
        }
    }
}
