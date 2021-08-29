package io.github.wulkanowy.ui.modules.grade.details

import io.github.wulkanowy.data.Status
import io.github.wulkanowy.data.db.entities.Grade
import io.github.wulkanowy.data.repositories.GradeRepository
import io.github.wulkanowy.data.repositories.PreferencesRepository
import io.github.wulkanowy.data.repositories.SemesterRepository
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.ui.modules.grade.GradeAverageProvider
import io.github.wulkanowy.ui.modules.grade.GradeSortingMode.ALPHABETIC
import io.github.wulkanowy.ui.modules.grade.GradeSortingMode.DATE
import io.github.wulkanowy.ui.modules.grade.GradeSubject
import io.github.wulkanowy.utils.AnalyticsHelper
import io.github.wulkanowy.utils.afterLoading
import io.github.wulkanowy.utils.flowWithResource
import io.github.wulkanowy.utils.flowWithResourceIn
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import javax.inject.Inject

class GradeDetailsPresenter @Inject constructor(
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository,
    private val gradeRepository: GradeRepository,
    private val semesterRepository: SemesterRepository,
    private val preferencesRepository: PreferencesRepository,
    private val averageProvider: GradeAverageProvider,
    private val analytics: AnalyticsHelper
) : BasePresenter<GradeDetailsView>(errorHandler, studentRepository) {

    private var newGradesAmount: Int = 0

    private var currentSemesterId = 0

    private lateinit var lastError: Throwable

    override fun onAttachView(view: GradeDetailsView) {
        super.onAttachView(view)
        view.initView()
        errorHandler.showErrorMessage = ::showErrorViewOnError
    }

    fun onParentViewLoadData(semesterId: Int, forceRefresh: Boolean) {
        currentSemesterId = semesterId

        loadData(semesterId, forceRefresh)
        if (!forceRefresh) view?.showErrorView(false)
    }

    fun onGradeItemSelected(grade: Grade, position: Int) {
        Timber.i("Select grade item ${grade.id}, position: $position")
        view?.apply {
            showGradeDialog(grade, preferencesRepository.gradeColorTheme)
            if (!grade.isRead) {
                grade.isRead = true
                updateItem(grade, position)
                getHeaderOfItem(grade.subject).let { header ->
                    (header.value as GradeDetailsHeader).newGrades--
                    updateHeaderItem(header)
                }
                newGradesAmount--
                updateMarkAsDoneButton()
                updateGrade(grade)
            }
        }
    }

    fun onMarkAsReadSelected(): Boolean {
        flowWithResource {
            val student = studentRepository.getCurrentStudent()
            val semesters = semesterRepository.getSemesters(student)
            val semester = semesters.first { item -> item.semesterId == currentSemesterId }
            val unreadGrades = gradeRepository.getUnreadGrades(semester).first()

            Timber.i("Mark as read ${unreadGrades.size} grades")
            gradeRepository.updateGrades(unreadGrades.map { it.apply { isRead = true } })
        }.onEach {
            when (it.status) {
                Status.LOADING -> Timber.i("Select mark grades as read")
                Status.SUCCESS -> {
                    Timber.i("Mark as read result: Success")
                    loadData(currentSemesterId, false)
                }
                Status.ERROR -> {
                    Timber.i("Mark as read result: An exception occurred")
                    errorHandler.dispatch(it.error!!)
                }
            }
        }.launch("mark")
        return true
    }

    fun onSwipeRefresh() {
        Timber.i("Force refreshing the grade details")
        view?.notifyParentRefresh()
    }

    fun onRetry() {
        view?.run {
            showErrorView(false)
            showProgress(true)
        }
        view?.notifyParentRefresh()
    }

    fun onDetailsClick() {
        view?.showErrorDetailsDialog(lastError)
    }

    fun onParentViewReselected() {
        view?.run {
            if (!isViewEmpty) {
                if (preferencesRepository.isGradeExpandable) collapseAllItems()
                scrollToStart()
            }
        }
    }

    fun onParentViewChangeSemester() {
        view?.run {
            showProgress(true)
            enableSwipe(false)
            showRefresh(false)
            showContent(false)
            showEmpty(false)
            clearView()
        }
        cancelJobs("load")
    }

    fun updateMarkAsDoneButton() {
        view?.enableMarkAsDoneButton(newGradesAmount > 0)
    }

    private fun loadData(semesterId: Int, forceRefresh: Boolean) {
        Timber.i("Loading grade details data started")

        flowWithResourceIn {
            val student = studentRepository.getCurrentStudent()
            averageProvider.getGradesDetailsWithAverage(student, semesterId, forceRefresh)
        }.onEach {
            Timber.d("Loading grade details status: ${it.status}, data: ${it.data != null}")
            when (it.status) {
                Status.LOADING -> {
                    val items = createGradeItems(it.data.orEmpty())
                    if (items.isNotEmpty()) {
                        Timber.i("Loading grade details result: load cached data")
                        view?.run {
                            updateNewGradesAmount(it.data.orEmpty())
                            enableSwipe(true)
                            showRefresh(true)
                            showProgress(false)
                            showEmpty(false)
                            showContent(true)
                            updateData(
                                data = items,
                                isGradeExpandable = preferencesRepository.isGradeExpandable,
                                gradeColorTheme = preferencesRepository.gradeColorTheme
                            )
                            notifyParentDataLoaded(semesterId)
                        }
                    }
                }
                Status.SUCCESS -> {
                    Timber.i("Loading grade details result: Success")
                    updateNewGradesAmount(it.data!!)
                    updateMarkAsDoneButton()
                    val items = createGradeItems(it.data)
                    view?.run {
                        showEmpty(items.isEmpty())
                        showErrorView(false)
                        showContent(items.isNotEmpty())
                        updateData(
                            data = items,
                            isGradeExpandable = preferencesRepository.isGradeExpandable,
                            gradeColorTheme = preferencesRepository.gradeColorTheme
                        )
                    }
                    analytics.logEvent(
                        "load_data",
                        "type" to "grade_details",
                        "items" to it.data.size
                    )
                }
                Status.ERROR -> {
                    Timber.i("Loading grade details result: An exception occurred")
                    errorHandler.dispatch(it.error!!)
                }
            }
        }.afterLoading {
            view?.run {
                showRefresh(false)
                showProgress(false)
                enableSwipe(true)
                notifyParentDataLoaded(semesterId)
            }
        }.launch()
    }

    private fun updateNewGradesAmount(grades: List<GradeSubject>) {
        newGradesAmount = grades.sumOf { item ->
            item.grades.sumOf { grade -> (if (!grade.isRead) 1 else 0).toInt() }
        }
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

    private fun createGradeItems(items: List<GradeSubject>): List<GradeDetailsItem> {
        return items
            .let { gradesWithAverages ->
                if (!preferencesRepository.showSubjectsWithoutGrades) {
                    gradesWithAverages.filter { it.grades.isNotEmpty() }
                } else gradesWithAverages
            }
            .let {
                when (preferencesRepository.gradeSortingMode) {
                    DATE -> it.sortedByDescending { gradeDetailsWithAverage -> gradeDetailsWithAverage.grades.firstOrNull()?.date }
                    ALPHABETIC -> it.sortedBy { gradeDetailsWithAverage -> gradeDetailsWithAverage.subject.lowercase() }
                }
            }
            .map { (subject, average, points, _, grades) ->
                val subItems = grades
                    .sortedByDescending { it.date }
                    .map { GradeDetailsItem(it, ViewType.ITEM) }

                listOf(GradeDetailsItem(GradeDetailsHeader(
                    subject = subject,
                    average = average,
                    pointsSum = points,
                    grades = subItems
                ).apply {
                    newGrades = grades.filter { grade -> !grade.isRead }.size
                }, ViewType.HEADER)) + if (preferencesRepository.isGradeExpandable) emptyList() else subItems
            }.flatten()
    }

    private fun updateGrade(grade: Grade) {
        flowWithResource { gradeRepository.updateGrade(grade) }.onEach {
            when (it.status) {
                Status.LOADING -> Timber.i("Attempt to update grade ${grade.id}")
                Status.SUCCESS -> Timber.i("Update grade result: Success")
                Status.ERROR -> {
                    Timber.i("Update grade result: An exception occurred")
                    errorHandler.dispatch(it.error!!)
                }
            }
        }.launch("update")
    }
}
