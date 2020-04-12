package io.github.wulkanowy.ui.modules.grade.statistics

import io.github.wulkanowy.data.db.entities.Subject
import io.github.wulkanowy.data.repositories.gradestatistics.GradeStatisticsRepository
import io.github.wulkanowy.data.repositories.preferences.PreferencesRepository
import io.github.wulkanowy.data.repositories.semester.SemesterRepository
import io.github.wulkanowy.data.repositories.student.StudentRepository
import io.github.wulkanowy.data.repositories.subject.SubjectRepository
import io.github.wulkanowy.ui.base.BasePresenter
import io.github.wulkanowy.ui.base.ErrorHandler
import io.github.wulkanowy.utils.FirebaseAnalyticsHelper
import io.github.wulkanowy.utils.SchedulersProvider
import timber.log.Timber
import javax.inject.Inject

class GradeStatisticsPresenter @Inject constructor(
    schedulers: SchedulersProvider,
    errorHandler: ErrorHandler,
    studentRepository: StudentRepository,
    private val gradeStatisticsRepository: GradeStatisticsRepository,
    private val subjectRepository: SubjectRepository,
    private val semesterRepository: SemesterRepository,
    private val preferencesRepository: PreferencesRepository,
    private val analytics: FirebaseAnalyticsHelper
) : BasePresenter<GradeStatisticsView>(errorHandler, studentRepository, schedulers) {

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
        disposable.clear()
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
        disposable.clear()
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
        Timber.i("Loading grade stats subjects started")
        disposable.add(studentRepository.getCurrentStudent()
            .flatMap { student ->
                semesterRepository.getCurrentSemester(student).flatMap { semester ->
                    subjectRepository.getSubjects(student, semester)
                }
            }
            .doOnSuccess { subjects = it }
            .map { ArrayList(it.map { subject -> subject.name }) }
            .subscribeOn(schedulers.backgroundThread)
            .observeOn(schedulers.mainThread)
            .subscribe({
                Timber.i("Loading grade stats subjects result: Success")
                view?.run {
                    updateSubjects(it)
                    showSubjects(true)
                }
            }, {
                Timber.e("Loading grade stats subjects result: An exception occurred")
                errorHandler.dispatch(it)
            })
        )
    }

    private fun loadDataByType(semesterId: Int, subjectName: String, type: ViewType, forceRefresh: Boolean = false) {
        currentSubjectName = subjectName
        currentType = type
        loadData(semesterId, subjectName, type, forceRefresh)
    }

    private fun loadData(semesterId: Int, subjectName: String, type: ViewType, forceRefresh: Boolean) {
        Timber.i("Loading grade stats data started")
        disposable.add(studentRepository.getCurrentStudent()
            .flatMap { student ->
                semesterRepository.getSemesters(student).flatMap { semesters ->
                    val semester = semesters.first { item -> item.semesterId == semesterId }

                    when (type) {
                        ViewType.SEMESTER -> gradeStatisticsRepository.getGradesStatistics(student, semester, subjectName, true, forceRefresh)
                        ViewType.PARTIAL -> gradeStatisticsRepository.getGradesStatistics(student, semester, subjectName, false, forceRefresh)
                        ViewType.POINTS -> gradeStatisticsRepository.getGradesPointsStatistics(student, semester, subjectName, forceRefresh)
                    }
                }
            }
            .subscribeOn(schedulers.backgroundThread)
            .observeOn(schedulers.mainThread)
            .doFinally {
                view?.run {
                    showRefresh(false)
                    showProgress(false)
                    enableSwipe(true)
                    notifyParentDataLoaded(semesterId)
                }
            }
            .subscribe({
                Timber.i("Loading grade stats result: Success")
                view?.run {
                    showEmpty(it.isEmpty())
                    showContent(it.isNotEmpty())
                    showErrorView(false)
                    updateData(it, preferencesRepository.gradeColorTheme)
                }
                analytics.logEvent("load_grade_statistics", "items" to it.size, "force_refresh" to forceRefresh)
            }) {
                Timber.e("Loading grade stats result: An exception occurred")
                errorHandler.dispatch(it)
            })
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
