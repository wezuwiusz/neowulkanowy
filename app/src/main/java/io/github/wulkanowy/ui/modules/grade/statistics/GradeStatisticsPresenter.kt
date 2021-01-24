package io.github.wulkanowy.ui.modules.grade.statistics

import io.github.wulkanowy.data.Status
import io.github.wulkanowy.data.db.entities.Subject
import io.github.wulkanowy.data.pojos.GradeStatisticsItem
import io.github.wulkanowy.data.repositories.GradeStatisticsRepository
import io.github.wulkanowy.data.repositories.PreferencesRepository
import io.github.wulkanowy.data.repositories.SemesterRepository
import io.github.wulkanowy.data.repositories.StudentRepository
import io.github.wulkanowy.data.repositories.SubjectRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.utils.AnalyticsHelper
import io.github.wulkanowy.utils.afterLoading
import io.github.wulkanowy.utils.flowWithResourceIn
import kotlinx.coroutines.flow.onEach
import timber.log.Timber
import javax.inject.Inject

class GradeStatisticsPresenter @Inject constructor(
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository,
    private val gradeStatisticsRepository: GradeStatisticsRepository,
    private val subjectRepository: SubjectRepository,
    private val semesterRepository: SemesterRepository,
    private val preferencesRepository: PreferencesRepository,
    private val analytics: AnalyticsHelper
) : BasePresenter<GradeStatisticsView>(errorHandler, studentRepository) {

    private var subjects = emptyList<Subject>()

    private var currentSemesterId = 0

    private var currentSubjectName: String = "Wszystkie"

    private lateinit var lastError: Throwable

    var currentType: ViewType = ViewType.PARTIAL
        private set

    fun onAttachView(view: GradeStatisticsView, type: ViewType?) {
        super.onAttachView(view)
        currentType = type ?: ViewType.PARTIAL
        view.initView()
        errorHandler.showErrorMessage = ::showErrorViewOnError
    }

    fun onParentViewLoadData(semesterId: Int, forceRefresh: Boolean) {
        currentSemesterId = semesterId
        loadSubjects()
        if (!forceRefresh) view?.showErrorView(false)
        loadDataByType(semesterId, currentSubjectName, currentType, forceRefresh)
    }

    fun onParentViewReselected() {
        view?.run {
            if (!isViewEmpty) resetView()
        }
    }

    fun onParentViewChangeSemester() {
        view?.run {
            showProgress(true)
            enableSwipe(false)
            showRefresh(false)
            showContent(false)
            showErrorView(false)
            showEmpty(false)
            clearView()
        }
        cancelJobs("load")
    }

    fun onSwipeRefresh() {
        Timber.i("Force refreshing the grade stats")
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

    fun onSubjectSelected(name: String?) {
        Timber.i("Select grade stats subject $name")
        view?.run {
            showContent(false)
            showProgress(true)
            enableSwipe(false)
            showEmpty(false)
            showErrorView(false)
            clearView()
        }
        (subjects.singleOrNull { it.name == name }?.name)?.let {
            if (it != currentSubjectName) loadDataByType(currentSemesterId, it, currentType)
        }
    }

    fun onTypeChange() {
        val type = view?.currentType ?: ViewType.POINTS
        Timber.i("Select grade stats semester: $type")
        cancelJobs("load")
        view?.run {
            showContent(false)
            showProgress(true)
            enableSwipe(false)
            showEmpty(false)
            showErrorView(false)
            clearView()
        }
        loadDataByType(currentSemesterId, currentSubjectName, type)
    }

    private fun loadSubjects() {
        flowWithResourceIn {
            val student = studentRepository.getCurrentStudent()
            val semester = semesterRepository.getCurrentSemester(student)
            subjectRepository.getSubjects(student, semester)
        }.onEach {
            when (it.status) {
                Status.LOADING -> Timber.i("Loading grade stats subjects started")
                Status.SUCCESS -> {
                    subjects = it.data!!

                    Timber.i("Loading grade stats subjects result: Success")
                    view?.run {
                        view?.updateSubjects(ArrayList(it.data.map { subject -> subject.name }))
                        showSubjects(!preferencesRepository.showAllSubjectsOnStatisticsList)
                    }
                }
                Status.ERROR -> {
                    Timber.i("Loading grade stats subjects result: An exception occurred")
                    errorHandler.dispatch(it.error!!)
                }
            }
        }.launch("subjects")
    }

    private fun loadDataByType(semesterId: Int, subjectName: String, type: ViewType, forceRefresh: Boolean = false) {
        Timber.i("Loading grade stats data started")

        currentSubjectName = if (preferencesRepository.showAllSubjectsOnStatisticsList) "Wszystkie" else subjectName
        currentType = type

        flowWithResourceIn {
            val student = studentRepository.getCurrentStudent()
            val semesters = semesterRepository.getSemesters(student)
            val semester = semesters.first { item -> item.semesterId == semesterId }

            with(gradeStatisticsRepository) {
                when (type) {
                    ViewType.PARTIAL -> getGradesPartialStatistics(student, semester, currentSubjectName, forceRefresh)
                    ViewType.SEMESTER -> getGradesSemesterStatistics(student, semester, currentSubjectName, forceRefresh)
                    ViewType.POINTS -> getGradesPointsStatistics(student, semester, currentSubjectName, forceRefresh)
                }
            }
        }.onEach {
            when (it.status) {
                Status.LOADING -> {
                    val isNoContent = it.data == null || checkIsNoContent(it.data, type)
                    if (!isNoContent) {
                        view?.run {
                            showEmpty(isNoContent)
                            showContent(!isNoContent)
                            showErrorView(false)
                            enableSwipe(true)
                            showRefresh(true)
                            showProgress(false)
                            updateData(it.data!!, preferencesRepository.gradeColorTheme, preferencesRepository.showAllSubjectsOnStatisticsList)
                            showSubjects(!preferencesRepository.showAllSubjectsOnStatisticsList)
                        }
                    }
                }
                Status.SUCCESS -> {
                    Timber.i("Loading grade stats result: Success")
                    view?.run {
                        val isNoContent = checkIsNoContent(it.data!!, type)
                        showEmpty(isNoContent)
                        showContent(!isNoContent)
                        showErrorView(false)
                        updateData(it.data, preferencesRepository.gradeColorTheme, preferencesRepository.showAllSubjectsOnStatisticsList)
                        showSubjects(!preferencesRepository.showAllSubjectsOnStatisticsList)
                    }
                    analytics.logEvent(
                        "load_data",
                        "type" to "grade_statistics",
                        "items" to it.data!!.size
                    )
                }
                Status.ERROR -> {
                    Timber.i("Loading grade stats result: An exception occurred")
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
        }.launch("load")
    }

    private fun checkIsNoContent(items: List<GradeStatisticsItem>, type: ViewType): Boolean {
        return items.isEmpty() || when (type) {
            ViewType.SEMESTER -> items.firstOrNull()?.semester?.amounts.orEmpty().sum() == 0
            ViewType.PARTIAL -> items.firstOrNull()?.partial?.classAmounts.orEmpty().sum() == 0
            ViewType.POINTS -> items.firstOrNull()?.points?.let { points ->
                points.student == .0 && points.others == .0
            } ?: false
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
}
