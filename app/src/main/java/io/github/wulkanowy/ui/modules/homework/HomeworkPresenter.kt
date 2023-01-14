package io.github.wulkanowy.ui.modules.homework

import io.github.wulkanowy.data.*
import io.github.wulkanowy.data.db.entities.Homework
import io.github.wulkanowy.data.repositories.HomeworkRepository
import io.github.wulkanowy.data.repositories.SemesterRepository
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.utils.*
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import java.time.LocalDate
import java.time.LocalDate.ofEpochDay
import javax.inject.Inject

class HomeworkPresenter @Inject constructor(
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository,
    private val homeworkRepository: HomeworkRepository,
    private val semesterRepository: SemesterRepository,
    private val analytics: AnalyticsHelper
) : BasePresenter<HomeworkView>(errorHandler, studentRepository) {

    private var baseDate: LocalDate = LocalDate.now().nextOrSameSchoolDay

    lateinit var currentDate: LocalDate
        private set

    private lateinit var lastError: Throwable

    fun onAttachView(view: HomeworkView, date: Long?) {
        super.onAttachView(view)
        view.initView()
        Timber.i("Homework view was initialized")
        errorHandler.showErrorMessage = ::showErrorViewOnError
        reloadView(ofEpochDay(date ?: baseDate.toEpochDay()))
        loadData()
        if (currentDate.isHolidays) setBaseDateOnHolidays()
    }

    fun onPreviousDay() {
        reloadView(currentDate.minusDays(7))
        loadData()
    }

    fun onNextDay() {
        reloadView(currentDate.plusDays(7))
        loadData()
    }

    fun onSwipeRefresh() {
        Timber.i("Force refreshing the homework")
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

    fun onHomeworkItemSelected(homework: Homework) {
        Timber.i("Select homework item ${homework.id}")
        view?.showHomeworkDialog(homework)
    }

    fun onHomeworkAddButtonClicked() {
        view?.showAddHomeworkDialog()
    }

    private fun setBaseDateOnHolidays() {
        flow {
            val student = studentRepository.getCurrentStudent()
            emit(semesterRepository.getCurrentSemester(student))
        }
            .catch { Timber.i("Loading semester result: An exception occurred") }
            .onEach {
                baseDate = baseDate.getLastSchoolDayIfHoliday(it.schoolYear)
                currentDate = baseDate
                reloadNavigation()
            }
            .launch("holidays")
    }

    private fun loadData(forceRefresh: Boolean = false) {
        Timber.i("Loading homework data started")

        flatResourceFlow {
            val student = studentRepository.getCurrentStudent()
            val semester = semesterRepository.getCurrentSemester(student)
            homeworkRepository.getHomework(
                student = student,
                semester = semester,
                start = currentDate,
                end = currentDate,
                forceRefresh = forceRefresh
            )
        }
            .logResourceStatus("loading homework")
            .mapResourceData { createHomeworkItem(it) }
            .onResourceData {
                view?.run {
                    enableSwipe(true)
                    showProgress(false)
                    showErrorView(false)
                    showContent(it.isNotEmpty())
                    showEmpty(it.isEmpty())
                    updateData(it)
                }
            }
            .onResourceIntermediate { view?.showRefresh(true) }
            .onResourceSuccess {
                analytics.logEvent(
                    "load_data",
                    "type" to "homework",
                    "items" to it.size
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

    private fun createHomeworkItem(items: List<Homework>): List<HomeworkItem<*>> {
        return items.groupBy { it.date }.toSortedMap().map { (date, exams) ->
            listOf(HomeworkItem(date, HomeworkItem.ViewType.HEADER)) + exams.reversed()
                .map { exam ->
                    HomeworkItem(exam, HomeworkItem.ViewType.ITEM)
                }
        }.flatten()
    }

    private fun reloadView(date: LocalDate) {
        currentDate = date

        Timber.i("Reload homework view with the date ${currentDate.toFormattedString()}")
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
            showPreButton(!currentDate.minusDays(7).isHolidays)
            showNextButton(!currentDate.plusDays(7).isHolidays)
            updateNavigationWeek(
                "${currentDate.monday.toFormattedString("dd.MM")} - " +
                    currentDate.sunday.toFormattedString("dd.MM")
            )
        }
    }

    fun onViewReselected() {
        Timber.i("Homework view is reselected")

        baseDate = LocalDate.now().nextOrSameSchoolDay

        if (currentDate != baseDate) {
            reloadView(baseDate)
            loadData()
        } else if (view?.isViewEmpty == false) {
            view?.resetView()
        }
    }
}
