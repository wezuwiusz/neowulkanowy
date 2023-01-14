package io.github.wulkanowy.ui.modules.exam

import io.github.wulkanowy.data.*
import io.github.wulkanowy.data.db.entities.Exam
import io.github.wulkanowy.data.repositories.ExamRepository
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
import java.time.LocalDate.now
import java.time.LocalDate.ofEpochDay
import javax.inject.Inject

class ExamPresenter @Inject constructor(
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository,
    private val examRepository: ExamRepository,
    private val semesterRepository: SemesterRepository,
    private val analytics: AnalyticsHelper
) : BasePresenter<ExamView>(errorHandler, studentRepository) {

    private var baseDate: LocalDate = now().nextOrSameSchoolDay

    lateinit var currentDate: LocalDate
        private set

    private lateinit var lastError: Throwable

    fun onAttachView(view: ExamView, date: Long?) {
        super.onAttachView(view)
        view.initView()
        Timber.i("Exam view was initialized")
        errorHandler.showErrorMessage = ::showErrorViewOnError
        reloadView(ofEpochDay(date ?: baseDate.toEpochDay()))
        loadData()
        if (currentDate.isHolidays) setBaseDateOnHolidays()
    }

    fun onPreviousWeek() {
        reloadView(currentDate.minusDays(7))
        loadData()
    }

    fun onNextWeek() {
        reloadView(currentDate.plusDays(7))
        loadData()
    }

    fun onSwipeRefresh() {
        Timber.i("Force refreshing the exam")
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

    fun onExamItemSelected(exam: Exam) {
        Timber.i("Select exam item ${exam.id}")
        view?.showExamDialog(exam)
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
        flatResourceFlow {
            val student = studentRepository.getCurrentStudent()
            val semester = semesterRepository.getCurrentSemester(student)
            examRepository.getExams(
                student = student,
                semester = semester,
                start = currentDate.monday,
                end = currentDate.sunday,
                forceRefresh = forceRefresh
            )
        }
            .logResourceStatus("load exam data")
            .mapResourceData { createExamItems(it) }
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
                    "type" to "exam",
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

    private fun createExamItems(items: List<Exam>): List<ExamItem<*>> {
        return items.groupBy { it.date }.toSortedMap().map { (date, exams) ->
            listOf(ExamItem(date, ExamItem.ViewType.HEADER)) + exams.reversed().map { exam ->
                ExamItem(exam, ExamItem.ViewType.ITEM)
            }
        }.flatten()
    }

    private fun reloadView(date: LocalDate) {
        currentDate = date

        Timber.i("Reload exam view with the date ${currentDate.toFormattedString()}")
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
        Timber.i("Exam view is reselected")

        baseDate = now().nextOrSameSchoolDay

        if (currentDate != baseDate) {
            reloadView(baseDate)
            loadData()
        } else if (view?.isViewEmpty == false) {
            view?.resetView()
        }
    }
}
