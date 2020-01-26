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

    fun onParentViewChangeSemester() {
        view?.run {
            showProgress(true)
            enableSwipe(false)
            showRefresh(false)
            showBarContent(false)
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
            showBarContent(false)
            showPieContent(false)
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
            showBarContent(false)
            showPieContent(false)
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
            .flatMap { semesterRepository.getCurrentSemester(it) }
            .flatMap { subjectRepository.getSubjects(it) }
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
        when (type) {
            ViewType.SEMESTER -> loadData(semesterId, subjectName, true, forceRefresh)
            ViewType.PARTIAL -> loadData(semesterId, subjectName, false, forceRefresh)
            ViewType.POINTS -> loadPointsData(semesterId, subjectName, forceRefresh)
        }
    }

    private fun loadData(semesterId: Int, subjectName: String, isSemester: Boolean, forceRefresh: Boolean = false) {
        Timber.i("Loading grade stats data started")
        disposable.add(studentRepository.getCurrentStudent()
            .flatMap { semesterRepository.getSemesters(it) }
            .flatMap { gradeStatisticsRepository.getGradesStatistics(it.first { item -> item.semesterId == semesterId }, subjectName, isSemester, forceRefresh) }
            .map { list -> list.sortedByDescending { it.grade } }
            .map { list -> list.filter { it.amount != 0 } }
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
                    showBarContent(false)
                    showPieContent(it.isNotEmpty())
                    showErrorView(false)
                    updatePieData(it, preferencesRepository.gradeColorTheme)
                }
                analytics.logEvent("load_grade_statistics", "items" to it.size, "force_refresh" to forceRefresh)
            }) {
                Timber.e("Loading grade stats result: An exception occurred")
                errorHandler.dispatch(it)
            })
    }

    private fun loadPointsData(semesterId: Int, subjectName: String, forceRefresh: Boolean = false) {
        Timber.i("Loading grade points stats data started")
        disposable.add(studentRepository.getCurrentStudent()
            .flatMap { semesterRepository.getSemesters(it) }
            .flatMapMaybe { gradeStatisticsRepository.getGradesPointsStatistics(it.first { item -> item.semesterId == semesterId }, subjectName, forceRefresh) }
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
                Timber.i("Loading grade points stats result: Success")
                view?.run {
                    showEmpty(false)
                    showPieContent(false)
                    showBarContent(true)
                    showErrorView(false)
                    updateBarData(it)
                }
                analytics.logEvent("load_grade_points_statistics", "force_refresh" to forceRefresh)
            }, {
                Timber.e("Loading grade points stats result: An exception occurred")
                errorHandler.dispatch(it)
            }, {
                Timber.d("Loading grade points stats result: No point stats found")
                view?.run {
                    showBarContent(false)
                    showEmpty(true)
                }
            })
        )
    }

    private fun showErrorViewOnError(message: String, error: Throwable) {
        view?.run {
            if ((isBarViewEmpty && currentType == ViewType.POINTS) || (isPieViewEmpty) && currentType != ViewType.POINTS) {
                lastError = error
                setErrorDetails(message)
                showErrorView(true)
                showEmpty(false)
            } else showError(message, error)
        }
    }
}
